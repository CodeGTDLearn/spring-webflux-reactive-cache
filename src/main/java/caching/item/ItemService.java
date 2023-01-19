package caching.item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ItemService {

  Mono<Item> getById(String id);


  Mono<Item> save(Item item);


  Flux<Item> getAll();


  Mono<Item> update(Item item);


  Mono<Void> delete(String id);
}