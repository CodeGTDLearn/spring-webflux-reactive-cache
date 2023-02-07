package caching.config.exceptions;

import caching.config.exceptions.types.ItemNameIsEmptyException;
import caching.config.exceptions.types.ItemNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

// ==> EXCEPTIONS IN CONTROLLER:
// *** REASON: IN WEBFLUX, EXCEPTIONS MUST BE IN CONTROLLER - WHY?
// - "Como stream pode ser manipulado por diferentes grupos de thread,
// - caso um erro aconteça em uma thread que não é a que operou a controller,
// - o ControllerAdvice não vai ser notificado "
// - https://medium.com/nstech/programa%C3%A7%C3%A3o-reativa-com-spring-boot-webflux-e-mongodb-chega-de-sofrer-f92fb64517c3
@Component("itemExceptionThrower")
@Getter
@Setter
@RequiredArgsConstructor
public class ItemExceptionThrower {

  @Autowired
  private ItemExceptionMessages itemExceptionMessages;

  public <T> Mono<T> throwsItemNameIsEmptyException() {

    return Mono.error(
         new ItemNameIsEmptyException(
              itemExceptionMessages.getItemNameIsEmptyMessage()));
  }

  public <T> Mono<T> throwItemNotFoundException() {

    return Mono.error(
         new ItemNotFoundException(
              itemExceptionMessages.getItemNotFoundMessage()));
  }

}