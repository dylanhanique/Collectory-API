package fr.dylanhanique.collectoryapi.service;

import fr.dylanhanique.collectoryapi.dto.CreateUserRequest;
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
            CreateUserRequest dto = new CreateUserRequest("New user", "newuser@email.com", "password");
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
                    Arguments.of(new CreateUserRequest("UsernameAlreadyTaken", "takenusername@email.com", "password"), UsernameAlreadyTakenException.class),
                    Arguments.of(new CreateUserRequest("New user", "emailAlreadyTaken@email.com", "password"), EmailAlreadyTakenException.class)
            );
        }

        @ParameterizedTest
        @MethodSource("create_failureCases")
        @DisplayName("Create should throw an exception when request is invalid")
        void create_invalidRequests_throwExceptions(CreateUserRequest dto, Class<? extends Exception> expectedException) {
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
            User user = new User(1L, "User 1", "user1@email.com", "encodedPassword");
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
}
