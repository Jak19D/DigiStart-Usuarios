package DigiStart.DTO.Input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AlunoRequestDTO {
    @NotBlank(message = "O nome não pode estar em branco")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Por favor, insira um e-mail válido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 16, message = "A senha deve ter no mínimo 8 caracteres")
    private String senha;

    @NotBlank(message = "O nickname é obrigatório")
    @Size(min = 3, max = 15, message = "O nickname deve ter entre 3 e 15 caracteres")
    private String nickname;

    @NotNull(message = "A data de nascimento é obrigatória")
    private String dataNascimento;
}
