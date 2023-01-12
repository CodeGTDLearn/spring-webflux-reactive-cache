package caching.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service("itemService")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemDAOCrud itemDAOCrud;

  @Override
  public Mono<Item> save(Item item) {

    return itemDAOCrud.save(item);
  }


  @Override
  public Mono<Item> update(Item item) {

    return itemDAOCrud.save(item);
  }

  @Override
  public Mono<Item> getById(String id) {

    return itemDAOCrud.findById(id);
  }

  @Override
  public Flux<Item> getAll() {

    return itemDAOCrud.findAll();
  }

  @Override
  public Mono<Void> delete(String id) {

    return itemDAOCrud.deleteById(id);
  }

  @Override
  public Flux<Item> saveTransact(List<Item> userList) {

    return itemDAOCrud.saveAll(userList);
  }


}