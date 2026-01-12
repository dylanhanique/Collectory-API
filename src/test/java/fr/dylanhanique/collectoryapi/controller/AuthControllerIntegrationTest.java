package fr.dylanhanique.collectoryapi.controller;

import fr.dylanhanique.collectoryapi.dto.LoginRequest;
import fr.dylanhanique.collectoryapi.dto.RegisterUserRequest;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.repository.UserRepository;
import fr.dylanhanique.collectoryapi.security.jwt.RestAuthenticationEntryPoint;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc()
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
        User user = new User("User", "user@email.com", "$2a$12$EMQfMcaXZYYbcmCBbduBR.prEM0kssaX5KYEslstqFIIAQLm0mld.");
        User savedUser = userRepository.save(user);
    }

    @Nested
    @DisplayName("POST /auth/signup")
    class Signup {

        @Test
        @DisplayName("POST /auth/signup returns 201 and the user when dto is valid")
        void signup_whenDtoIsValid_shouldCreateAndReturnUser() throws Exception {
            RegisterUserRequest dto = new RegisterUserRequest("New user", "newuser@email.com", "password");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/signup")
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.username").value(dto.username()))
                    .andExpect(jsonPath("$.email").value(dto.email()))
                    .andExpect(jsonPath("$.password").doesNotExist());

            assertThat(userRepository.findByEmail("newuser@email.com")).isPresent();
        }

        private static Stream<Arguments> signup_wrongDtos() {
            return Stream.of(
                    Arguments.of(new RegisterUserRequest("", "newuser@email.com", "password"), HttpStatus.BAD_REQUEST), // empty username
                    Arguments.of(new RegisterUserRequest("xy", "newuser@email.com", "password"), HttpStatus.BAD_REQUEST), // username too short
                    Arguments.of(new RegisterUserRequest("User", "newuser@email.com", "password"), HttpStatus.CONFLICT), // username already taken
                    Arguments.of(new RegisterUserRequest("New User", "", "password"), HttpStatus.BAD_REQUEST), // empty email
                    Arguments.of(new RegisterUserRequest("New User", "wrongEmailFormat", "password"), HttpStatus.BAD_REQUEST), // wrong format email
                    Arguments.of(new RegisterUserRequest("New User", "@wrongEmailFormat.com", "password"), HttpStatus.BAD_REQUEST), // wrong format email
                    Arguments.of(new RegisterUserRequest("New User", "user@email.com", "password"), HttpStatus.CONFLICT), // email already taken
                    Arguments.of(new RegisterUserRequest("New User", "newuser@email.com", ""), HttpStatus.BAD_REQUEST), // empty password
                    Arguments.of(new RegisterUserRequest("New User", "newuser@email.com", "pass"), HttpStatus.BAD_REQUEST) // password too short
            );
        }

        @ParameterizedTest
        @MethodSource("signup_wrongDtos")
        @DisplayName("POST /auth/signup returns 400 or 409 when dto is not valid")
        void signup_whenDtoIsNotValid_shouldReturnBadRequest(RegisterUserRequest dto, HttpStatus httpStatus) throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/signup")
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(httpStatus.value()));
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class Login {

        @Test
        @DisplayName("POST /auth/login should return a Jwt when the user exists and credentials are valid")
        void login_whenUserExistsAndCredentialsAreValid_shouldReturnJwt() throws Exception {
            LoginRequest dto = new LoginRequest("user@email.com", "password");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/login")
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        private static Stream<Arguments> login_wrongDtos() {
            return Stream.of(
                    Arguments.of(new LoginRequest("", "password"), HttpStatus.BAD_REQUEST), // empty email
                    Arguments.of(new LoginRequest("user@email.com", ""), HttpStatus.BAD_REQUEST), // empty password
                    Arguments.of(new LoginRequest("user@email.com", "pass"), HttpStatus.BAD_REQUEST), // password too short
                    Arguments.of(new LoginRequest("wrongemail@email.com", "password"), HttpStatus.UNAUTHORIZED), // wrong email
                    Arguments.of(new LoginRequest("user@email.com", "wrongPassword"), HttpStatus.UNAUTHORIZED) // wrong password
            );
        }

        @ParameterizedTest
        @MethodSource("login_wrongDtos")
        @DisplayName("POST /auth/login returns 400 or 401 when dto is not valid")
        void login_whenDtoIsNotValid_shouldReturnBadRequest(LoginRequest dto, HttpStatus httpStatus) throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/login")
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(httpStatus.value()));
        }

    }
}
