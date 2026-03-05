package DigiStart.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professores")
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @NotBlank(message = "O nome do professor é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
    private String nome;

    private String telefone;

    private String curriculoPath;

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Modulo> modulos = new ArrayList<>();

    public Professor() {}

    public Professor(User user, String nome, String telefone, String curriculoPath) {
        this.user = user;
        this.nome = nome;
        this.telefone = telefone;
        this.curriculoPath = curriculoPath;
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCurriculoPath() { return curriculoPath; }
    public void setCurriculoPath(String curriculoPath) { this.curriculoPath = curriculoPath; }

    public List<Modulo> getModulos() { return modulos; }
    public void setModulos(List<Modulo> modulos) { this.modulos = modulos; }

    @Override
    public String toString() {
        return "Professor{id=" + id + ", nome='" + nome + "'}";
    }
}