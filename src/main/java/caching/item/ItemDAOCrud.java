package caching.item;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository("itemDAOCrud")
public interface ItemDAOCrud extends ReactiveMongoRepository<Item, String> {
}