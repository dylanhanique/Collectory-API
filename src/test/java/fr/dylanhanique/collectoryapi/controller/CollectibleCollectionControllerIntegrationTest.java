package fr.dylanhanique.collectoryapi.controller;

import fr.dylanhanique.collectoryapi.dto.CreateCollectibleCollectionRequest;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.repository.CollectibleCollectionRepository;
import fr.dylanhanique.collectoryapi.repository.UserRepository;
import fr.dylanhanique.collectoryapi.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CollectibleCollectionControllerIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectibleCollectionRepository collectibleCollectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    String token;

    @BeforeEach
    void beforeEach() {
        User user = userRepository.save(new User("User", "user@email.com", "$2a$12$EMQfMcaXZYYbcmCBbduBR.prEM0kssaX5KYEslstqFIIAQLm0mld."));
        token = jwtService.generateToken(user);
    }

    @Nested
    @DisplayName("POST /collections")
    class Create {

        private static Stream<Arguments> create_validDtos() {
            return Stream.of(
                    Arguments.of(new CreateCollectibleCollectionRequest("New collection", "", "")),
                    Arguments.of(new CreateCollectibleCollectionRequest("New collection", "http://newcollection.test/images/coverImageUrl.jpg", "")),
                    Arguments.of(new CreateCollectibleCollectionRequest("New collection", "http://newcollection.test/images/coverImageUrl.jpg", "Description"))
            );
        }

        @ParameterizedTest
        @MethodSource("create_validDtos")
        @DisplayName("POST /collections returns 201 and the collection when dto is valid")
        void create_whenDtoIsValid_shouldCreateAndReturnUser(CreateCollectibleCollectionRequest dto) throws Exception {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                            .post("/collections")
                            .header("Authorization", "Bearer " + token)
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.name").value(dto.name()))
                    .andExpect(jsonPath("$.coverImageUrl").value(dto.coverImageUrl()))
                    .andExpect(jsonPath("$.description").value(dto.description()))
                    .andReturn();
        }

        private static Stream<Arguments> create_wrongDtos() {
            return Stream.of(
                    Arguments.of(new CreateCollectibleCollectionRequest("", "http://newcollection.test/images/coverImageUrl.jpg", "")), // empty name
                    Arguments.of(new CreateCollectibleCollectionRequest("ne", "", "")), // name too soort
                    Arguments.of(new CreateCollectibleCollectionRequest("New collection", "newcollection", "")), // coverImageUrl wrong format
                    Arguments.of(new CreateCollectibleCollectionRequest("New collection", "https://example.com/images/coverImageUrlaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.jpg", "")), // coverImageUrl too long
                    Arguments.of(new CreateCollectibleCollectionRequest("New collection", "http://newcollection.test/images/coverImageUrl.jpg","""
                                            Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa.
                                            Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis,
                                            ultricies nec, pellentesque eu, pretium quis,.""")) // description too long
            );
        }

        @ParameterizedTest
        @MethodSource("create_wrongDtos")
        @DisplayName("POST /collections returns 400 when dto is not valid")
        void create_whenDtoIsNotValid_shouldReturnBadRequest(CreateCollectibleCollectionRequest dto) throws Exception {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                            .post("/collections")
                            .header("Authorization", "Bearer " + token)
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

    }

}
