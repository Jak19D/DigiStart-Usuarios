package DigiStart.DTO.Input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ExercicioRequestDTO {
    @NotBlank(message = "O título do exercício é obrigatório")
    @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres")
    private String titulo;

    @NotBlank(message = "A descrição do exercício é obrigatória")
    @Size(min = 10, max = 500, message = "A descrição deve ser detalhada (10-500 caracteres)")
    private String descricao;

    @NotNull(message = "O ID da aula é obrigatório para vincular o exercício")
    private Long aulaId;
}
