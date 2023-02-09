package caching.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service("itemService")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemDAOCrud itemDAOCrud;

  @Override
  public Mono<Void> delete(String id) {

    return itemDAOCrud.deleteById(id);
  }

  @Override
  public Flux<Item> findAll() {

    return itemDAOCrud.findAll();
  }

  @Override
  public Mono<Item> findById(String id) {

    return itemDAOCrud.findById(id);
  }

  @Override
  public Mono<Item> save(Item item) {

    return itemDAOCrud.save(item);
  }

  @Override
  public Mono<Item> update(Item item) {

    return itemDAOCrud.save(item);
  }
}