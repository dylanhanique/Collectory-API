package fr.dylanhanique.collectoryapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CreateCollectibleCollectionRequest(
        @NotBlank @Size(min = 3) String name,
        //TODO: better validation jpg, png, ...
        @URL @Size(max = 255) String coverImageUrl,
        @Size(max = 255) String description
) {}
