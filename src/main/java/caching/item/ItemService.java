package caching.item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemService {

  Mono<Item> getById(String id);


  Mono<Item> save(Item item);


  Flux<Item> getAll();


  Mono<Item> update(Item project);


  Mono<Void> delete(String projectId);


  Mono<Item> getItem_withCache(String id);
}