package caching.item;

import caching.config.exceptions.ItemExceptionNameEmpty;
import caching.config.exceptions.types.ProjectNameIsEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

import static caching.config.routes.ItemRoutes.*;
import static io.netty.util.internal.StringUtil.isNullOrEmpty;
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

  @Transactional
  @PutMapping(UPDATE)
  @ResponseStatus(OK)
  public Mono<Item> update(@RequestBody Item item) {

    return
         itemService
              .update(item)
                       .doOnNext(this::throwSimpleExceptionWhenEmptyName)
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

  @Transactional
  @PostMapping(SAVE_ROLLBACK)
  @ResponseStatus(CREATED)
  public Flux<Item> saveRollback(@Valid @RequestBody List<Item> userList) {

    return
         itemService
              .saveTransact(userList)
              .onErrorResume(error -> {
                               if (error instanceof ProjectNameIsEmptyException) {
                                 return projectThrower.throwProjectNameIsEmptyException();
                               }
                               return Mono.error(new ResponseStatusException(NOT_FOUND));
                             }
              )
         ;
  }

  private void throwSimpleExceptionWhenEmptyName(Item user) {

    if (isNullOrEmpty(user.getName())) {
      throw new ItemExceptionNameEmpty(BAD_REQUEST, "Fail: Empty Name");
    }
  }
}