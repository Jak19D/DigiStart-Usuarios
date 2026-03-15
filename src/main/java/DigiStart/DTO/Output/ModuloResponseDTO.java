package DigiStart.DTO.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ModuloResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String nomeProfessor;
    private List<AulaResponseDTO> aulas;
}
