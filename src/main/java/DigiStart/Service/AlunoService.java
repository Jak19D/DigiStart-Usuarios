package DigiStart.Service;

import DigiStart.DTO.Input.AlunoRequestDTO;
import DigiStart.Exceptions.RecursoNaoEncontrado;
import DigiStart.Exceptions.ValidacaoException;
import DigiStart.Mapper.AlunoMapper;
import DigiStart.Model.Aluno;
import DigiStart.Model.User;
import DigiStart.Repository.AlunoRepositoryShard1;
import DigiStart.Repository.AlunoRepositoryShard2;
import DigiStart.Service.ShardRoutingService;
import DigiStart.Service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepositoryShard1 alunoRepositoryShard1;
    private final AlunoRepositoryShard2 alunoRepositoryShard2;
    private final UserService userService;
    private final ShardRoutingService shardRoutingService;
    private final ValidationService validationService;
    private final AlunoMapper alunoMapper;

    @Autowired
    public AlunoService(AlunoRepositoryShard1 alunoRepositoryShard1, 
                       AlunoRepositoryShard2 alunoRepositoryShard2, UserService userService, 
                       ShardRoutingService shardRoutingService, ValidationService validationService,
                       AlunoMapper alunoMapper) {
        this.alunoRepositoryShard1 = alunoRepositoryShard1;
        this.alunoRepositoryShard2 = alunoRepositoryShard2;
        this.userService = userService;
        this.shardRoutingService = shardRoutingService;
        this.validationService = validationService;
        this.alunoMapper = alunoMapper;
    }


    public List<Aluno> listarTodos() {
        List<Aluno> todosAlunos = new java.util.ArrayList<>();
        todosAlunos.addAll(alunoRepositoryShard1.findAll());
        todosAlunos.addAll(alunoRepositoryShard2.findAll());
        
        return todosAlunos.stream()
                .collect(java.util.stream.Collectors.toMap(
                    Aluno::getId, 
                    aluno -> aluno, 
                    (existing, replacement) -> existing))
                .values()
                .stream()
                .toList();
    }

    public List<Aluno> buscarPorNome(String nome) {
        List<Aluno> alunos = new java.util.ArrayList<>();
        alunos.addAll(alunoRepositoryShard1.buscarPorNomeOuEmail(nome));
        alunos.addAll(alunoRepositoryShard2.buscarPorNomeOuEmail(nome));
        
        return alunos.stream()
                .collect(java.util.stream.Collectors.toMap(
                    Aluno::getId, 
                    aluno -> aluno, 
                    (existing, replacement) -> existing))
                .values()
                .stream()
                .toList();
    }

    public Aluno buscarPorId(Long alunoId) {
        return alunoRepositoryShard1.findById(alunoId)
                .or(() -> alunoRepositoryShard2.findById(alunoId))
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado com ID: " + alunoId));
    }

    public Aluno buscarPorEmail(String email) {
        return alunoRepositoryShard1.findByUserEmail(email)
                .or(() -> alunoRepositoryShard2.findByUserEmail(email))
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado com email: " + email));
    }

    public Aluno findByUserId(Long userId) {
        return alunoRepositoryShard1.findByUserId(userId)
                .or(() -> alunoRepositoryShard2.findByUserId(userId))
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado para o User ID: " + userId));
    }


    
    @Transactional
    public Aluno cadastrarAluno(String email, String senha, String nickname, LocalDate dataNascimento) {
        if (email == null || email.isEmpty() || nickname == null || nickname.isEmpty() || dataNascimento == null) {
            throw new ValidacaoException("Certifique-se de que todos os campos obrigatórios estejam preenchidos.");
        }

        validationService.validarNickname(nickname);
        validationService.validarDataNascimento(dataNascimento);
        validationService.validarIdadeMinimaAluno(dataNascimento);
        validationService.validarDominioEmail(email);

        if (userService.existeEmail(email)) {
            throw new ValidacaoException("Este email já está em uso.");
        }

        validationService.validarForcaSenha(senha);

        User novoUser = new User(nickname, email, senha, "ALUNO");
        User userSalvo = userService.criarNovoUser(novoUser);

        Aluno novoAluno = new Aluno(userSalvo, nickname, dataNascimento);

        int shardCorreto = userService.determinarShardDoUsuario(userSalvo.getId());
        
        if (shardCorreto == 1) {
            return alunoRepositoryShard1.save(novoAluno);
        } else {
            return alunoRepositoryShard2.save(novoAluno);
        }
    }

    
    @Transactional
    public void deletar(Long id) {
        Aluno aluno = buscarPorId(id);
        int shard = userService.determinarShardDoUsuario(aluno.getUser().getId());
        
        if (shard == 1) {
            alunoRepositoryShard1.delete(aluno);
        } else {
            alunoRepositoryShard2.delete(aluno);
        }
    }

    private void validarNickname(String nickname) {
        if (alunoRepositoryShard1.findByNickname(nickname).isPresent() ||
            alunoRepositoryShard2.findByNickname(nickname).isPresent()) {
            throw new ValidacaoException("Este nickname já está cadastrado.");
        }
    }

    @Transactional
    public Aluno atualizarPerfil(Long alunoId, String nome, String email, String senha, String nickname, String dataNascimento) {
        Aluno aluno = alunoRepositoryShard1.findById(alunoId)
                .or(() -> alunoRepositoryShard2.findById(alunoId))
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado para o ID: " + alunoId));

        User user = aluno.getUser();

        if (nome != null && !nome.isEmpty()) {
            user.setNome(nome);
        }

        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            validationService.validarDominioEmail(email);
            if (userService.existeEmail(email)) {
                throw new ValidacaoException("Este email já está em uso.");
            }
            user.setEmail(email);
        }

        if (senha != null && !senha.isEmpty()) {
            validationService.validarForcaSenha(senha);
            user.setSenha(senha);
        }

        if (nickname != null && !nickname.isEmpty() && !nickname.equals(aluno.getNickname())) {
            validationService.validarNickname(nickname);
            validarNickname(nickname);
            aluno.setNickname(nickname);
        }

        if (dataNascimento != null && !dataNascimento.isEmpty()) {
            try {
                DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate data = LocalDate.parse(dataNascimento, formatador);
                validationService.validarDataNascimento(data);
                validationService.validarIdadeMinimaAluno(data);
                aluno.setDataNascimento(data);
            } catch (Exception e) {
                throw new ValidacaoException("Formato de data inválido. Use DD/MM/AAAA.");
            }
        }

        userService.salvar(user);
        
        int shard = userService.determinarShardDoUsuario(aluno.getUser().getId());
        
        if (shard == 1) {
            return alunoRepositoryShard1.save(aluno);
        } else {
            return alunoRepositoryShard2.save(aluno);
        }
    }
}
