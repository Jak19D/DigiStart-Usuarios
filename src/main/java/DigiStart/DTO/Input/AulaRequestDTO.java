package DigiStart.DTO.Input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AulaRequestDTO {
    @NotBlank(message = "O título da aula é obrigatório")
    @Size(min = 10, max = 50, message = "O título deve ser claro (10-50 caracteres)")
    private String titulo;

    private String descricao;

    @NotBlank(message = "A URL do vídeo é obrigatória")
    private String videoUrl;

    @NotNull(message = "A ordem da aula deve ser definida")
    private Integer ordem;

    @NotNull(message = "O ID do módulo é obrigatório")
    private Long moduloId;


}
