package DigiStart.Mapper;

import DigiStart.DTO.Input.ExercicioRequestDTO;
import DigiStart.DTO.Output.ExercicioResponseDTO;
import DigiStart.Model.Exercicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExercicioMapper {
    ExercicioResponseDTO toResponseDTO(Exercicio exercicio);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aula", ignore = true)
    Exercicio toEntity(ExercicioRequestDTO dto);
}
