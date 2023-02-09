package caching.item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ItemService {

  Mono<Void> delete(String id);


  Flux<Item> findAll();


  Mono<Item> findById(String id);


  Mono<Item> save(Item item);


  Mono<Item> update(Item item);
}