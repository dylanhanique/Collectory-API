package fr.dylanhanique.collectoryapi.dto;

import fr.dylanhanique.collectoryapi.model.CollectibleItem;

import java.util.Date;
import java.util.List;

public record CollectibleCollectionResponse (
        Long id,
        String name,
        String coverImageUrl,
        String description,

        // TODO: pagination
        List<CollectibleItem> collectibleItems,

        Date createdAt,
        Date updatedAt
) {}
