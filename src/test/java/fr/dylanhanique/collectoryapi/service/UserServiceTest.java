package fr.dylanhanique.collectoryapi.service;

import fr.dylanhanique.collectoryapi.dto.CreateUserRequest;
import fr.dylanhanique.collectoryapi.dto.UserResponse;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("create should add in database and return user")
    void create_whenDtoIsCorrect_createInDbAndReturnUser() {
        CreateUserRequest dto = new CreateUserRequest("New user", "newuser@email.com", "password");
        when(passwordEncoder.encode(dto.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.create(dto);

        verify(passwordEncoder).encode(dto.password());
        verify(userRepository).save(any(User.class));
        assertThat(result.id()).isNull();
        assertThat(result.username()).isEqualTo(dto.username());
        assertThat(result.email()).isEqualTo(dto.email());
    }

    @Test
    @DisplayName("findById should return user when user exists")
    void findById_existingUser_returnsUser() {
        User user = new User(1L, "User 1", "user1@email.com", "encodedPassword");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse result = userService.findById(user.getId());

        verify(userRepository).findById(user.getId());
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.username()).isEqualTo(user.getUsername());
        assertThat(result.email()).isEqualTo(user.getEmail());
    }
}
