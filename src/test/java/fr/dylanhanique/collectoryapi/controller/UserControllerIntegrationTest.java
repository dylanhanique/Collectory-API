package fr.dylanhanique.collectoryapi.controller;

import fr.dylanhanique.collectoryapi.dto.CreateUserRequest;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.repository.UserRepository;
import jakarta.transaction.Transactional;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class UserControllerIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("GET /users/{id}")
    class getUserById {

        @Test
        @DisplayName("GET /users/{id} returns 200 and the user when it exists")
        void getUserById_whenUserExists_shouldReturnUser() throws Exception {
            User user = new User("User 2", "user2@email.com", "encodedPassword");
            User savedUser = userRepository.save(user);

            mockMvc.perform(get("/users/" + savedUser.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.username").value(savedUser.getUsername()))
                    .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                    .andExpect(jsonPath("$.password").doesNotExist());
        }

        @Test
        @DisplayName("GET /users/{id} returns 404 when user does not exist")
        void getUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/users/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

    }

    @Nested
    @DisplayName("POST /users")
    class createUser {

        @Test
        @DisplayName("POST /users returns 201 and the user when dto is valid")
        void createUser_dtoIsValid_shouldCreateAndReturnUser() throws Exception {
            CreateUserRequest dto = new CreateUserRequest("New user", "newuser@email.com", "password");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/users")
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.username").value(dto.username()))
                    .andExpect(jsonPath("$.email").value(dto.email()))
                    .andExpect(jsonPath("$.password").doesNotExist());
        }

        private static Stream<Arguments> createUser_wrongDtos() {
            return Stream.of(
                    Arguments.of(new CreateUserRequest("", "anothernewuser@email.com", "password"), HttpStatus.BAD_REQUEST), // empty username
                    Arguments.of(new CreateUserRequest("xy", "anothernewuser@email.com", "password"), HttpStatus.BAD_REQUEST), // too short username
                    Arguments.of(new CreateUserRequest("Another new user", "", "password"), HttpStatus.BAD_REQUEST), // empty email
                    Arguments.of(new CreateUserRequest("Another new user", "wrongEmailFormat", "password"), HttpStatus.BAD_REQUEST), // wrong format email
                    Arguments.of(new CreateUserRequest("Another new user", "@wrongEmailFormat.com", "password"), HttpStatus.BAD_REQUEST), // wrong format email
                    Arguments.of(new CreateUserRequest("Another new user", "anothernewuser@email.com", ""), HttpStatus.BAD_REQUEST), // empty password
                    Arguments.of(new CreateUserRequest("Another new user", "anothernewuser@email.com", "pass"), HttpStatus.BAD_REQUEST), // too short password
                    Arguments.of(new CreateUserRequest("User 3", "anothernewuser@email.com", "password"), HttpStatus.CONFLICT), // username already taken
                    Arguments.of(new CreateUserRequest("Another new user", "user3@email.com", "password"), HttpStatus.CONFLICT) // email already taken
            );
        }

        @ParameterizedTest
        @MethodSource("createUser_wrongDtos")
        @DisplayName("POST /users returns 400 or 409 when dto is not valid")
        void createUser_dtoIsNotValid_shouldReturnBadRequest(CreateUserRequest dto, HttpStatus httpStatus) throws Exception {
            User user = new User("User 3", "user3@email.com", "encodedPassword");
            User savedUser = userRepository.save(user);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/users")
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(httpStatus.value()));
        }
    }
}
