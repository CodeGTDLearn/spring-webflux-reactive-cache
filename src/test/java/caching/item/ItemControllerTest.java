package caching.item;


import caching.config.testcontainter.TcContainerReplicaset;
import caching.config.utils.DbUtilsConfig;
import caching.config.utils.TestDbUtils;
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
import static caching.config.databuilders.ItemBuilder.itemWithID;
import static caching.config.databuilders.ItemBuilder.itemWithoutID;
import static caching.config.utils.RestAssureSpecs.*;
import static caching.config.utils.TestUtils.*;
import static io.restassured.module.webtestclient.RestAssuredWebTestClient.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
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
@DisplayName("1 Testcontainer Controller")
@AutoConfigureWebTestClient
@Import({DbUtilsConfig.class})
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = BEFORE_CLASS)
@TestPropertySource("classpath:application.yml")
@ActiveProfiles({"crud-test"})
@TcContainerReplicaset // TEST TRANSACTIONS
@TestMethodOrder(MethodOrderer.DisplayName.class)
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

  final static String LOCAL_HOST = "http://localhost:8080";

  @Autowired
  WebTestClient client;

  @Autowired
  TestDbUtils dbUtils;

  @Autowired
  ItemService itemService;

  private Item item1, item2;
  private Item itemNoId;


  @BeforeAll
  static void beforeAll(TestInfo info) {
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
    globalTestMessage(info.getDisplayName(), "class-start");
    reset();

    requestSpecification = requestSpecsSetPath(LOCAL_HOST + ROOT);
    responseSpecification = responseSpecs();
  }

  @AfterAll
  static void afterAll(TestInfo info) {

    globalAfterAll();
    globalTestMessage(info.getDisplayName(), "class-end");
    //    closeTcContainer();
  }

  @BeforeEach
  void beforeEach(TestInfo info) {

    globalTestMessage(info.getDisplayName(), "method-start");

    item1 = itemWithoutID().create();
    item2 = itemWithoutID().create();
    List<Item> itemList = asList(item1, item2);
    Flux<Item> itemFlux = dbUtils.cleanDbAndSaveListElements(itemList);

    dbUtils.countAndExecuteFlux(itemFlux, 2);

  }

  @AfterEach
  void afterEach(TestInfo info) {

    globalTestMessage(info.getDisplayName(), "method-end");
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("1 Delete")
  public void delete() {

    responseSpecification = noContentTypeAndVoidResponses();

    dbUtils.countAndExecuteFlux(itemService.findAll(), 2);

    given()
         .webTestClient(client)

         .when()
         .delete(DELETE, item1.get_id())

         .then()
         .log()
         .everything()

         .statusCode(NO_CONTENT.value());

    dbUtils.countAndExecuteFlux(itemService.findAll(), 1);
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("2 FindAll")
  public void findAll() {

    dbUtils.checkFluxListElements(itemService.findAll()
                                             .flatMap(Flux::just), asList(item1, item2));

    given()
         .webTestClient(client)

         .when()
         .get(FIND_ALL)

         .then()
         .log()
         .everything()

         .statusCode(OK.value())
         .body("size()", is(2))
         .body("$", hasSize(2))
         .body("name", hasItems(item1.getName(), item1.getName()))
    //         .body(matchesJsonSchemaInClasspath("contracts/getAll.json"))
    ;

    dbUtils.countAndExecuteFlux(itemService.findAll(), 2);
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("3 FindById")
  public void findById() {

    given()
         .webTestClient(client)

         .when()
         .get(FIND_BY_ID, item1.get_id())

         .then()
         .log()
         .everything()

         .statusCode(OK.value())
         .body("_id", equalTo(item1.get_id()))
         .body("name", equalTo(item1.getName()))

    //         .body(matchesJsonSchemaInClasspath("contracts/project/findbyid.json"))
    ;
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)

  @DisplayName("4 Save")
  public void save() {

    itemNoId = itemWithoutID().create();

    given()
         .webTestClient(client)
         .body(itemNoId)

         .when()
         .post(SAVE)

         .then()
         .log()
         .everything()

         .statusCode(CREATED.value())
         .body("name", equalTo(itemNoId.getName()))
    //         .body(matchesJsonSchemaInClasspath("contracts/transaction.json"))
    ;

    dbUtils.countAndExecuteFlux(itemService.findAll(), 3);
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("6 SaveWithID")
  public void saveWithID() {

    Item userIsolated = itemWithID().create();

    given()
         .webTestClient(client)

         .body(userIsolated)

         .when()
         .post(SAVE)

         .then()
         .log()
         .everything()

         .statusCode(CREATED.value())
         .body("name", equalTo(userIsolated.getName()))
         .body("version", is(0))
    // .body(matchesJsonSchemaInClasspath("contracts/save.json"))
    ;

    dbUtils.countAndExecuteFlux(itemService.findAll(), 3);
  }

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("1 Global Exception")
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

  @Test
  @EnabledIf(expression = enabledTest, loadContext = true)
  @DisplayName("7 UpdateOptim")
  public void updateOptim() {
    // OPTMISTIC-LOCKING-UPDATE:
    // A) Uses the 'VERSION-ANNOTATION' in THE Entity
    // B) to prevent update-problems when happens 'CONCURRENT-UPDATES'
    // C) EXPLANATION:
    //  C.1) The ENTITY-VERSION in the UPDATING-OBJECT
    //  C.2) must be the same ENTITY-VERSION than the DB-OBJECT
    // DB-OBJECT-VERSION should be the same as the OBJECT-TO-BE-UPDATED
    var previousVersion = item1.getVersion();
    var updatedVersion = previousVersion + 1;

    var previousName = item1.getName();
    var newName = "NewName";
    item1.setName(newName);

    given()
         .webTestClient(client)

         .body(item1)

         .when()
         .put(UPDATE)

         .then()
         .log()
         .everything()

         .statusCode(OK.value())
         .body("name", not(equalTo(previousName)))
         .body("name", equalTo(newName))
         .body("version", not(equalTo(previousVersion)))
         .body("version", hasToString(Long.toString(updatedVersion)))

    //         .body(matchesJsonSchemaInClasspath("contracts/saveOrUpdate.json"))
    ;
  }

  //  @Test
  //  @EnabledIf(expression = enabledTest, loadContext = true)
  //  @DisplayName("Blockhound")
  //  public void blockHoundWorks() {
  //    blockHoundTestCheck();
  //  }
  //@org.junit.Test
  //public void saveall_transaction_rollback() {
  //  List<Anime> listAnime = Arrays.asList(anime_1 , anime_2);
  //
  //  RestAssuredWebTestClient
  //       .given()
  //       .webTestClient(webTestClient)
  //       .header("Accept" , ContentType.ANY)
  //       .header(RoleUsersHeaders.role_admin_header)
  //       .body(listAnime)
  //
  //       .when()
  //       .post("/saveall_rollback")
  //
  //       .then()
  //       .contentType(ContentType.JSON)
  //       .statusCode(CREATED.value())
  //       .log().headers().and()
  //       .log().body().and()
  //
  //       .body("size()" ,is(listAnime.size()))
  //       .body("name" ,hasItems(anime_1.getName() ,anime_2.getName()))
  //  ;
  //}
  //
  //  @org.junit.Test
  //  public void saveall_transaction_rollback_ERROR() {
  //    List<Anime> listAnime = Arrays.asList(anime_1 ,anime_2);
  //
  //    RestAssuredWebTestClient
  //         .given()
  //         .webTestClient(webTestClient)
  //         .header("Accept" ,ContentType.ANY)
  //         .header(RoleUsersHeaders.role_admin_header)
  //         .body(listAnime)
  //
  //         .when()
  //         .post("/saveall_rollback")
  //
  //         .then()
  //         .contentType(ContentType.JSON)
  //         .statusCode(BAD_REQUEST.value())
  //         .log().headers().and()
  //         .log().body().and()
  //
  //         .body("developerMensagem" ,is("A ResponseStatusException happened!!!"))
  //    ;
  //  }
}