package caching.config.exceptions.global;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

import static org.springframework.boot.web.error.ErrorAttributeOptions.*;

/*
    ╔═══════════════════════════════════════════════════════════╗
    ║              GLOBAL-EXCEPTIONS EXPLANATIONS               ║
    ╠═══════════════════════════════════════════════════════════╣
    ║         There is no Thrower in Global-Exceptions          ║
    ║           Because Global-Exceptions are threw             ║
    ║               for "the system by itself",                 ║
    ║         not programmatically in a specific method         ║
    ║(meaning threw inside a method according the coder defined)║
    ╚═══════════════════════════════════════════════════════════╝
*/
// ==> EXCEPTIONS IN CONTROLLER:
// *** REASON: IN WEBFLUX, EXCEPTIONS MUST BE IN CONTROLLER - WHY?
//     - "Como stream pode ser manipulado por diferentes grupos de thread,
//     - caso um erro aconteça em uma thread que não é a que operou a controller,
//     - o ControllerAdvice não vai ser notificado "
//     - https://medium.com/nstech/programa%C3%A7%C3%A3o-reativa-com-spring-boot-webflux-e-mongodb-chega-de-sofrer-f92fb64517c3
//CustomGlobalExceptionHandler COMES BEFORE the SpringWebFluxGlobalExceptionHandlerDefault
@Order(- 2)
@Component
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

  private final String completeStackTrace = "completeStackTrace=true";


  public GlobalExceptionHandler(
       ErrorAttributes attributes,
       WebProperties webproperties,
       ApplicationContext context,
       ServerCodecConfigurer configurer) {

    super(attributes, webproperties.getResources(), context);
    this.setMessageWriters(configurer.getWriters());
    super.setMessageReaders(configurer.getReaders());
  }


  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {

    return RouterFunctions
         .route(
              RequestPredicates.all(),
              this::formatErrorResponse
         );
  }


  private Mono<ServerResponse> formatErrorResponse(ServerRequest request) {

    String query = request.uri()
                          .getQuery();

    ErrorAttributeOptions errorAttributeOptions =
         isTraceEnabled(query)
              ? of(Include.STACK_TRACE)
              : defaults();

    Map<String, Object>
         errorAttribsMap =
         getErrorAttributes(request, errorAttributeOptions);


    int status = (int) Optional
         .ofNullable(errorAttribsMap
                          .get("status"))
         .orElse(500);

    return ServerResponse
         .status(status)
         .contentType(MediaType.APPLICATION_JSON)
         .body(BodyInserters.fromValue(errorAttribsMap));
  }


  // Hence: ?trace=true in the url show the COMPLETE STACK-TRACK IN THE BROWSER
  // instead the CustomErrorHandlingException(simplified) as Stack-Trace
  private boolean isTraceEnabled(String query) {

    return StringUtils.hasText(query) && query.contains(completeStackTrace);
  }
}