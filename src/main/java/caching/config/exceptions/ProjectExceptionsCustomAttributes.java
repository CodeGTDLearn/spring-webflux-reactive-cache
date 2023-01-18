package caching.config.exceptions;

import caching.config.YamlProcessor;
import com.webflux.api.core.config.YamlProcessor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


// =================== PropertySource + ConfigurationProperties + YAML Files =======================
// - @PropertySource with YAML Files in Spring Boot - https://www.baeldung.com/spring-yaml-propertysource
// - Setter/Getter are CRUCIAL for PropertySource + ConfigurationProperties works properly
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "modules.exception.project")
@PropertySource(value = "classpath:exceptions-messages.yml", factory =
     YamlProcessor.class)
public class ProjectExceptionsCustomAttributes {

  // THE BEAN-VALIDATION IS VALIDATING THE MESSAGE-CONTENT
  // THAT COMES FROM THE EXCEPTIONS-MANAGEMENT.PROPERTIES FILE
  // THOSE VALIDATIONS NOT HAVE RELATION WITH THE EXCEPTIONS
  //    @NotEmpty
  private String projectNotFoundMessage;
  private String projectUpdateSimpleFailMessage;
  private String projectUpdateOptFailMessage;
  private String projectNameIsEmptyMessage;
}