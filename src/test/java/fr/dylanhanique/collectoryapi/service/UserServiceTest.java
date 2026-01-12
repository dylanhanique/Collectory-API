package fr.dylanhanique.collectoryapi.service;

import fr.dylanhanique.collectoryapi.dto.RegisterUserRequest;
import fr.dylanhanique.collectoryapi.dto.UserResponse;
import fr.dylanhanique.collectoryapi.exception.EmailAlreadyTakenException;
import fr.dylanhanique.collectoryapi.exception.UserNotFoundException;
import fr.dylanhanique.collectoryapi.exception.UsernameAlreadyTakenException;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Nested
    @DisplayName("Create")
    class Create {

        @Test
        @DisplayName("create should add in database and return user")
        void create_whenDtoIsCorrect_createInDbAndReturnUser() {
            RegisterUserRequest dto = new RegisterUserRequest("New user", "newuser@email.com", "password");
            when(userRepository.existsByEmail(dto.email())).thenReturn(false);
            when(userRepository.existsByUsername(dto.username())).thenReturn(false);
            when(passwordEncoder.encode(dto.password())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserResponse result = userService.create(dto);

            verify(userRepository).existsByEmail(dto.email());
            verify(userRepository).existsByUsername(dto.username());
            verify(passwordEncoder).encode(dto.password());
            verify(userRepository).save(any(User.class));
            assertThat(result.id()).isNull();
            assertThat(result.username()).isEqualTo(dto.username());
            assertThat(result.email()).isEqualTo(dto.email());
        }

        private static Stream<Arguments> create_failureCases() {
            return Stream.of(
                    Arguments.of(new RegisterUserRequest("User", "user@email.com", "password"), UsernameAlreadyTakenException.class),
                    Arguments.of(new RegisterUserRequest("New user", "user@email.com", "password"), EmailAlreadyTakenException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("create_failureCases")
        @DisplayName("Create should throw an exception when request is invalid")
        void create_invalidRequests_throwExceptions(RegisterUserRequest dto, Class<? extends Exception> expectedException) {
            if (expectedException == UsernameAlreadyTakenException.class) {
                when(userRepository.existsByUsername(dto.username())).thenReturn(true);
            } else if (expectedException == EmailAlreadyTakenException.class) {
                when(userRepository.existsByEmail(dto.email())).thenReturn(true);
            }

            assertThatThrownBy(() -> userService.create(dto)).isInstanceOf(expectedException);
        }
    }

    @Nested
    @DisplayName("FindById")
    class FindById {

        @Test
        @DisplayName("findById should return user when user exist")
        void findById_userExist_returnsUser() {
            User user = new User(1L, "User", "user@email.com", "password");
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            UserResponse result = userService.findById(user.getId());

            verify(userRepository).findById(user.getId());
            assertThat(result.id()).isEqualTo(user.getId());
            assertThat(result.username()).isEqualTo(user.getUsername());
            assertThat(result.email()).isEqualTo(user.getEmail());
        }

        @Test
        @DisplayName("FindById should throw an exception when user does not exist")
        void findById_userDoesNotExist_throwException() {
            Long userId = 999L;
            when(userRepository.findById(userId)).thenThrow(new UserNotFoundException(userId));

            assertThatThrownBy(() -> userService.findById(userId)).isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("FindByEmail")
    class FindByEmail {

        @Test
        @DisplayName("findByEmail should return user when user exist")
        void findByEmail_userExist_returnUser() {
            User user = new User(1L, "User", "user@email.com", "password");
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

            User result = userService.findByEmail(user.getEmail());

            verify(userRepository).findByEmail(user.getEmail());
            assertThat(result.getId()).isEqualTo(user.getId());
            assertThat(result.getUsername()).isEqualTo(user.getUsername());
            assertThat(result.getEmail()).isEqualTo(user.getEmail());
            assertThat(result.getPassword()).isEqualTo(user.getPassword());
        }

        @Test
        @DisplayName("FindByEmail should throw an exception when user does not exist")
        void findByEmail_userDoesNotExist_throwException() {
            String email = "wrongemail@email.com";
            when(userRepository.findByEmail(email)).thenThrow(new UserNotFoundException(email));

            assertThatThrownBy(() -> userService.findByEmail(email)).isInstanceOf(UserNotFoundException.class);
        }
    }
}
