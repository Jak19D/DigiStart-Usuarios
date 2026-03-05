package DigiStart.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "exercicios")
public class Exercicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título do exercício é obrigatório")
    @Size(max = 100, message = "O título pode ter até 100 caracteres")
    private String titulo;

    @Size(max = 500, message = "A descrição pode ter até 500 caracteres")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "aula_id")
    private Aula aula;

    public Exercicio() {}

    public Exercicio(String titulo, String descricao, Aula aula) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.aula = aula;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Aula getAula() { return aula; }
    public void setAula(Aula aula) { this.aula = aula; }

    @Override
    public String toString() {
        return "Exercicio{id=" + id + ", titulo='" + titulo + "'}";
    }
}
