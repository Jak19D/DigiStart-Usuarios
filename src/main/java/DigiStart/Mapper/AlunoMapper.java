package DigiStart.Mapper;

import DigiStart.DTO.Input.AlunoRequestDTO;
import DigiStart.DTO.Output.AlunoResponseDTO;
import DigiStart.Model.Aluno;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AlunoMapper {
    AlunoResponseDTO toResponseDTO(Aluno aluno);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Aluno toEntity(AlunoRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDTO(AlunoRequestDTO dto, @MappingTarget Aluno aluno);
}
