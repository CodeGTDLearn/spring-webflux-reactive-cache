package caching.item;


import caching.config.testcontainter.TcContainerReplicaset;
import caching.config.utils.DbUtilsConfig;
import caching.config.utils.TestDbUtils;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static caching.config.ItemRoutes.*;
import static caching.config.databuilders.ItemBuilder.itemWithoutID;
import static caching.config.utils.RestAssureSpecs.requestSpecsSetPath;
import static caching.config.utils.RestAssureSpecs.responseSpecs;
import static caching.config.utils.TestUtils.*;
import static io.restassured.module.webtestclient.RestAssuredWebTestClient.given;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
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
@DisplayName("1 Testcontainer Controller Exceptions")
@AutoConfigureWebTestClient
@Import({DbUtilsConfig.class})
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = BEFORE_CLASS)
@TestPropertySource("classpath:application.yml")
@ActiveProfiles({"crud-test"})
@TcContainerReplicaset // TEST TRANSACTIONS
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class ItemControllerExceptionsTest {
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
  WebTestClient client;

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
    RestAssuredWebTestClient.reset();
    RestAssuredWebTestClient.requestSpecification = requestSpecsSetPath(
         "http://localhost:8080" + ROOT);
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
  @DisplayName("1 Global Exception Trace")
  public void globalExceptionTrace() {

    Item itemNoName = itemWithoutID().create();
    itemNoName.setName("");

    given()
         .webTestClient(client)
         .body(itemNoName)

         .when()
         .post(SAVE + "?completeStackTrace=true")

         .then()
         .log()
         .everything()

         .statusCode(BAD_REQUEST.value())
         .body("trace",
               allOf(startsWith("org.springframework.web.bind.support.WebExchangeBindException"),
                     containsString("Validation failed for argument at index 0")
               )
         )
    //         .body(matchesJsonSchemaInClasspath("contracts/exception.json"))
    ;

    dbUtils.countAndExecuteFlux(itemService.findAll(), 2);
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("2 Global Exception No Trace")
  public void globalExceptionNoTrace() {

    Item itemNoName = itemWithoutID().create();
    itemNoName.setName("");

    given()
         .webTestClient(client)
         .body(itemNoName)

         .when()
         .post(SAVE + "?completeStackTrace=false")

         .then()
         .log()
         .everything()

         .statusCode(BAD_REQUEST.value())
         .body("$", not(hasKey("trace")))
    //         .body(matchesJsonSchemaInClasspath("contracts/exception.json"))
    ;

    dbUtils.countAndExecuteFlux(itemService.findAll(), 2);
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("3 UpdateOptimFail")
  public void updateOptimFail() {

    item1.setName("");

    given()
         .webTestClient(client)

         .body(item1)

         .when()
         .put(UPDATE)

         .then()
         .log()
         .everything()

         .statusCode(BAD_REQUEST.value())

    //         .body(matchesJsonSchemaInClasspath("contracts/saveOrUpdate.json"))
    ;
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("4 Save Rollback Exception")
  public void saveRollback() {

    Item itemNoName = itemWithoutID().create();
    itemNoName.setName("");

    given()
         .webTestClient(client)
         .body(itemNoName)

         .when()
         .post(SAVE)

         .then()
         .log()
         .everything()

         .statusCode(BAD_REQUEST.value())

    //         .body(matchesJsonSchemaInClasspath("contracts/exception.json"))
    //         .body("developerMensagem" ,is("Item[Fail: Empty Name].notFound"))
    ;

    dbUtils.countAndExecuteFlux(itemService.findAll(), 2);
  }


}