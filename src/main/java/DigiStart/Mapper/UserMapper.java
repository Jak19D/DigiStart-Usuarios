package DigiStart.Mapper;

import DigiStart.DTO.Input.UserRequestDTO;
import DigiStart.DTO.Output.UserResponseDTO;
import DigiStart.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserResponseDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserRequestDTO dto);
}
