package DigiStart.Service;

import DigiStart.Exceptions.ValidacaoException;
import DigiStart.Model.User;
import DigiStart.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validarForcaSenha(String senha) {
        if (senha == null || senha.isEmpty()) {
            throw new ValidacaoException("A senha é obrigatória.");
        }

        if (senha.length() < 8 || senha.length() > 16) {
            throw new ValidacaoException("A senha deve ter de 8 a 16 caracteres.");
        }

        if (!senha.matches(".*\\d.*")) {
            throw new ValidacaoException("A senha deve ter pelo menos 1 número.");
        }

        String specialChars = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";
        if (!senha.matches(specialChars)) {
            throw new ValidacaoException("A senha deve ter pelo menos 1 caractere especial.");
        }

        if (!senha.matches(".*[A-Z].*")) {
            throw new ValidacaoException("A senha deve ter pelo menos 1 letra maiúscula.");
        }
    }

    public boolean existeEmail(String email) {
        return repository.existsByEmail(email);
    }

    // --- MÉTODOS DE CRUD ---

    @Transactional
    public User salvar(User user) {
        return repository.save(user);
    }

    @Transactional
    public User criarNovoUser(User novoUser) {
        String senhaPura = novoUser.getSenha();
        String senhaCriptografada = passwordEncoder.encode(senhaPura);
        novoUser.setSenha(senhaCriptografada);

        return repository.save(novoUser);
    }

    public User autenticarUsuario(String email, String senhaPura) {
        Optional<User> optionalUser = repository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isAtivo()) {
                throw new ValidacaoException("Usuário inativo. Contacte o administrador.");
            }
            if (passwordEncoder.matches(senhaPura, user.getSenha())) {
                return user;
            }
        }
        return null;
    }

    public List<User> listar() {
        return repository.findAll();
    }

    public User atualizar(Long id, User user) {
        User existente = repository.findById(id).orElseThrow();
        existente.setEmail(user.getEmail());
        existente.setTipo(user.getTipo());
        return repository.save(existente);
    }

    @Transactional
    public void inativar(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new ValidacaoException("Usuário não encontrado."));
        user.setAtivo(false);
        repository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info(">>> [UserService] Tentando carregar usuário com email: {}", email);

        Optional<User> userOptional = repository.findByEmail(email);

        if (userOptional.isPresent()) {
            log.info(">>> [UserService] Usuário encontrado: {}", email);
            return userOptional.get();
        } else {
            log.warn(">>> [UserService] Usuário NÃO encontrado com email: {}", email);
            throw new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email);
        }
    }
}
