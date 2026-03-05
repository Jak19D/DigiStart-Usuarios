package DigiStart.Mapper;

import DigiStart.DTO.Output.UserResponseDTO;
import DigiStart.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "senha", ignore = true)
    User toEntity(UserResponseDTO dto);
}
