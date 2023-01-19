package caching.item;

import caching.config.exceptions.ItemExceptionsThrower;
import caching.config.exceptions.types.ItemNameIsEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
  private final ItemExceptionsThrower itemExceptionsThrower;

  @Transactional
  @PostMapping(SAVE)
  @ResponseStatus(CREATED)
  public Mono<Item> save(@Valid @RequestBody Item item) {

    return itemService
         .save(item)
         .onErrorResume(this::exceptionSelector);

  }

  @Transactional
  @PutMapping(UPDATE)
  @ResponseStatus(OK)
  public Mono<Item> update(@RequestBody Item item) {

    return itemService
         .update(item)
         .onErrorResume(this::exceptionSelector);
  }

  @GetMapping(GET_BY_ID)
  @ResponseStatus(OK)
  public Mono<Item> getById(@PathVariable String id) {

    return itemService.getById(id);
  }

  @GetMapping(GET_ALL)
  @ResponseStatus(OK)
  public Flux<Item> getAll() {

    return itemService.getAll();
  }

  @DeleteMapping(DELETE)
  @ResponseStatus(NO_CONTENT)
  public Mono<Void> delete(@PathVariable String id) {

    return itemService.delete(id);
  }


  private Mono<Item> exceptionSelector(Throwable error) {

    if (error instanceof ItemNameIsEmptyException)
      return itemExceptionsThrower.throwsItemNameIsEmptyException();

    return Mono.error(new ResponseStatusException(NOT_FOUND));
  }
}