package DigiStart.Service;

import DigiStart.Model.User;
import DigiStart.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info(">>> [UserDetailsServiceImpl] Tentando carregar usuário com email: {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            log.info(">>> [UserDetailsServiceImpl] Usuário encontrado: {}", email);
            return userOptional.get();
        } else {
            log.warn(">>> [UserDetailsServiceImpl] Usuário NÃO encontrado com email: {}", email);
            throw new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email);
        }
    }
}