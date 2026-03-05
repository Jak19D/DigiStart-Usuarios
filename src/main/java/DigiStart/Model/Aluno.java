package DigiStart.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;


    @Column(unique = true, length = 15)
    @Size(min = 3, max = 15, message = "O Nickname deve ter entre 3 e 15 caracteres.")
    @NotBlank(message = "O Nickname é obrigatório.")
    private String nickname;

    @Column(nullable = false)
    private LocalDate dataNascimento;


    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProgressoAula> progressoAulas = new ArrayList<>();

    public Aluno() {}

    public Aluno(User user, String nickname, LocalDate dataNascimento) {
        this.user = user;
        this.nickname = nickname;
        this.dataNascimento = dataNascimento;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public List<ProgressoAula> getProgressoAulas() { return progressoAulas; }
    public void setProgressoAulas(List<ProgressoAula> progressoAulas) { this.progressoAulas = progressoAulas; }
}