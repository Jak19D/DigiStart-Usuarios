package DigiStart.Service;

import DigiStart.Exceptions.RecursoNaoEncontrado;
import DigiStart.Model.Aluno;
import DigiStart.Model.Aula;
import DigiStart.Model.ProgressoAula;
import DigiStart.Model.ProgressoAula.StatusAula;
import DigiStart.Repository.ProgressoAulaRepository;
import DigiStart.Repository.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressoAulaService {

    private final ProgressoAulaRepository progressoAulaRepository;

    private final AulaRepository aulaRepository;

    private final AlunoService alunoService;

    @Autowired
    public ProgressoAulaService(ProgressoAulaRepository progressoAulaRepository,
                                AulaRepository aulaRepository, // <<-- CONSTRUTOR ATUALIZADO
                                AlunoService alunoService) {
        this.progressoAulaRepository = progressoAulaRepository;
        this.aulaRepository = aulaRepository; // <<-- ATUALIZAÇÃO
        this.alunoService = alunoService;
    }

    @Transactional
    public ProgressoAula registrarInicio(Long alunoId, Long aulaId) {
        Aluno aluno = alunoService.buscarPorId(alunoId);

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Aula não encontrada."));

        Optional<ProgressoAula> progressoOpt = progressoAulaRepository.findByAlunoIdAndAulaId(alunoId, aulaId);

        ProgressoAula progresso;
        if (progressoOpt.isPresent()) {
            progresso = progressoOpt.get();
        } else {
            progresso = new ProgressoAula();
            progresso.setAluno(aluno);
            progresso.setAula(aula);
        }

        if (progresso.getStatus() == StatusAula.PENDENTE) {
            progresso.setStatus(StatusAula.EM_ANDAMENTO);
        }

        return progressoAulaRepository.save(progresso);
    }

    @Transactional
    public ProgressoAula marcarConcluida(Long alunoId, Long aulaId) {
        ProgressoAula progresso = progressoAulaRepository.findByAlunoIdAndAulaId(alunoId, aulaId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Progresso não encontrado."));

        if (progresso.getStatus() != StatusAula.CONCLUIDA) {
            progresso.setStatus(StatusAula.CONCLUIDA);
            progresso.setDataConclusao(LocalDateTime.now());
            return progressoAulaRepository.save(progresso);
        }
        return progresso;
    }

    public boolean verificarPreRequisito(Long alunoId, Long aulaIdAtual) {
        Aula aulaAtual = aulaRepository.findById(aulaIdAtual)
                .orElseThrow(() -> new RecursoNaoEncontrado("Aula não encontrada."));

        List<Aula> aulasDoModulo = aulaRepository.findByModuloIdOrderByOrdemAsc(aulaAtual.getModulo().getId());

        int indiceAulaAtual = aulasDoModulo.indexOf(aulaAtual);

        if (indiceAulaAtual <= 0) {
            return true;
        }

        for (int i = 0; i < indiceAulaAtual; i++) {
            Aula aulaAnterior = aulasDoModulo.get(i);

            Optional<ProgressoAula> progressoAnteriorOpt = progressoAulaRepository.findByAlunoIdAndAulaId(alunoId, aulaAnterior.getId());

            if (progressoAnteriorOpt.isEmpty() || progressoAnteriorOpt.get().getStatus() != StatusAula.CONCLUIDA) {
                return false;
            }
        }

        return true;
    }

    public ProgressoAula buscarProgresso(Long alunoId, Long aulaId) {
        return progressoAulaRepository.findByAlunoIdAndAulaId(alunoId, aulaId)
                .orElse(null);
    }
}