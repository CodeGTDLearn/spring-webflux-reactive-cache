package caching;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

@SpringBootTest
@ActiveProfiles("cache")
public class ItemServiceTest {


  @Autowired
  ItemService itemService;

  final static MongoDBContainer mongoDBContainer = new MongoDBContainer(
       DockerImageName.parse("mongo:4.0.10"));

  @DynamicPropertySource
  static void mongoDbProperties(DynamicPropertyRegistry registry) {

    mongoDBContainer.start();
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Test
  public void givenItem_whenGetItemIsCalled_thenMonoIsCached() {

    Mono<Item> glass = itemService.save(new Item("glass", 1.00));

    String id = glass.block()
                     .get_id();

    Mono<Item> mono = itemService.getItem(id);
    Item item = mono.block();

    Assertions.assertThat(item)
              .isNotNull();
    Assertions.assertThat(item.getName())
              .isEqualTo("glass");
    Assertions.assertThat(item.getPrice())
              .isEqualTo(1.00);

    Mono<Item> mono2 = itemService.getItem(id);
    Item item2 = mono2.block();

    Assertions.assertThat(item2)
              .isNotNull();
    Assertions.assertThat(item2.getName())
              .isEqualTo("glass");
    Assertions.assertThat(item2.getPrice())
              .isEqualTo(1.00);
  }

  @Test
  public void givenItem_whenGetItemWithCacheIsCalled_thenMonoResultIsCached() {

    Mono<Item> glass = itemService.save(new Item("glass", 1.00));

    String id = glass.block()
                     .get_id();

    Mono<Item> mono = itemService.getItem_withCache(id);
    Item item = mono.block();

    Assertions.assertThat(item)
              .isNotNull();
    Assertions.assertThat(item.getName())
              .isEqualTo("glass");
    Assertions.assertThat(item.getPrice())
              .isEqualTo(1.00);

    Mono<Item> mono2 = itemService.getItem_withCache(id);
    Item item2 = mono2.block();

    Assertions.assertThat(item2)
              .isNotNull();
    Assertions.assertThat(item2.getName())
              .isEqualTo("glass");
    Assertions.assertThat(item2.getPrice())
              .isEqualTo(1.00);
  }

  //    @Test
  //    public void givenItem_whenGetItemWithAddonsIsCalled_thenMonoResultIsCached() {
  //        Mono<Item> glass = itemService.save(new Item("glass", 1.00));
  //
  //        String id = glass.block().get_id();
  //
  //        Mono<Item> mono = itemService.getItem_withAddons(id);
  //        Item item = mono.block();
  //
  //        Assertions.assertThat(item).isNotNull();
  //        Assertions.assertThat(item.getName()).isEqualTo("glass");
  //        Assertions.assertThat(item.getPrice()).isEqualTo(1.00);
  //
  //        Mono<Item> mono2 = itemService.getItem_withAddons(id);
  //        Item item2 = mono2.block();
  //
  //        Assertions.assertThat(item2).isNotNull();
  //        Assertions.assertThat(item2.getName()).isEqualTo("glass");
  //        Assertions.assertThat(item2.getPrice()).isEqualTo(1.00);
  //    }

}