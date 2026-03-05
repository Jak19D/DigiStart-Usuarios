package DigiStart.Service;

import DigiStart.Exceptions.RecursoNaoEncontrado;
import DigiStart.Exceptions.ValidacaoException;
import DigiStart.Model.Professor;
import DigiStart.Model.User;
import DigiStart.Repository.ProfessorRepository;
import DigiStart.DTO.Input.ProfessorRequestDTO;
import DigiStart.Mapper.ProfessorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UserService userService;
    private final ProfessorMapper professorMapper;

    @Autowired
    public ProfessorService(ProfessorRepository professorRepository, UserService userService, ProfessorMapper professorMapper) {
        this.professorRepository = professorRepository;
        this.userService = userService;
        this.professorMapper = professorMapper;
    }


    private void validarDominioEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|email\\.com)$";
        if (!Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE).matcher(email).matches()) {
            throw new ValidacaoException("E-mail inválido. O domínio deve ser estritamente @gmail.com ou @email.com.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.length() < 3) {
            throw new ValidacaoException("O nome completo deve ter no mínimo 3 caracteres.");
        }
        if (nome.length() > 150) {
            throw new ValidacaoException("O nome completo inserido ultrapassa o limite de 150 caracteres.");
        }
    }

    private String validarECaminhoCurriculo(MultipartFile curriculo) {
        if (curriculo == null || curriculo.isEmpty()) {
            throw new ValidacaoException("O currículo em PDF é obrigatório.");
        }
        if (!"application/pdf".equals(curriculo.getContentType())) {
            throw new ValidacaoException("Só são aceitos arquivos no formato PDF.");
        }
        return "/storage/curriculos/" + curriculo.getOriginalFilename();
    }



    @Transactional
    public Professor solicitarCadastroProfessor(
            String nome,
            String email,
            String telefone,
            String senha,
            MultipartFile curriculo) {

        if (nome == null || nome.isEmpty() || email == null || email.isEmpty() || telefone == null || telefone.isEmpty() || senha == null || senha.isEmpty()) {
            throw new ValidacaoException("Certifique-se de que todos os campos obrigatórios estejam preenchidos.");
        }
        validarNome(nome);
        validarDominioEmail(email);
        if (userService.existeEmail(email)) {
            throw new ValidacaoException("Este email já está em uso.");
        }
        userService.validarForcaSenha(senha);
        String caminhoCurriculo = validarECaminhoCurriculo(curriculo);

        try {
            User novoUser = new User(nome, email, senha, "PROFESSOR");
            User userSalvo = userService.criarNovoUser(novoUser);

            Professor novoProfessor = new Professor();
            novoProfessor.setUser(userSalvo);
            novoProfessor.setNome(nome);
            novoProfessor.setTelefone(telefone);
            novoProfessor.setCurriculoPath(caminhoCurriculo);

            System.out.println("LOG: Professor " + nome + " criado com sucesso. Persistindo entidade.");

            return professorRepository.save(novoProfessor);

        } catch (ValidacaoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidacaoException("Falha interna de persistência ao finalizar o cadastro. (Verifique o log para detalhes: " + e.getMessage() + ")");
        }
    }

    public Professor findByUserId(Long userId) {
        return professorRepository.findByUserId(userId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado para o User ID: " + userId));
    }

    public Professor salvar(Professor professor) {
        return professorRepository.save(professor);
    }

    @Transactional
    public Professor salvar(ProfessorRequestDTO dto) {
        return solicitarCadastroProfessor(
                dto.getNome(),
                dto.getEmail(),
                dto.getTelefone(),
                dto.getSenha(),
                null
        );
    }

    public List<Professor> listar() {
        return professorRepository.findAll();
    }

    public Professor buscarPorId(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado com ID: " + id));
    }

    public Professor buscarPorEmail(String email) {
        return professorRepository.findByUserEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado com email: " + email));
    }

    public Professor atualizar(Long id, Professor professorDados) {
        Professor professorExistente = buscarPorId(id);
        professorExistente.setNome(professorDados.getNome());

        return professorRepository.save(professorExistente);
    }

    @Transactional
    public void deletar(Long id) {
        professorRepository.deleteById(id);
    }

    @Transactional
    public Professor atualizarPerfil(Long professorId, String nome, String telefone, String email) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado para o ID: " + professorId));


        if (nome != null && !nome.isEmpty()) {
            validarNome(nome);
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

            validarDominioEmail(email);

            user.setEmail(email);

            userService.salvar(user);
        }

        return professorRepository.save(professor);
    }
}
