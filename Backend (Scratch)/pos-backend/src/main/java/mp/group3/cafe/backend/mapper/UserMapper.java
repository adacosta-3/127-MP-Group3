package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.UserDTO;
import mp.group3.cafe.backend.DTO.UserResponseDTO;
import mp.group3.cafe.backend.entities.User;

public class UserMapper {
    public static UserDTO mapToUserDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }

    public static User mapToUser(UserDTO userDTO) {
        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        return user;
    }

    public static UserResponseDTO toUserResponseDTO(UserDTO userDTO) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setUserId(userDTO.getUserId());
        responseDTO.setUsername(userDTO.getUsername());
        responseDTO.setRole(userDTO.getRole());
        return responseDTO;
    }
}
