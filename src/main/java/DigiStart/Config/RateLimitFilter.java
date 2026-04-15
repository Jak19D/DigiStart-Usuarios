package DigiStart.Config;

import DigiStart.Exceptions.RateLimitExceededException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter implements Filter {

    private final ConcurrentHashMap<String, RequestInfo> requestCache = new ConcurrentHashMap<>();
    
    private static final int MAX_REQUESTS = 10;
    private static final int BLOCK_SECONDS = 1;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.contains("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String clientIp = getClientIP(httpRequest);
            
            if (isBlocked(clientIp)) {
                throw new RateLimitExceededException("Muitas tentativas. Tente novamente em " + BLOCK_SECONDS + " segundo(s).");
            }
            
            registerRequest(clientIp);
            
            addRateLimitHeaders(httpResponse, clientIp);
            
            chain.doFilter(request, response);
            
        } catch (RateLimitExceededException e) {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                "{\"status\":429,\"error\":\"Muitas tentativas\",\"message\":\"" + e.getMessage() + "\"}"
            );
        }
    }

    private boolean isBlocked(String clientIp) {
        RequestInfo info = requestCache.get(clientIp);
        if (info == null) {
            return false;
        }
        
        if (info.blockedUntil != null && LocalDateTime.now().isBefore(info.blockedUntil)) {
            return true;
        }
        
        if (info.blockedUntil != null && LocalDateTime.now().isAfter(info.blockedUntil)) {
            requestCache.remove(clientIp);
            return false;
        }
        
        return false;
    }

    private void registerRequest(String clientIp) {
        RequestInfo info = requestCache.computeIfAbsent(clientIp, k -> new RequestInfo());
        
        if (info.lastRequest != null &&
            ChronoUnit.SECONDS.between(info.lastRequest, LocalDateTime.now()) >= BLOCK_SECONDS) {
            info.requestCount.set(0);
        }
        
        int currentCount = info.requestCount.incrementAndGet();
        info.lastRequest = LocalDateTime.now();
        
        if (currentCount >= MAX_REQUESTS) {
            info.blockedUntil = LocalDateTime.now().plusSeconds(BLOCK_SECONDS);
        }
    }

    private void addRateLimitHeaders(HttpServletResponse response, String clientIp) {
        RequestInfo info = requestCache.get(clientIp);
        if (info != null) {
            int remaining = Math.max(0, MAX_REQUESTS - info.requestCount.get());
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(remaining));
            response.setHeader("X-Rate-Limit-Max", String.valueOf(MAX_REQUESTS));
            response.setHeader("X-Rate-Limit-Block-Seconds", String.valueOf(BLOCK_SECONDS));
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
        requestCache.clear();
    }

    private static class RequestInfo {
        AtomicInteger requestCount = new AtomicInteger(0);
        LocalDateTime lastRequest;
        LocalDateTime blockedUntil;
    }
}
