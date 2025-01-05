package mp.group3.cafe.backend.serviceImpl;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.UserDTO;
import mp.group3.cafe.backend.entities.User;
import mp.group3.cafe.backend.mapper.UserMapper;
import mp.group3.cafe.backend.repositories.UserRepository;
import mp.group3.cafe.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Integer userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToUserDTO);
    }

    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::mapToUserDTO);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = UserMapper.mapToUser(userDTO);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Integer userId, UserDTO userDTO) {
        Optional<User> existingUserOpt = userRepository.findById(userId);
        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User existingUser = existingUserOpt.get();
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setRole(userDTO.getRole());
        existingUser.setPassword(userDTO.getPassword()); // Password should be hashed in a real app
        User updatedUser = userRepository.save(existingUser);

        return UserMapper.mapToUserDTO(updatedUser);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
}


