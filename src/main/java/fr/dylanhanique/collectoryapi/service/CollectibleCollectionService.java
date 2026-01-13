package fr.dylanhanique.collectoryapi.service;

import fr.dylanhanique.collectoryapi.dto.CollectibleCollectionResponse;
import fr.dylanhanique.collectoryapi.dto.CreateCollectibleCollectionRequest;
import fr.dylanhanique.collectoryapi.mapper.CollectibleCollectionMapper;
import fr.dylanhanique.collectoryapi.model.CollectibleCollection;
import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.repository.CollectibleCollectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectibleCollectionService {

    private final CollectibleCollectionRepository collectibleCollectionRepository;

    public CollectibleCollectionService(CollectibleCollectionRepository collectibleCollectionRepository) {
        this.collectibleCollectionRepository = collectibleCollectionRepository;
    }

    public CollectibleCollectionResponse create(CreateCollectibleCollectionRequest dto, User user) {

        CollectibleCollection savedCollection = collectibleCollectionRepository.save(
                CollectibleCollectionMapper.fromCreateCollectibleCollectionRequestToEntity(dto, user)
        );

        return CollectibleCollectionMapper.fromEntityToCollectibleCollectionResponse(savedCollection);

    }

    public List<CollectibleCollectionResponse> findAllByUser(User user) {
        return collectibleCollectionRepository.findAllByUser(user)
                .stream()
                .map(CollectibleCollectionMapper::fromEntityToCollectibleCollectionResponse)
                .toList();
    }

}
