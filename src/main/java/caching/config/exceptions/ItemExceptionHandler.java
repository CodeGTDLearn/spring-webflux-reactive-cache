package caching.config.exceptions;

import caching.config.exceptions.types.ItemNameIsEmptyException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

// ==> EXCEPTIONS IN CONTROLLER:
// *** REASON: IN WEBFLUX, EXCEPTIONS MUST BE IN CONTROLLER - WHY?
//     - "Como stream pode ser manipulado por diferentes grupos de thread,
//     - caso um erro aconteça em uma thread que não é a que operou a controller,
//     - o ControllerAdvice não vai ser notificado "
//     - https://medium.com/nstech/programa%C3%A7%C3%A3o-reativa-com-spring-boot-webflux-e-mongodb-chega-de-sofrer-f92fb64517c3
@ControllerAdvice(annotations = {RestController.class})
@AllArgsConstructor
public class ItemExceptionHandler {

  @ExceptionHandler(ItemNameIsEmptyException.class)
  public ResponseEntity<?> itemNameIsEmptyException(ItemNameIsEmptyException exception) {

    ItemExceptionsAttributes attributes =
         new ItemExceptionsAttributes(
              exception.getMessage(),
              exception.getClass()
                       .getName(),
              NOT_ACCEPTABLE.value(),
              new Date().getTime()
         );
    return new ResponseEntity<>(attributes, NOT_ACCEPTABLE);
  }

}