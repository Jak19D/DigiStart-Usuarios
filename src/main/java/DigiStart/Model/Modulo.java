package DigiStart.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modulos")
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do módulo é obrigatório")
    @Size(min = 5, max = 50, message = "O nome deve ter entre 5 e 50 caracteres")
    private String nome;

    @Size(max = 255)
    private String descricao;

    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Aula> aulas = new ArrayList<>();

    public Modulo() {}

    public Modulo(String nome) {
        this.nome = nome;
    }

    public Modulo(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Modulo(String nome, String descricao, Professor professor) {
        this.nome = nome;
        this.descricao = descricao;
        this.professor = professor;
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isAtivo() {
        return ativo;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }

    public List<Aula> getAulas() { return aulas; }
    public void setAulas(List<Aula> aulas) { this.aulas = aulas; }

    @Override
    public String toString() {
        return "Modulo{id=" + id + ", nome='" + nome + "'}";
    }
}