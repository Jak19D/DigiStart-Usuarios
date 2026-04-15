package DigiStart.Service;

import DigiStart.DTO.Input.UserRequestDTO;
import DigiStart.Exceptions.ValidacaoException;
import DigiStart.Mapper.UserMapper;
import DigiStart.Model.User;
import DigiStart.Repository.UserRepositoryShard1;
import DigiStart.Repository.UserRepositoryShard2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepositoryShard1 userRepositoryShard1;
    private final UserRepositoryShard2 userRepositoryShard2;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate shard1JdbcTemplate;
    private final JdbcTemplate shard2JdbcTemplate;
    private final RabbitMQService rabbitMQService;
    private final ShardRoutingService shardRoutingService;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepositoryShard1 userRepositoryShard1, 
                     UserRepositoryShard2 userRepositoryShard2, PasswordEncoder passwordEncoder,
                      @Qualifier("shard1JdbcTemplate") JdbcTemplate shard1JdbcTemplate,
                      @Qualifier("shard2JdbcTemplate") JdbcTemplate shard2JdbcTemplate,
                      RabbitMQService rabbitMQService, ShardRoutingService shardRoutingService,
                      UserMapper userMapper) {
        this.userRepositoryShard1 = userRepositoryShard1;
        this.userRepositoryShard2 = userRepositoryShard2;
        this.passwordEncoder = passwordEncoder;
        this.shard1JdbcTemplate = shard1JdbcTemplate;
        this.shard2JdbcTemplate = shard2JdbcTemplate;
        this.rabbitMQService = rabbitMQService;
        this.shardRoutingService = shardRoutingService;
        this.userMapper = userMapper;
    }

    private JdbcTemplate getShardForId(Long id) {
        return (id % 2 == 0) ? shard1JdbcTemplate : shard2JdbcTemplate;
    }

    private JdbcTemplate getShardForNewUser(String email) {
        int shard = shardRoutingService.determinarShardPorHash(email);
        return shard == 1 ? shard1JdbcTemplate : shard2JdbcTemplate;
    }

    private RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setNome(rs.getString("nome"));
        user.setEmail(rs.getString("email"));
        user.setSenha(rs.getString("senha"));
        user.setTipo(rs.getString("tipo"));
        user.setAtivo(rs.getBoolean("ativo"));
        return user;
    };

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
        String sql = "SELECT COUNT(*) FROM tb_user WHERE email = ?";
        Long count1 = shard1JdbcTemplate.queryForObject(sql, Long.class, email);
        Long count2 = shard2JdbcTemplate.queryForObject(sql, Long.class, email);
        return (count1 != null && count1 > 0) || (count2 != null && count2 > 0);
    }

    // --- MÉTODOS DE CRUD ---

    @Transactional
    public User salvar(User user) {
        if (user.getId() == null) {
            JdbcTemplate targetShard = getShardForNewUser(user.getEmail());
            String sql = "INSERT INTO tb_user (nome, email, senha, tipo, ativo) VALUES (?, ?, ?, ?, ?) RETURNING id";
            Long id = targetShard.queryForObject(sql, Long.class,
                    user.getNome(), user.getEmail(), user.getSenha(), user.getTipo(), user.isAtivo());
            user.setId(id);
        } else {
            String sql = "UPDATE tb_user SET nome = ?, email = ?, senha = ?, tipo = ?, ativo = ? WHERE id = ?";
            getShardForId(user.getId()).update(sql, user.getNome(), user.getEmail(), 
                    user.getSenha(), user.getTipo(), user.isAtivo(), user.getId());
        }
        return user;
    }


    @Transactional
    public User criarNovoUser(User novoUser) {
        String senhaPura = novoUser.getSenha();
        String senhaCriptografada = passwordEncoder.encode(senhaPura);
        novoUser.setSenha(senhaCriptografada);

        User userSalvo = salvar(novoUser);
        
        rabbitMQService.sendUserCreatedMessage(userSalvo);
        rabbitMQService.sendContentSyncMessage(userSalvo.getId(), "CREATED");
        
        return userSalvo;
    }

    public User autenticarUsuario(String email, String senhaPura) {
        String sql = "SELECT * FROM tb_user WHERE email = ?";
        List<User> users1 = shard1JdbcTemplate.query(sql, userRowMapper, email);
        List<User> users2 = shard2JdbcTemplate.query(sql, userRowMapper, email);
        
        List<User> allUsers = new java.util.ArrayList<>();
        allUsers.addAll(users1);
        allUsers.addAll(users2);

        for (User user : allUsers) {
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
        String sql = "SELECT * FROM tb_user";
        List<User> users1 = shard1JdbcTemplate.query(sql, userRowMapper);
        List<User> users2 = shard2JdbcTemplate.query(sql, userRowMapper);
        
        List<User> allUsers = new java.util.ArrayList<>();
        allUsers.addAll(users1);
        allUsers.addAll(users2);
        return allUsers;
    }

    public User atualizar(Long id, User user) {
        User existente = findById(id);
        if (existente == null) {
            throw new ValidacaoException("Usuário não encontrado.");
        }
        existente.setEmail(user.getEmail());
        existente.setTipo(user.getTipo());
        return salvar(existente);
    }

    @Transactional
    public void inativar(Long id) {
        User user = findById(id);
        if (user == null) {
            throw new ValidacaoException("Usuário não encontrado.");
        }
        user.setAtivo(false);
        salvar(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info(">>> [UserService] Tentando carregar usuário com email: {}", email);

        String sql = "SELECT * FROM tb_user WHERE email = ?";
        List<User> users1 = shard1JdbcTemplate.query(sql, userRowMapper, email);
        List<User> users2 = shard2JdbcTemplate.query(sql, userRowMapper, email);
        
        List<User> allUsers = new java.util.ArrayList<>();
        allUsers.addAll(users1);
        allUsers.addAll(users2);

        if (!allUsers.isEmpty()) {
            log.info(">>> [UserService] Usuário encontrado: {}", email);
            return allUsers.get(0);
        } else {
            log.warn(">>> [UserService] Usuário NÃO encontrado com email: {}", email);
            throw new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email);
        }
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM tb_user WHERE id = ?";
        List<User> users = getShardForId(id).query(sql, userRowMapper, id);
        return users.isEmpty() ? null : users.get(0);
    }
}