package DigiStart.Service;

import DigiStart.Exceptions.RegraNegocioException;
import DigiStart.Exceptions.RecursoNaoEncontrado;
import DigiStart.Exceptions.ValidacaoException;
import DigiStart.Model.Aula;
import DigiStart.Model.Professor;
import DigiStart.Model.Exercicio;
import DigiStart.Repository.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final ProgressoAulaService progressoAulaService;
    private final ExercicioService exercicioService;

    @Autowired
    public AulaService(AulaRepository aulaRepository, ProgressoAulaService progressoAulaService, ExercicioService exercicioService) {
        this.aulaRepository = aulaRepository;
        this.progressoAulaService = progressoAulaService;
        this.exercicioService = exercicioService;
    }


    private void validarVideo(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            throw new ValidacaoException("O vídeo da aula é obrigatório.");
        }
        if (!videoUrl.toLowerCase().endsWith(".mp4")) {
            throw new ValidacaoException("Só são aceitos arquivos no formato MP4.");
        }
    }

    @Transactional
    public Aula adicionarAula(Aula novaAula) {
        if (novaAula.getTitulo() == null || novaAula.getTitulo().length() < 10 || novaAula.getTitulo().length() > 50) {
            throw new ValidacaoException("Título da aula deve ter entre 10 e 50 caracteres.");
        }
        validarVideo(novaAula.getVideoUrl());

        int proximaOrdem = aulaRepository.countByModulo(novaAula.getModulo()) + 1;
        novaAula.setOrdem(proximaOrdem);

        novaAula.setAtiva(true);

        return aulaRepository.save(novaAula);
    }

    @Transactional
    public Aula atualizar(Long id, Aula aulaAtualizada, Professor professorProprietario) {
        return aulaRepository.findById(id)
                .map(aulaExistente -> {
                    if (!aulaExistente.getModulo().getProfessor().equals(professorProprietario)) {
                        throw new RegraNegocioException("Você não tem permissão para editar esta aula.");
                    }

                    if (aulaAtualizada.getTitulo() != null) {
                        if (aulaAtualizada.getTitulo().length() < 10 || aulaAtualizada.getTitulo().length() > 50) {
                            throw new ValidacaoException("Novo título da aula deve ter entre 10 e 50 caracteres."); // RF006
                        }
                        aulaExistente.setTitulo(aulaAtualizada.getTitulo());
                    }

                    if (aulaAtualizada.getDescricao() != null) {
                        if (aulaAtualizada.getDescricao().length() < 10 || aulaAtualizada.getDescricao().length() > 255) {
                            throw new ValidacaoException("A descrição deve ter entre 10 e 255 caracteres."); // RF006
                        }
                        aulaExistente.setDescricao(aulaAtualizada.getDescricao());
                    }

                    if (aulaAtualizada.getVideoUrl() != null) {
                        validarVideo(aulaAtualizada.getVideoUrl());
                        aulaExistente.setVideoUrl(aulaAtualizada.getVideoUrl());
                    }

                    return aulaRepository.save(aulaExistente);
                })
                .orElseThrow(() -> new RecursoNaoEncontrado("Aula não encontrada com o id: " + id));
    }

    @Transactional
    public void deletar(Long id, Professor professorProprietario) {
        Aula aula = buscarPorId(id);

        if (!aula.getModulo().getProfessor().equals(professorProprietario)) {
            throw new RegraNegocioException("Você não tem permissão para deletar esta aula.");
        }

        aula.setAtiva(false);
        aulaRepository.save(aula);
    }

    @Transactional
    public Boolean desativarAula(Long id) {
        Aula aula = buscarPorId(id);
        aula.setAtiva(false);
        aulaRepository.save(aula);
        return true;
    }


    @Transactional
    public Aula acessarAula(Long alunoId, Long aulaId) {
        Aula aula = buscarPorId(aulaId);

        if (!aula.getModulo().isAtivo() || !aula.isAtiva()) {
            throw new RegraNegocioException("Este conteúdo está indisponível no momento.");
        }

        if (!progressoAulaService.verificarPreRequisito(alunoId, aulaId)) {
            throw new RegraNegocioException("Violação de Regra de Negócio (RN02/08): Aula bloqueada. Conclua as aulas anteriores para liberar o acesso.");
        }

        progressoAulaService.registrarInicio(alunoId, aulaId);

        return aula;
    }

    @Transactional
    public void marcarComoConcluida(Long alunoId, Long aulaId) {
        progressoAulaService.marcarConcluida(alunoId, aulaId);

        List<Exercicio> exercicios = exercicioService.listarPorAula(aulaId);

        if (!exercicios.isEmpty()) {
            System.out.println("LOG: Aula concluída, mas possui exercícios. RN 11 Ativada.");
        }

    }



    public List<Aula> listar() {
        return aulaRepository.findAll();
    }

    public List<Aula> listarPorModulo(Long moduloId) {
        return aulaRepository.findByModuloIdOrderByOrdemAsc(moduloId);
    }

    public Aula buscarPorId(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("Aula não encontrada com o id: " + id));
    }
}