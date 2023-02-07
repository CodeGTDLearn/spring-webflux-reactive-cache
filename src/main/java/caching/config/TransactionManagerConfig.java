package caching.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;

/*╔════════════════════════════════════════════════════╗
  ║                    ATTENTION!!!                    ║
  ╠════════════════════════════════════════════════════╣
  ║  THIS BEAN SHOULD HAVE BE IN "MAIN-CONFIGURATIONS" ║
  ║  THE ONLY REASON FOR THIS BEAN IS HERE IS:         ║
  ║  TEST @Import in in the "MAIN-CONFIGURATIONS"      ║
  ╚════════════════════════════════════════════════════╝*/
@Profile("rs")
@Slf4j
@Configuration
public class TransactionManagerConfig {

  @Bean
  ReactiveMongoTransactionManager manager(ReactiveMongoDatabaseFactory factory) {

    return new ReactiveMongoTransactionManager(factory);
  }

}