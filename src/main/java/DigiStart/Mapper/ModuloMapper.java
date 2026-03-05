package DigiStart.Mapper;

import DigiStart.DTO.Input.ModuloRequestDTO;
import DigiStart.DTO.Output.ModuloResponseDTO;
import DigiStart.Model.Modulo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModuloMapper {

    @Mapping(source = "professor.nome", target = "nomeProfessor")
    ModuloResponseDTO toResponseDTO(Modulo modulo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "professor", ignore = true)
    Modulo toEntity(ModuloRequestDTO dto);
}