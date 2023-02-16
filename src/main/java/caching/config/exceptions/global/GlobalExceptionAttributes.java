package caching.config.exceptions.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

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
@Component
@Getter
@Setter
@AllArgsConstructor
public class GlobalExceptionAttributes extends DefaultErrorAttributes {

  @Autowired
  private GlobalExceptionMessages globalExceptionMessages;

  @Override
  public Map<String, Object> getErrorAttributes(
       ServerRequest request,
       ErrorAttributeOptions options) {

    Map<String, Object> attributes = super.getErrorAttributes(request, options);

    // ADICIONA A GLOBAL-EXCEPTION(ResponseStatusException)
    // POIS NAO SE TRATA DE NENHUMA DAS 'CUSTOM-EXCEPTIONS'
    Throwable throwable = getError(request);
    if (throwable instanceof ResponseStatusException error) {

      // IDEIA GERAL
      // SENDO UMA GLOBAL-EXCEPTION(ResponseStatusException)
      // adiciona ATTRIBUTES no attributes

      // A) DEFAULT-EXCEPTION-ATTRIBUTES EXAMPLE:
            /*
            {
                 "timestamp": "2022-02-08T22:02:08.410+00:00",
                 "path": "/project/save",
                 "status": 500,
                 "error": "Internal Server Error",
                 "message": "",
                 "requestId": "317e3568"
            }
            */

      // B) CREATING Parameters based on Default-Message
      // B.1) Fix the Default-Parameter "message"("message": "",) DO NOT USE ":"
      attributes.put("message", error.getMessage());
      attributes.put("reason", error.getReason());

      // C) ADDING Custom-Parameters in the Default-Parameters
      attributes.put("Global-Atrib", globalExceptionMessages.getGlobalMessage());
      attributes.put("Dev-Atrib", globalExceptionMessages.getDeveloperMessage());
      //      attributes.put("example","example2");

      // D) REMOVING Keys/Fields from the Global-Exception-Message
      //      attributes.remove("path");
      attributes.remove("error");
      // attributes.remove("message");
      attributes.remove("timestamp");
      attributes.remove("requestId");
    }

    // NAO SENDO UMA GLOBAL-EXCEPTION(ResponseStatusException)
    // PORTANTO SENDO, UMA CUSTOM-EXCEPTION-GLOBAL
    // retorna o valor PADRAO de ATTRIBUTES ou seja,
    // o attributes "PURO", sem insercao(.put's do IF acima) de qquer atributo
    // personalizado
    // OU SEJA, nao se acrescenta os atributos definidos no IF-ACIMA
    return attributes;
  }

}