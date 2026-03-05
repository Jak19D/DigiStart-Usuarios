package DigiStart.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progresso_aulas")
public class ProgressoAula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAula status = StatusAula.PENDENTE;

    private Long tempoAssistidoSegundos = 0L;

    private LocalDateTime dataConclusao;

    public ProgressoAula() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Aula getAula() { return aula; }
    public void setAula(Aula aula) { this.aula = aula; }

    public StatusAula getStatus() { return status; }
    public void setStatus(StatusAula status) { this.status = status; }

    public Long getTempoAssistidoSegundos() { return tempoAssistidoSegundos; }
    public void setTempoAssistidoSegundos(Long tempoAssistidoSegundos) { this.tempoAssistidoSegundos = tempoAssistidoSegundos; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    // Enum de Status
    public enum StatusAula {
        PENDENTE,
        EM_ANDAMENTO,
        CONCLUIDA
    }
}