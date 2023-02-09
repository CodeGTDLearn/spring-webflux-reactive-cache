package caching.item;

import caching.config.exceptions.ItemExceptionThrower;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static caching.config.ItemRoutes.*;
import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping(ROOT)
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;
  private final ItemExceptionThrower itemExceptionsThrower;

  @DeleteMapping(DELETE)
  @ResponseStatus(NO_CONTENT)
  public Mono<Void> delete(@PathVariable String id) {

    return
         itemService
              .findById(id)
              .switchIfEmpty(itemExceptionsThrower.throwItemNotFoundException())
              .then(itemService.delete(id))
         ;
  }

  @GetMapping(FIND_ALL)
  @ResponseStatus(OK)
  public Flux<Item> findAll() {

    return itemService.findAll();
  }

  @GetMapping(FIND_BY_ID)
  @ResponseStatus(OK)
  public Mono<Item> findById(@PathVariable String id) {

    return
         itemService
              .findById(id)
              .switchIfEmpty(itemExceptionsThrower.throwItemNotFoundException())
         ;
  }

  @Transactional
  @PostMapping(SAVE)
  @ResponseStatus(CREATED)
  public Mono<Item> save(@Valid @RequestBody Item item) {
//https://www.youtube.com/watch?v=IqptwwkznCE&list=PL62G310vn6nH5Tgcp5q2a1xCb6CsZJAi7&index=25
    return
         itemService
              .save(item)
              .doOnNext(returnedItem -> {
                boolean check = StringUtil.isNullOrEmpty(returnedItem.getName());
                if (check) itemExceptionsThrower.throwsItemNameIsEmptyException();
              })
         ;
  }


  @Transactional
  @PutMapping(UPDATE)
  @ResponseStatus(OK)
  public Mono<Item> update(@RequestBody Item item) {

    return itemService
         .findById(item.get_id())
         .switchIfEmpty(itemExceptionsThrower.throwItemNotFoundException())
         .then(itemService.update(item))
         ;
  }
}