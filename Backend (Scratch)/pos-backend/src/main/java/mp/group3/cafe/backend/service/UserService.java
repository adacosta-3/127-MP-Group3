package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();

    Optional<UserDTO> getUserById(Integer userId);

    Optional<UserDTO> getUserByUsername(String username);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Integer userId, UserDTO userDTO);

    void deleteUser(Integer userId);
}

