package caching.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static caching.item.ItemRoutes.*;
import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping(ROOT)
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;

  @PostMapping(SAVE)
  @ResponseStatus(CREATED)
  public Mono<Item> save(@RequestBody Item item) {

    return
         itemService
              .save(item)
//              .doOnNext(this::throwSimpleExceptionWhenEmptyName)
         ;
  }

  @PutMapping(UPDATE)
  @ResponseStatus(OK)
  public Mono<Item> update(@RequestBody Item item) {

    return
         itemService
              .update(item)
         //              .doOnNext(this::throwSimpleExceptionWhenEmptyName)
         ;
  }

  @GetMapping(GET_BY_ID)
  @ResponseStatus(OK)
  public Mono<Item> getById(@RequestParam String id) {
    return itemService.getById(id);
  }

  @GetMapping(GET_ALL)
  @ResponseStatus(OK)
  public Flux<Item> getAll() {

    return itemService.getAll();
  }

  @DeleteMapping(DELETE)
  @ResponseStatus(NO_CONTENT)
  public Mono<Void> delete(@PathVariable String id){
    return itemService.delete(id);
  }
}