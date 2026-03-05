package DigiStart.Mapper;

import DigiStart.DTO.Input.AulaRequestDTO;
import DigiStart.DTO.Output.AulaResponseDTO;
import DigiStart.Model.Aula;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AulaMapper {

    @Mapping(source = "modulo.id", target = "moduloId")
    @Mapping(source = "modulo.nome", target = "nomeModulo")
    AulaResponseDTO toResponseDTO(Aula aula);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "modulo", ignore = true)
    @Mapping(target = "exercicios", ignore = true)
    Aula toEntity(AulaRequestDTO dto);
}