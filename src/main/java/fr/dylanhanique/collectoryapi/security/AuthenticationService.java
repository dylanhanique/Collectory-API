package fr.dylanhanique.collectoryapi.security;

import fr.dylanhanique.collectoryapi.dto.LoginRequest;
import fr.dylanhanique.collectoryapi.dto.RegisterUserRequest;
import fr.dylanhanique.collectoryapi.dto.UserResponse;
import fr.dylanhanique.collectoryapi.mapper.UserMapper;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final fr.dylanhanique.collectoryapi.repository.UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse signup(RegisterUserRequest dto) {
        User savedUser = userRepository.save(
                User.fromDto(dto, passwordEncoder.encode(dto.password()))
        );

        return UserMapper.toResponse(savedUser);
    }

    public User authenticate(LoginRequest dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );

        return userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
    }
}