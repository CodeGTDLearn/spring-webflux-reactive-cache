package caching.item;

import caching.config.testcontainter.TcContainerReplicaset;
import caching.config.utils.DbUtilsConfig;
import caching.config.utils.TestDbUtils;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static caching.config.databuilders.ItemBuilder.itemWithoutID;
import static caching.config.utils.TestUtils.*;
import static java.util.Arrays.asList;

@DisplayName("1 Testcontainer Service")
@Import({DbUtilsConfig.class})
@TestPropertySource("classpath:application.yml")
@ActiveProfiles({"crud-test"})
@TcContainerReplicaset
@TestMethodOrder(MethodOrderer.DisplayName.class)
class ItemServiceImplTest {

  final static String enabledTest = "true";

  @Autowired
  TestDbUtils dbUtils;

  @Autowired
  ItemService itemService;

  private Item item1, item2;
  private Item itemNoId;

  @BeforeAll
  static void beforeAll(TestInfo testInfo) {
    /*╔══════════════════════════╗
      ║        BLOCKHOUND        ║
      ╠══════════════════════════╣
      ║ Possible 'False-Positive ║
      ║    Out-date in GitHub    ║
      ╚══════════════════════════╝*/
    //    blockhoundInstallWithSpecificAllowedCalls();
    //    blockhoundInstallWithAllAllowedCalls();

    System.clearProperty("runTest");
    System.setProperty("runTest", enabledTest);

    globalBeforeAll();
    globalTestMessage(testInfo.getDisplayName(), "class-start");
  }

  @AfterAll
  static void afterAll(TestInfo testInfo) {

    globalAfterAll();
    globalTestMessage(testInfo.getDisplayName(), "class-end");
  }

  @BeforeEach
  void beforeEach(TestInfo testInfo) {

    globalTestMessage(testInfo.getTestMethod()
                              .toString(), "method-start");

    item1 = itemWithoutID().create();
    item2 = itemWithoutID().create();
    List<Item> itemList = asList(item1, item2);
    Flux<Item> itemFlux = dbUtils.cleanDbAndSaveListElements(itemList);

    dbUtils.countAndExecuteFlux(itemFlux, 2);

  }

  @AfterEach
  void tearDown(TestInfo testInfo) {

    globalTestMessage(testInfo.getTestMethod()
                              .toString(), "method-end");
  }


  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("1 Delete")
  public void Delete() {

    dbUtils.countAndExecuteFlux(itemService.findAll(), 2);

    Mono<Void> deletedItem = itemService.delete(item1.get_id());

    StepVerifier
         .create(deletedItem.log())
         .expectSubscription()
         .verifyComplete();

    dbUtils.countAndExecuteFlux(itemService.findAll(), 1);

  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("2 FindAll")
  public void FindAll() {

    StepVerifier
         .create(itemService.findAll())
         .expectSubscription()
         .expectNextCount(2)
         .verifyComplete();

    dbUtils.cleanTestDb();

    StepVerifier
         .create(itemService.findAll())
         .expectSubscription()
         .expectNextCount(0L)
         .verifyComplete();
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("3 FindById")
  public void FindById() {

    Mono<Item> itemFoundById =
         itemService
              .findById(item1.get_id())
              .map(itemFound -> itemFound);

    StepVerifier
         .create(itemFoundById)
         .expectSubscription()
         .expectNextMatches(found -> found.get_id()
                                          .equals(item1.get_id()))
         .verifyComplete();
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("4 Save")
  public void Save() {

    dbUtils.cleanTestDb();

    StepVerifier
         .create(itemService.save(item1))
         .expectSubscription()
         .expectNext(item1)
         .verifyComplete();
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("5 Update")
  public void Update() {

    var newName = Faker.instance()
                       .name()
                       .fullName();

    Mono<Item> updatedItem =
         itemService
              .findById(item1.get_id())
              .map(itemFound -> {
                itemFound.setName(newName);
                return itemFound;
              })
              .flatMap(
                   checkedItem -> itemService.update(checkedItem));

    StepVerifier
         .create(updatedItem)
         .expectSubscription()
         .expectNextMatches(
              user -> user.getName()
                          .equals(newName))
         .verifyComplete();
  }
}