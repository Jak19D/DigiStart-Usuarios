package DigiStart.DTO.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ExercicioResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
}
