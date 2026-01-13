package fr.dylanhanique.collectoryapi.repository;

import fr.dylanhanique.collectoryapi.model.CollectibleCollection;
import fr.dylanhanique.collectoryapi.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectibleCollectionRepository extends CrudRepository<CollectibleCollection, Long> {

    List<CollectibleCollection> findAllByUser(User user);

}
