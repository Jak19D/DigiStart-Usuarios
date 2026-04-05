package DigiStart.Service;

import DigiStart.DTO.Input.AlunoRequestDTO;
import DigiStart.Exceptions.RecursoNaoEncontrado;
import DigiStart.Exceptions.ValidacaoException;
import DigiStart.Model.Aluno;
import DigiStart.Model.User;
import DigiStart.Repository.AlunoRepository;
import DigiStart.Mapper.AlunoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UserService userService;
    private final AlunoMapper alunoMapper;

    @Autowired
    public AlunoService(AlunoRepository alunoRepository, UserService userService, AlunoMapper alunoMapper) {
        this.alunoRepository = alunoRepository;
        this.userService = userService;
        this.alunoMapper = alunoMapper;
    }


    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public List<Aluno> buscarPorNome(String nome) {
        List<Aluno> alunos = alunoRepository.buscarPorNomeOuEmail(nome);
        return alunos;
    }

    public Aluno buscarPorId(Long alunoId) {
        return alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado com ID: " + alunoId));
    }

    public Aluno buscarPorEmail(String email) {
        return alunoRepository.findByUserEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado com email: " + email));
    }

    public Aluno findByUserId(Long userId) {
        return alunoRepository.findByUserId(userId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado para o User ID: " + userId));
    }


    @Transactional
    public Aluno salvar(AlunoRequestDTO dto) {
        LocalDate data;

        try {
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            data = LocalDate.parse(dto.getDataNascimento(), formatador);
        } catch (Exception e) {
            throw new ValidacaoException("Formato de data inválido no JSON. Use DD/MM/AAAA.");
        }

        return cadastrarAluno(
                dto.getEmail(),
                dto.getSenha(),
                dto.getNickname(),
                data
        );
    }

    @Transactional
    public Aluno cadastrarAluno(String email, String senha, String nickname, LocalDate dataNascimento) {
        if (email == null || email.isEmpty() || nickname == null || nickname.isEmpty() || dataNascimento == null) {
            throw new ValidacaoException("Certifique-se de que todos os campos obrigatórios estejam preenchidos.");
        }

        validarNickname(nickname);
        validarDataNascimento(dataNascimento);
        validarIdadeMinima(dataNascimento);
        validarDominioEmail(email);

        if (userService.existeEmail(email)) {
            throw new ValidacaoException("Este email já está em uso.");
        }

        userService.validarForcaSenha(senha);

        User novoUser = new User(nickname, email, senha, "ALUNO");
        User userSalvo = userService.criarNovoUser(novoUser);

        Aluno novoAluno = new Aluno(userSalvo, nickname, dataNascimento);

        return alunoRepository.save(novoAluno);
    }


    @Transactional
    public Aluno editar(Long id, AlunoRequestDTO dto) {
        Aluno alunoExistente = buscarPorId(id);
        
        try {
            LocalDate data = LocalDate.parse(dto.getDataNascimento());
            
            if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
                if (!dto.getNickname().equals(alunoExistente.getNickname())) {
                    validarNickname(dto.getNickname());
                }
            }
            
            if (dto.getDataNascimento() != null && !dto.getDataNascimento().isEmpty()) {
                validarDataNascimento(data);
                validarIdadeMinima(data);
            }
            
            if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
                if (!dto.getEmail().equals(alunoExistente.getUser().getEmail())) {
                    validarDominioEmail(dto.getEmail());
                    if (!dto.getEmail().equals(alunoExistente.getUser().getEmail()) && userService.existeEmail(dto.getEmail())) {
                        throw new ValidacaoException("Este email já está em uso.");
                    }
                }
            }
            
            if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
                userService.validarForcaSenha(dto.getSenha());
            }
            
            alunoMapper.updateEntityFromDTO(dto, alunoExistente);
            
            return alunoRepository.save(alunoExistente);
            
        } catch (Exception e) {
            throw new ValidacaoException("Formato de data inválido. Use AAAA-MM-DD.");
        }
    }

    @Transactional
    public void deletar(Long id) {
        Aluno aluno = buscarPorId(id);
        alunoRepository.delete(aluno);
    }

    private void validarDominioEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|email\\.com)$";
        if (!Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE).matcher(email).matches()) {
            throw new ValidacaoException("E-mail inválido. O domínio deve ser @gmail.com ou @email.com.");
        }
    }

    private void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new ValidacaoException("A data de nascimento não pode ser uma data futura.");
        }
        if (Period.between(dataNascimento, LocalDate.now()).getYears() > 120) {
            throw new ValidacaoException("A data de nascimento é inválida (limite de 120 anos).");
        }
    }

    private void validarIdadeMinima(LocalDate dataNascimento) {
        if (Period.between(dataNascimento, LocalDate.now()).getYears() < 10) {
            throw new ValidacaoException("Você precisa ter 10 anos ou mais para se cadastrar.");
        }
    }

    private void validarNickname(String nickname) {
        if (nickname.length() < 3 || nickname.length() > 15) {
            throw new ValidacaoException("O Nickname deve ter entre 3 e 15 caracteres.");
        }
        if (alunoRepository.findByNickname(nickname).isPresent()) {
            throw new ValidacaoException("Este nickname já está cadastrado.");
        }
    }

    @Transactional
    public Aluno atualizarPerfil(Long alunoId, String nome, String email, String senha, String nickname, String dataNascimento) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado para o ID: " + alunoId));

        User user = aluno.getUser();

        if (nome != null && !nome.isEmpty()) {
            user.setNome(nome);
        }

        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            validarDominioEmail(email);
            if (userService.existeEmail(email)) {
                throw new ValidacaoException("Este email já está em uso.");
            }
            user.setEmail(email);
        }

        if (senha != null && !senha.isEmpty()) {
            userService.validarForcaSenha(senha);
            user.setSenha(senha);
        }

        if (nickname != null && !nickname.isEmpty() && !nickname.equals(aluno.getNickname())) {
            validarNickname(nickname);
            aluno.setNickname(nickname);
        }

        if (dataNascimento != null && !dataNascimento.isEmpty()) {
            try {
                DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate data = LocalDate.parse(dataNascimento, formatador);
                validarDataNascimento(data);
                validarIdadeMinima(data);
                aluno.setDataNascimento(data);
            } catch (Exception e) {
                throw new ValidacaoException("Formato de data inválido. Use DD/MM/AAAA.");
            }
        }

        userService.salvar(user);
        
        return alunoRepository.save(aluno);
    }
}
