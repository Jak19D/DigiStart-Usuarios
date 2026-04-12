package DigiStart.Mapper;

import DigiStart.DTO.Input.ProfessorRequestDTO;
import DigiStart.DTO.Output.ProfessorResponseDTO;
import DigiStart.Model.Professor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProfessorMapper {
    ProfessorResponseDTO toResponseDTO(Professor professor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "curriculoPath", ignore = true)
    Professor toEntity(ProfessorRequestDTO dto);
}
