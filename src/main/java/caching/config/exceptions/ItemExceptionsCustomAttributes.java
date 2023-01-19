package caching.config.exceptions;

import caching.config.YamlProcessor;
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
@ConfigurationProperties(prefix = "exceptions.messages.item")
@PropertySource(value = "classpath:exceptions-messages.yml", factory = YamlProcessor.class)
public class ItemExceptionsCustomAttributes {

  private String itemNameIsEmptyMessage;
  private String itemNotFoundMessage;
}