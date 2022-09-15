package caching.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

/*╔══════════════════════════════════════════════════════════════╗
  ║                     YAML-FILE-CONVERTER                      ║
  ╠══════════════════════════════════════════════════════════════╣
  ║ APPLICATION.YML "MUST HAVE" THIS 'YAML-FILE-CONVERTER'       ║
  ║ APPLICATION.PROPERTIES "NO NEED" THIS 'YAML-FILE-CONVERTER'  ║
  ║ Source: https://www.baeldung.com/spring-yaml-propertysource  ║
  ╚══════════════════════════════════════════════════════════════╝*/
public class YamlProcessor implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource)
       throws IOException {

    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(encodedResource.getResource());

    Properties properties = factory.getObject();

    return new PropertiesPropertySource(encodedResource.getResource()
                                                       .getFilename(), properties);
  }
}