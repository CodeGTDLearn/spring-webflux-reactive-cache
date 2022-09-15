package caching.config;


import com.mongo.rs.core.YamlProcessor;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@PropertySource(value = "classpath:application.yml", factory = YamlProcessor.class)
@ConfigurationProperties(prefix = "db.mongodb.replicaset")
@Setter
@Getter
@Profile("rs")
@Import({TransactionManagerConfig.class})
@Slf4j
@Configuration
@EnableReactiveMongoRepositories(basePackages = {"com.mongo.rs.modules.user"})
public class ItemConfig extends AbstractReactiveMongoConfiguration {

  private String rootUri;
  private String database;
  private String replicasetName;
  private String authenticationDatabase;
  private String username;
  private String password;

  @Autowired
  private Environment environment;

  @Override
  public MongoClient reactiveMongoClient() {
/*╔═══════════════════════════════════════════════════╗
  ║ DEV-SINGLENODE-REPLICASET-NOAUTH DEVELOPMENT URL  ║
  ╠═══════════════════════════════════════════════════╣
  ║ mongodb://myservice-mongodb:27017/                ║
  ║ ?connect=direct                                   ║
  ║ &replicaSet=singleNodeReplSet                     ║
  ║ &readPreference=primary                           ║
  ╚═══════════════════════════════════════════════════╝*/
    final String
         connection =
         rootUri +
              "/?connect=direct" +
              "&replicaSet=" + replicasetName +
              "&readPreference=primary";

    System.out.println("Profile: "+environment.getActiveProfiles()[0]+" | Uri: " + connection);

    return MongoClients.create(connection);
  }


  @Override
  protected String getDatabaseName() {

    return database;
  }
}