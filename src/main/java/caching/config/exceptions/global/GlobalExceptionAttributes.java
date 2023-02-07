package caching.config.exceptions.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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

  private GlobalExceptionMessages globalExceptionMessages;

  @Override
  public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                ErrorAttributeOptions options) {

    Map<String, Object> globalAttributes = super.getErrorAttributes(request, options);

    // ADICIONA A GLOBAL-EXCEPTION(ResponseStatusException)
    // POIS NAO SE TRATA DE NENHUMA DAS 'CUSTOM-EXCEPTIONS'
    Throwable throwable = getError(request);
    if (throwable instanceof ResponseStatusException) {

      ResponseStatusException error = (ResponseStatusException) throwable;

      // IDEIA GERAL
      // SENDO UMA GLOBAL-EXCEPTION(ResponseStatusException)
      // adiciona ATTRIBUTES no globalAttributes

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
      globalAttributes.put("message", error.getMessage());
      globalAttributes.put("reason", error.getReason());

      // C) ADDING Custom-Parameters in the Default-Parameters
      globalAttributes.put("Global-Global-Atribute", globalExceptionMessages.getGlobalMessage());
      globalAttributes.put("Global-Dev-Atribute", globalExceptionMessages.getDeveloperMessage());
      //      globalAttributes.put("example","example2");

      // D) REMOVING Keys/Fields from the Global-Exception-Message
      //      globalAttributes.remove("path");
      globalAttributes.remove("error");
      globalAttributes.remove("message");
      globalAttributes.remove("timestamp");
      globalAttributes.remove("requestId");
    }

    // NAO SENDO UMA GLOBAL-EXCEPTION(ResponseStatusException)
    // PORTANTO SENDO, UMA CUSTOM-EXCEPTION
    // retorna o valor PADRAO de ATTRIBUTES ou seja,
    // o globalAttributes "PURO", sem insercao(.put's do IF acima) de qquer atributo
    // personalizado
    // OU SEJA, nao se acrescenta os atributos definidos no IF-ACIMA
    return globalAttributes;
  }

}