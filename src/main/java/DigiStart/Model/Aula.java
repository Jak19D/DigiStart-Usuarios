package DigiStart.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aulas")
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(min = 10, max = 50, message = "O título deve ter entre 10 e 50 caracteres")
    private String titulo;

    @Size(max = 255)
    private String descricao;

    @NotBlank(message = "O vídeo é obrigatório")
    private String videoUrl;

    @Column(nullable = false)
    private int ordem;

    private boolean ativa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id")
    private Modulo modulo;

    @OneToMany(mappedBy = "aula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exercicio> exercicios = new ArrayList<>();

    public Aula() {}

    public Aula(String titulo, String descricao, String videoUrl, Modulo modulo, int ordem) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.videoUrl = videoUrl;
        this.modulo = modulo;
        this.ordem = ordem;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public Modulo getModulo() { return modulo; }
    public void setModulo(Modulo modulo) { this.modulo = modulo; }

    public List<Exercicio> getExercicios() { return exercicios; }
    public void setExercicios(List<Exercicio> exercicios) { this.exercicios = exercicios; }

    @Override
    public String toString() {
        return "Aula{id=" + id + ", titulo='" + titulo + "'}";
    }
}