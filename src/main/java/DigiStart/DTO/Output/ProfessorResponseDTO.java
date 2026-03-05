package DigiStart.DTO.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProfessorResponseDTO {
    private Long id;
    private String nome;
    private String telefone;
    private UserResponseDTO user;
}
