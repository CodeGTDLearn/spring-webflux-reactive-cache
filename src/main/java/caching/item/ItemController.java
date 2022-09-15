package caching.item;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static caching.item.ItemRoutes.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


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

  @GetMapping(GET_ITEM)
  @ResponseStatus(OK)
  public Mono<Item> getItem(@RequestParam String id) {

    return itemService.getItem(id);
  }

  @Transactional
  @GetMapping(GET_ITEM_WITH_CACHE)
  @ResponseStatus(OK)
  public Mono<Item> getItem_withCache(@RequestParam String id) {

    return
         itemService.getItem_withCache(id)
//              .doOnNext(this::throwSimpleExceptionWhenEmptyName)
         ;
  }


}