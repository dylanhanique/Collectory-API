package fr.dylanhanique.collectoryapi.service;

import fr.dylanhanique.collectoryapi.dto.CollectibleCollectionResponse;
import fr.dylanhanique.collectoryapi.dto.CreateCollectibleCollectionRequest;
import fr.dylanhanique.collectoryapi.mapper.CollectibleCollectionMapper;
import fr.dylanhanique.collectoryapi.model.CollectibleCollection;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.repository.CollectibleCollectionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CollectibleCollectionServiceTest {

    @Mock
    CollectibleCollectionRepository collectibleCollectionRepository;

    @InjectMocks
    CollectibleCollectionService collectibleCollectionService;

    @Test
    @DisplayName("create should add in database and return collectible collection when dto is valid")
    void create_whenDtoIsValid_shouldCreateInDbAndReturnCollectibleCollection() {
        User user = new User("User", "user@email.com", "password");
        CreateCollectibleCollectionRequest dto = new CreateCollectibleCollectionRequest("New collection", "http://newcollection.test/images/coverImageUrl.jpg", "Test new collection");
        when(collectibleCollectionRepository.save(any(CollectibleCollection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CollectibleCollectionResponse result = collectibleCollectionService.create(dto, user);

        verify(collectibleCollectionRepository).save(any(CollectibleCollection.class));
        assertThat(result.name()).isEqualTo(dto.name());
        assertThat(result.coverImageUrl()).isEqualTo(dto.coverImageUrl());
        assertThat(result.description()).isEqualTo(dto.description());
    }

    @Test
    @DisplayName("findAll should return all user collectibleCollections when user exists")
    void findAll_whenUserExists_shouldReturnAllUserCollectibleCollections() {
        User user = new User("User", "user@email.com", "password");
        List<CollectibleCollection> collectibleCollections = List.of(
                new CollectibleCollection("Collection 1", "http://newcollection.test/images/coverImageUrl.jpg", "Description 1", user),
                new CollectibleCollection("Collection 2", "http://newcollection.test/images/coverImageUrl.jpg", "Description 2", user),
                new CollectibleCollection("Collection 3", "http://newcollection.test/images/coverImageUrl.jpg", "Description 3", user)
        );
        List<CollectibleCollectionResponse> expectedResult = collectibleCollections.stream().map(CollectibleCollectionMapper::fromEntityToCollectibleCollectionResponse).toList();
        when(collectibleCollectionRepository.findAllByUser(user)).thenReturn(collectibleCollections);

        List<CollectibleCollectionResponse> result = collectibleCollectionService.findAllByUser(user);

        assertThat(result).containsExactlyElementsOf(expectedResult);
        verify(collectibleCollectionRepository).findAllByUser(user);
    }

}
