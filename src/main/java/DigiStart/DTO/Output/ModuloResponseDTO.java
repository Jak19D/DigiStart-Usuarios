package DigiStart.DTO.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ModuloResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String nomeProfessor;
}
