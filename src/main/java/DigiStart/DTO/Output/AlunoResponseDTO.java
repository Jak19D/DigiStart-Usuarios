package DigiStart.DTO.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AlunoResponseDTO {
    private Long id;
    private String nickname;
    private String dataNascimento;
    private UserResponseDTO user;
    private ModuloResponseDTO moduloAtual;
}
