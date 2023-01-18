package caching.config.exceptions;

import com.webflux.api.modules.project.core.exceptions.types.ProjectNameIsEmptyException;
import com.webflux.api.modules.project.core.exceptions.types.ProjectNotFoundException;
import com.webflux.api.modules.project.core.exceptions.types.UpdateOptmisticVersionException;
import com.webflux.api.modules.project.core.exceptions.types.UpdateSimpleException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

// ==> EXCEPTIONS IN CONTROLLER:
// *** REASON: IN WEBFLUX, EXCEPTIONS MUST BE IN CONTROLLER - WHY?
//     - "Como stream pode ser manipulado por diferentes grupos de thread,
//     - caso um erro aconteça em uma thread que não é a que operou a controller,
//     - o ControllerAdvice não vai ser notificado "
//     - https://medium.com/nstech/programa%C3%A7%C3%A3o-reativa-com-spring-boot-webflux-e-mongodb-chega-de-sofrer-f92fb64517c3
// getters + setter are necessary, in order to use @ConfigurationProperties
@Component("projectExceptionsThrower")
@Getter
@Setter
@RequiredArgsConstructor
public class ProjectExceptionsThrower {

  private ProjectExceptionsCustomAttributes projectExceptionsCustomAttributes;

  public <T> Mono<T> throwProjectNameIsEmptyException() {

    return Mono.error(new ProjectNameIsEmptyException(
         projectExceptionsCustomAttributes.getProjectNameIsEmptyMessage()));
  }

}