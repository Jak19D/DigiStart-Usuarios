package DigiStart.Service;

import DigiStart.DTO.Input.ProfessorRequestDTO;
import DigiStart.DTO.Output.ModuloResponseDTO;
import DigiStart.Exceptions.RecursoNaoEncontrado;
import DigiStart.Exceptions.ValidacaoException;
import DigiStart.Mapper.ProfessorMapper;
import DigiStart.Model.Professor;
import DigiStart.Model.User;
import DigiStart.Repository.ProfessorRepositoryShard1;
import DigiStart.Repository.ProfessorRepositoryShard2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;

@Service
public class ProfessorService {

    private final ProfessorRepositoryShard1 professorRepositoryShard1;
    private final ProfessorRepositoryShard2 professorRepositoryShard2;
    private final UserService userService;
    private final ShardRoutingService shardRoutingService;
    private final ValidationService validationService;
    private final ProfessorMapper professorMapper;
    private final RabbitMQService rabbitMQService;

    @Autowired
    public ProfessorService(ProfessorRepositoryShard1 professorRepositoryShard1,
                          ProfessorRepositoryShard2 professorRepositoryShard2, UserService userService, 
                          ShardRoutingService shardRoutingService, ValidationService validationService,
                          ProfessorMapper professorMapper, RabbitMQService rabbitMQService) {
        this.professorRepositoryShard1 = professorRepositoryShard1;
        this.professorRepositoryShard2 = professorRepositoryShard2;
        this.userService = userService;
        this.shardRoutingService = shardRoutingService;
        this.validationService = validationService;
        this.professorMapper = professorMapper;
        this.rabbitMQService = rabbitMQService;
    }


    public Professor solicitarCadastroProfessor(
            String nome,
            String email,
            String telefone,
            String senha) {

        if (nome == null || nome.isEmpty() || email == null || email.isEmpty() || telefone == null || telefone.isEmpty() || senha == null || senha.isEmpty()) {
            throw new ValidacaoException("Certifique-se de que todos os campos obrigatórios estejam preenchidos.");
        }
        validationService.validarNomeProfessor(nome);
        validationService.validarDominioEmail(email);
        if (userService.existeEmail(email)) {
            throw new ValidacaoException("Este email já está em uso.");
        }
        validationService.validarForcaSenha(senha);

        try {
            User novoUser = new User(nome, email, senha, "PROFESSOR");
            User userSalvo = userService.criarNovoUser(novoUser);

            Professor novoProfessor = new Professor();
            novoProfessor.setUser(userSalvo);
            novoProfessor.setNome(nome);
            novoProfessor.setTelefone(telefone);

            System.out.println("LOG: Professor " + nome + " criado com sucesso. Persistindo entidade.");

            return salvarProfessor(novoProfessor);

        } catch (ValidacaoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidacaoException("Falha interna de persistência ao finalizar o cadastro. (Verifique o log para detalhes: " + e.getMessage() + ")");
        }
    }

    @Transactional
    private Professor salvarProfessor(Professor professor) {
        int shardCorreto = userService.determinarShardDoUsuario(professor.getUser().getId());
        
        if (shardCorreto == 1) {
            return professorRepositoryShard1.save(professor);
        } else {
            return professorRepositoryShard2.save(professor);
        }
    }

    public Professor findByUserId(Long userId) {
        return professorRepositoryShard1.findByUserId(userId)
                .or(() -> professorRepositoryShard2.findByUserId(userId))
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado para o User ID: " + userId));
    }


    
    @Transactional
    public List<Professor> listar() {
        List<Professor> todosProfessores = new java.util.ArrayList<>();
        todosProfessores.addAll(professorRepositoryShard1.findAll());
        todosProfessores.addAll(professorRepositoryShard2.findAll());
        
        return todosProfessores.stream()
                .collect(java.util.stream.Collectors.toMap(
                    Professor::getId, 
                    professor -> professor, 
                    (existing, replacement) -> existing))
                .values()
                .stream()
                .toList();
    }

    public Professor buscarPorId(Long id) {
        return professorRepositoryShard1.findById(id)
                .or(() -> professorRepositoryShard2.findById(id))
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado com ID: " + id));
    }

    public Professor buscarPorEmail(String email) {
        return professorRepositoryShard1.findByUserEmail(email)
                .or(() -> professorRepositoryShard2.findByUserEmail(email))
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado com email: " + email));
    }

    
    @Transactional
    public void deletar(Long id) {
        Professor professor = buscarPorId(id);
        int shard = userService.determinarShardDoUsuario(professor.getUser().getId());
        
        if (shard == 1) {
            professorRepositoryShard1.deleteById(id);
        } else {
            professorRepositoryShard2.deleteById(id);
        }
    }

    @Transactional
    public Professor atualizarPerfil(Long professorId, String nome, String telefone, String email) {
        Professor professor = professorRepositoryShard1.findById(professorId)
                .or(() -> professorRepositoryShard2.findById(professorId))
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado para o ID: " + professorId));


        if (nome != null && !nome.isEmpty()) {
            validationService.validarNomeProfessor(nome);
            professor.setNome(nome);
        }

        if (telefone != null) {
            professor.setTelefone(telefone);
        }


        User user = professor.getUser();

        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {

            if (userService.existeEmail(email)) {
                throw new ValidacaoException("O novo e-mail já está em uso por outro usuário.");
            }

            validationService.validarDominioEmail(email);

            user.setEmail(email);

            userService.salvar(user);
        }

        int shard = userService.determinarShardDoUsuario(professor.getUser().getId());
        
        if (shard == 1) {
            return professorRepositoryShard1.save(professor);
        } else {
            return professorRepositoryShard2.save(professor);
        }
    }
    
    public void solicitarCriacaoModulo(Long professorId, String nomeModulo, String descricaoModulo) {
        Professor professor = buscarPorId(professorId);
        
        rabbitMQService.sendModuleCreationMessage(professorId, nomeModulo, descricaoModulo);
        
        System.out.println("LOG: Solicitação de criação de módulo enviada para RabbitMQ - Professor: " +
                          professor.getNome() + ", Módulo: " + nomeModulo + ", Descrição: " + descricaoModulo);
    }
    
    public List<ModuloResponseDTO> listarModulosPorProfessor(Long professorId) {
        Professor professor = buscarPorId(professorId);
        
        // Enviar solicitação para o microserviço de conteúdo
        rabbitMQService.sendListModulesRequest(professorId);
        
        System.out.println("LOG: Solicitação de listagem de módulos enviada para RabbitMQ - Professor: " + 
                          professor.getNome());
        
        return new ArrayList<>(); // Temporário - implementar resposta real
    }
}
