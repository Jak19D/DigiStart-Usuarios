package DigiStart.Service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ShardRoutingService {
    
    private final AtomicLong counter = new AtomicLong(0);
    
    public int determinarShard() {
        return (int) (counter.incrementAndGet() % 2) + 1;
    }
    
    public int determinarShardPorHash(String valor) {
        if (valor == null) {
            return determinarShard();
        }
        return Math.abs(valor.hashCode()) % 2 + 1;
    }
    
    public int determinarShardPorId(Long id) {
        if (id == null) {
            return determinarShard();
        }
        return (id % 2 == 0) ? 2 : 1;
    }
}
