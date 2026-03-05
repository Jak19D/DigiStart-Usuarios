package DigiStart.DTO.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AulaResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String videoUrl;
    private int ordem;
    private boolean ativa;

    private Long moduloId;
    private String nomeModulo;
}
