package caching.item;

import caching.config.TcContainerReplicaset;
import caching.config.utils.TestDbUtils;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static caching.config.databuilders.ItemBuilder.itemNoID;
import static caching.config.utils.RestAssureSpecs.requestSpecsSetPath;
import static caching.config.utils.RestAssureSpecs.responseSpecs;
import static caching.config.utils.TestUtils.*;
import static caching.item.ItemRoutes.ROOT;
import static caching.item.ItemRoutes.SAVE;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

/*   ╔═══════════════════════════════╗
     ║ USING CONTAINERS IN THE TESTS ║
     ╠═══════════════════════════════╩════════════════╗
     ║ CONFLICT: TEST-CONTAINERS X DOCKER-CONTAINERS  ║
     ║           THEY DO NOT WORKS TOGETHER           ║
     ╠════════════════════════════════════════════════╩═════════╗
     ║A) TEST-CONTAINERS:                                       ║
     ║A.1) STOP+CLEAN DOCKER-CONTAINERS  (DOCKER-BAT-SCRIPT)    ║
     ║A.2) SELECT THE TEST-PROFILE FOR TESTS WITH TESTCONTAINERS║
     ║A.3) RUN THE TESTS                                        ║
     ║                                                          ║
     ║B) DOCKER-CONTAINERS (STANDALONE + REPLICASET):           ║
     ║B.1) SELECT THE TEST-PROFILE(dockercontainer's)           ║
     ║B.2) COMMENT @Container Instance variable                 ║
     ║B.3) START DOCKER-CONTAINER (DOCKER-BAT-SCRIPT-PROFILE)   ║
     ║B.4) RUN THE TESTS                                        ║
     ╚══════════════════════════════════════════════════════════╝*/
/*
  ╔══════════════════════════════════════════════════════════════════════╗
  ║    SILAEV + TRANSACTIONS + REPLICASET + TEST-CONTAINER-CONTAINER     ║
  ╠══════════════════════════════════════════════════════════════════════╣
  ║ MongoDBContainer does REPLICASET init automatically:                 ║
  ║  a) Static-Extension-Annotation Class starts automatically           ║
  ║  b) the MongoDBContainer this TcContainer has a Replicaset           ║
  ║  c) This Class gets the URI from this MongoDBContainer               ║
  ║  d) and setting this URI in 'Properties of the Test'                 ║
  ╚══════════════════════════════════════════════════════════════════════╝
*/
@DisplayName("2 Testcontainer Transactions")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = BEFORE_CLASS)
@TestPropertySource("classpath:application.yml")
@ActiveProfiles({"cache-test"})
@TcContainerReplicaset // TEST TRANSACTIONS
public class ItemControllerTest {
  /*
╔════════════════════════════════════════════════════════════╗
║              TEST-TRANSACTIONS + TEST-CONTAINERS           ║
╠════════════════════════════════════════════════════════════╣
║ a) TRANSACTIONS IN MONGO-DB DEPENDS ON THE REPLICASET      ║
║    - MEANING: TRANSACTIONS ONLY WILL WORK WITH REPLICASET  ║
║                                                            ║
║ b) MongoDBContainer provides REPLICASET automatically      ║
║    - MEANING:                                              ║
║      B.1) TESTS MUST BE DONE WITH "MongoDBContainer"       ║
║      B.2) DO NOT USE TEST-CONTAINER-DOCKER-COMPOSE-MODULE  ║
╚════════════════════════════════════════════════════════════╝
*/

  final static String enabledTest = "true";

  @Autowired
  WebTestClient mockedWebClient;

  @Autowired
  TestDbUtils dbUtils;

  @Autowired
  ItemService itemService;

  Item item1;
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
    RestAssuredWebTestClient.reset();
    RestAssuredWebTestClient.requestSpecification =
         requestSpecsSetPath("http://localhost:8080" + ROOT);
    RestAssuredWebTestClient.responseSpecification = responseSpecs();
  }

  @AfterAll
  static void afterAll(TestInfo testInfo) {

    globalAfterAll();
    globalTestMessage(testInfo.getDisplayName(), "class-end");
//    closeTcContainer();
  }

  @BeforeEach
  void beforeEach(TestInfo testInfo) {

    globalTestMessage(testInfo.getTestMethod()
                              .toString(), "method-start");

    item1 = itemNoID().create();

    Item user2 = itemNoID().create();

    List<Item> itemList = asList(item1, user2);
    Flux<Item> userFlux = dbUtils.cleanDbAndSaveList(itemList);

    dbUtils.countAndExecuteFlux(userFlux, 2);

  }

  @AfterEach
  void tearDown(TestInfo testInfo) {

    globalTestMessage(testInfo.getTestMethod()
                              .toString(), "method-end");
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @Tag("replicaset-transaction")
  @DisplayName("2 saveRollback")
  public void saveRollback() {

    itemNoId = itemNoId().create();
    Item lastUser = itemNoId().create();
    lastUser.setName("");
    List<Item> userList = asList(itemNoId, lastUser);

    RestAssuredWebTestClient
         .given()
         .webTestClient(mockedWebClient)
         .body(userList)

         .when()
         .post(SAVE)

         .then()
         .log()
         .everything()

         .statusCode(BAD_REQUEST.value())

//         .body(matchesJsonSchemaInClasspath("contracts/exception.json"))
    ;

    dbUtils.countAndExecuteFlux(itemService.getAll(), 2);
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @Tag("replicaset-transaction")
  @DisplayName("1 NoRollback")
  public void saveNoRollback() {

    itemNoId = itemNoId().create();
    Item lastUser = itemNoId().create();
    List<Item> userList = asList(itemNoId, lastUser);

    RestAssuredWebTestClient
         .given()
         .webTestClient(mockedWebClient)
         .body(userList)

         .when()
         .post(SAVE)

         .then()
         .log()
         .everything()

         .statusCode(CREATED.value())
         .body("size()", is(2))
         .body("$", hasSize(2))
         .body("name", hasItems(
              itemNoId.getName(),
              lastUser.getName()
                               ))
         .body(matchesJsonSchemaInClasspath("contracts/transaction.json"))
    ;

    dbUtils.countAndExecuteFlux(itemService.getAll(), 4);
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @Tags(value = {
       @Tag("replicaset-transaction"),
       @Tag("standalone")})
  @DisplayName("3 saveWithID")
  public void saveWithID() {

    Item userIsolated = itemNoID().create();

    RestAssuredWebTestClient
         .given()
         .webTestClient(mockedWebClient)

         .body(userIsolated)

         .when()
         .post(SAVE)

         .then()
         .log()
         .everything()

         .statusCode(CREATED.value())
         .body("name", equalTo(userIsolated.getName()))
         .body(matchesJsonSchemaInClasspath("contracts/save.json"))
    ;

    dbUtils.countAndExecuteFlux(itemService.getAll(), 3);
  }

  private String getTestProfiles(String[] profiles, String profile) {

    final String valueOf = String.valueOf(Arrays.stream(profiles)
                                                .anyMatch(str -> str.equals(profile)));
    return valueOf;
    //    final String valueOf = String.valueOf(Arrays.stream(profiles)
    //                                          .filter(str -> str.equals(profile)));
    //    return valueOf;

  }

}