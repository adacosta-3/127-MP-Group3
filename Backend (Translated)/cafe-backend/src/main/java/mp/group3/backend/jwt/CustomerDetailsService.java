package mp.group3.backend.jwt;

import lombok.extern.slf4j.Slf4j;
import mp.group3.backend.dtos.UserDTO;
import mp.group3.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;


    private mp.group3.backend.entities.User userDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}", username);
        userDetails = userRepository.findByEmailId(username);
        if (!Objects.isNull(userDetails)) {
            return new User(userDetails.getEmail(), userDetails.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public mp.group3.backend.entities.User getUserDatails() {
        return userDetails;
    }
}

