package DigiStart.Service;

import DigiStart.Exceptions.RecursoNaoEncontrado;
import DigiStart.Exceptions.ValidacaoException;
import DigiStart.Model.Modulo;
import DigiStart.Model.Professor;
import DigiStart.Repository.ModuloRepository;
import DigiStart.Repository.ProfessorRepository;
import DigiStart.DTO.Input.ModuloRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ModuloService {

    private final ModuloRepository moduloRepository;
    private final ProfessorRepository professorRepository;

    @Autowired
    public ModuloService(ModuloRepository moduloRepository, ProfessorRepository professorRepository) {
        this.moduloRepository = moduloRepository;
        this.professorRepository = professorRepository;
    }

    private void validarNomeModulo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("O nome do módulo não pode ser vazio");
        }
        if (nome.length() < 5) {
            throw new ValidacaoException("O nome do módulo deve ter no mínimo 5 caracteres");
        }
        if (nome.length() > 50) {
            throw new ValidacaoException("O nome do módulo não pode exceder 50 caracteres");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > 255) {
            throw new ValidacaoException("A descrição não pode exceder 255 caracteres");
        }
    }

    @Transactional
    public Modulo salvar(ModuloRequestDTO input, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado com ID: " + professorId));

        validarNomeModulo(input.getNome());
        validarDescricao(input.getDescricao());

        Modulo novoModulo = new Modulo();
        novoModulo.setNome(input.getNome());
        novoModulo.setDescricao(input.getDescricao());
        novoModulo.setProfessor(professor);
        novoModulo.setAtivo(true);

        return moduloRepository.save(novoModulo);
    }

    public List<Modulo> listarTodosAtivos() {
        return moduloRepository.findByAtivoTrue();
    }

    public List<Modulo> listarPorProfessor(Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado com ID: " + professorId));

        return moduloRepository.findByProfessor(professor);
    }

    public List<Modulo> listarAtivosPorProfessor(Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Professor não encontrado com ID: " + professorId));

        return moduloRepository.findByProfessorAndAtivoTrue(professor);
    }

    public Modulo buscarPorId(Long id) {
        return moduloRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("Módulo não encontrado com ID: " + id));
    }

    @Transactional
    public Modulo atualizar(Long id, ModuloRequestDTO input) {
        Modulo moduloExistente = buscarPorId(id);

        if (input.getNome() != null && !input.getNome().trim().isEmpty()) {
            validarNomeModulo(input.getNome());
            moduloExistente.setNome(input.getNome());
        }

        if (input.getDescricao() != null) {
            validarDescricao(input.getDescricao());
            moduloExistente.setDescricao(input.getDescricao());
        }

        return moduloRepository.save(moduloExistente);
    }

    @Transactional
    public Boolean desativarModuloSeDono(Long moduloId, Long professorId) {
        Modulo modulo = moduloRepository.findById(moduloId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Módulo não encontrado"));

        if (!modulo.getProfessor().getId().equals(professorId)) {
            throw new ValidacaoException("Você não tem permissão para desativar um módulo que não é seu.");
        }

        modulo.setAtivo(false);
        moduloRepository.save(modulo);
        return true;
    }

    @Transactional
    public Modulo buscarModuloDoAluno(Long alunoId) {
        return moduloRepository.findModuloByAlunoId(alunoId).orElse(null);
    }
}
