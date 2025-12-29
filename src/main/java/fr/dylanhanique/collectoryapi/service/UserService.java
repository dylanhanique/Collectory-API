package fr.dylanhanique.collectoryapi.service;

import fr.dylanhanique.collectoryapi.dto.CreateUserRequest;
import fr.dylanhanique.collectoryapi.dto.UserResponse;
import fr.dylanhanique.collectoryapi.exception.EmailAlreadyTakenException;
import fr.dylanhanique.collectoryapi.exception.UserNotFoundException;
import fr.dylanhanique.collectoryapi.exception.UsernameAlreadyTakenException;
import fr.dylanhanique.collectoryapi.mapper.UserMapper;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(CreateUserRequest dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyTakenException(dto.email());
        } else if (userRepository.existsByUsername(dto.username())) {
            throw new UsernameAlreadyTakenException(dto.username());
        }

        String encodedPassword = passwordEncoder.encode(dto.password());
        User savedUser = userRepository.save(User.fromDto(dto, encodedPassword));

        return UserMapper.toResponse(savedUser);
    }

    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return UserMapper.toResponse(user);
    }
}
