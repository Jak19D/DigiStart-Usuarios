package DigiStart.DTO.Input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ModuloRequestDTO {
    @NotBlank(message = "O nome do módulo não pode ser vazio")
    @Size(min = 5, max = 50, message = "O nome do módulo deve ter entre 5 e 50 caracteres")
    private String nome;

    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres")
    private String descricao;
}
