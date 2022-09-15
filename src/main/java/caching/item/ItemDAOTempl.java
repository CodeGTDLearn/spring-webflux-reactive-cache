package caching.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository("itemDAOTempl")
@RequiredArgsConstructor
public class ItemDAOTempl {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  /*╔══════════════════════════════════╗
    ║ REACTIVE-MONGO-TEMPLATE-CRITERIA ║
    ╚══════════════════════════════════╝*/

  public Mono<Void> dropCollectionsTemplate() {

    Flux<String> collections = reactiveMongoTemplate.getCollectionNames();

    return collections
         .map(item -> reactiveMongoTemplate.dropCollection(item + ".class"))
         .then();
  }

  public Flux<String> checkCollectionsTemplate() {

    Flux<String> collections = reactiveMongoTemplate.getCollectionNames();

    return collections
         .map(item -> reactiveMongoTemplate.dropCollection(item + ".class"))
         .thenMany(collections)
         .map(item -> item + "\n");
  }
}