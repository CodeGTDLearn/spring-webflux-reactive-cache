package caching.config.exceptions.global;

import caching.config.YamlProcessor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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
// =================== PropertySource + ConfigurationProperties + YAML Files =======================
// - @PropertySource with YAML Files in Spring Boot - https://www.baeldung.com/spring-yaml-propertysource
// - Setter/Getter are CRUCIAL for PropertySource + ConfigurationProperties works properly
@Getter
@Setter
@Configuration("globalExceptionMessages")
@ConfigurationProperties(prefix = "global.exception")
@PropertySource(value = "classpath:exceptions-messages.yml", factory =
     YamlProcessor.class)
public class GlobalExceptionMessages {

  // THE BEAN-VALIDATION IS VALIDATING THE MESSAGE-CONTENT
  // THAT COMES FROM THE EXCEPTIONS-MANAGEMENT.PROPERTIES FILE
  // THOSE VALIDATIONS NOT HAVE RELATION WITH THE EXCEPTIONS
  private String developerMessage;
  private String globalMessage;

}