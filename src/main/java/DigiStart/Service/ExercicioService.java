package DigiStart.Service;

import DigiStart.Exceptions.RecursoNaoEncontrado;
import DigiStart.Exceptions.RegraNegocioException;
import DigiStart.Exceptions.ValidacaoException;
import DigiStart.Model.Aula;
import DigiStart.Model.Exercicio;
import DigiStart.Model.Professor;
import DigiStart.Repository.ExercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;

    @Autowired
    public ExercicioService(ExercicioRepository exercicioRepository) {
        this.exercicioRepository = exercicioRepository;
    }

    public Exercicio salvar(Exercicio exercicio) {
        return exercicioRepository.save(exercicio);
    }

    public List<Exercicio> listarPorAula(Long aulaId) {
        return new ArrayList<>(exercicioRepository.findByAulaId(aulaId));
    }

    @Transactional
    public Exercicio criarNovoExercicio(Aula aula, String titulo, String descricao) {
        if (titulo == null || titulo.isEmpty() || descricao == null || descricao.isEmpty()) {
            throw new ValidacaoException("Título e descrição do exercício são obrigatórios.");
        }

        Exercicio novoExercicio = new Exercicio(titulo, descricao, aula);
        return exercicioRepository.save(novoExercicio);
    }

    @Transactional
    public Exercicio atualizar(Long exercicioId, String novoTitulo, String novaDescricao, Professor professorProprietario) {
        Exercicio exercicio = exercicioRepository.findById(exercicioId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Exercício não encontrado."));

        if (!exercicio.getAula().getModulo().getProfessor().equals(professorProprietario)) {
            throw new RegraNegocioException("Você não tem permissão para editar este exercício.");
        }

        if (novoTitulo != null && !novoTitulo.isEmpty()) {
            exercicio.setTitulo(novoTitulo);
        }
        if (novaDescricao != null && !novaDescricao.isEmpty()) {
            exercicio.setDescricao(novaDescricao);
        }

        return exercicioRepository.save(exercicio);
    }

    @Transactional
    public void deletar(Long exercicioId, Professor professorProprietario) {
        Exercicio exercicio = exercicioRepository.findById(exercicioId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Exercício não encontrado."));

        if (!exercicio.getAula().getModulo().getProfessor().equals(professorProprietario)) {
            throw new RegraNegocioException("Você não tem permissão para deletar este exercício.");
        }

        exercicioRepository.delete(exercicio);
    }

}