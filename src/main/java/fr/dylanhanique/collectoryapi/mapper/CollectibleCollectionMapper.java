package fr.dylanhanique.collectoryapi.mapper;

import fr.dylanhanique.collectoryapi.dto.CollectibleCollectionResponse;
import fr.dylanhanique.collectoryapi.dto.CreateCollectibleCollectionRequest;
import fr.dylanhanique.collectoryapi.model.CollectibleCollection;
import fr.dylanhanique.collectoryapi.model.User;

public class CollectibleCollectionMapper {

    private CollectibleCollectionMapper() {}

    public static CollectibleCollectionResponse fromEntityToCollectibleCollectionResponse(CollectibleCollection collectibleCollection) {
        return new CollectibleCollectionResponse(
                collectibleCollection.getId(),
                collectibleCollection.getName(),
                collectibleCollection.getCoverImageUrl(),
                collectibleCollection.getDescription(),
                collectibleCollection.getCollectibleItems(),
                collectibleCollection.getCreatedAt(),
                collectibleCollection.getUpdatedAt()
        );
    }

    public static CollectibleCollection fromCreateCollectibleCollectionRequestToEntity(CreateCollectibleCollectionRequest dto, User user) {
        return new CollectibleCollection(dto.name(), dto.coverImageUrl(), dto.description(), user);
    }

}
