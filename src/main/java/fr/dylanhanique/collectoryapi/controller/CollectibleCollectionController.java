package fr.dylanhanique.collectoryapi.controller;

import fr.dylanhanique.collectoryapi.dto.CollectibleCollectionResponse;
import fr.dylanhanique.collectoryapi.dto.CreateCollectibleCollectionRequest;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.service.CollectibleCollectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collections")
public class CollectibleCollectionController {

    private final CollectibleCollectionService collectibleCollectionService;

    @Autowired
    public CollectibleCollectionController(CollectibleCollectionService collectibleCollectionService) {
        this.collectibleCollectionService = collectibleCollectionService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CollectibleCollectionResponse create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateCollectibleCollectionRequest dto)
    {
        return collectibleCollectionService.create(dto, user);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<CollectibleCollectionResponse> findAllByUser(@AuthenticationPrincipal User user) {
        return collectibleCollectionService.findAllByUser(user);
    }

}
