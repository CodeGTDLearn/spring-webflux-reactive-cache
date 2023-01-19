//package caching.item;
//
//
//import com.github.javafaker.Faker;
//import com.reactive.spring.repo.ItemRepo;
//import io.restassured.http.ContentType;
//import io.restassured.module.webtestclient.RestAssuredWebTestClient;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static com.reactive.spring.config.MappingsController_v1_CRUD.REQ_MAP;
//import static com.reactive.spring.config.MappingsController_v1_CRUD.VERSION;
//import static com.reactive.spring.databuilder.ObjectMotherItem.newItemWithDescPrice;
//import static com.reactive.spring.databuilder.ObjectMotherItem.newItemWithIdDescPrice;
//import static org.hamcrest.Matchers.containsString;
//import static org.springframework.http.HttpStatus.CREATED;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@AutoConfigureWebTestClient
////@DirtiesContext
////@ActiveProfiles("test")
//public class Controller_Save {
//
//    @Autowired
//    WebTestClient client;
//
//    private List<Item> itemList;
//    private Item itemTest;
//
//    @Autowired
//    ItemRepo repo;
//
//    final MediaType MTYPE_JSON = MediaType.APPLICATION_JSON;
//    final ContentType CONT_ANY = ContentType.ANY;
//    final ContentType CONT_JSON = ContentType.JSON;
//
//    @Before
//    public void setUpLocal() {
//
//        itemTest = newItemWithIdDescPrice(Faker.instance()
//                                               .idNumber()
//                                               .valid()).create();
//
//        itemList = Arrays.asList(newItemWithDescPrice().create(),
//                                 newItemWithDescPrice().create(),
//                                 newItemWithDescPrice().create(),
//                                 itemTest
//                                );
//
//        repo.deleteAll()
//            .thenMany(Flux.fromIterable(itemList))
//            .flatMap(repo::save)
//            .doOnNext((item -> System.out.println("Inserted item is - TEST: " + item)))
//            .blockLast(); // THATS THE WHY, BLOCKHOUND IS NOT BEING USED.
//    }
//
//    @Test
//    public void webTestClient() {
//        client
//                .post()
//                .uri(VERSION + REQ_MAP)
//                .body(Mono.just(itemTest),Item.class)
//                .exchange()
//                .expectStatus()
//                .isCreated()
//                .expectHeader()
//                .contentType(MTYPE_JSON)
//                .expectBody()
//                .jsonPath("$.id")
//                .isEqualTo(itemTest.getId())
//                .jsonPath("$.price")
//                .isEqualTo(itemTest.getPrice())
//                .jsonPath("$.description")
//                .isEqualTo(itemTest.getDescription())
//        ;
//    }
//
//    @Test
//    public void RA() {
//        RestAssuredWebTestClient
//                .given()
//                .webTestClient(client)
//                .header("Accept",CONT_ANY)
//                .header("Content-type",CONT_JSON)
//                .body(itemTest)
//
//                .when()
//                .post(VERSION + REQ_MAP)
//
//                .then()
//                .log()
//                .headers()
//                .and()
//                .log()
//                .body()
//                .and()
//                .contentType(CONT_JSON)
//                .statusCode(CREATED.value())
//
//                //equalTo para o corpo do Json
//                .body("description",containsString(itemTest.getDescription()));
//    }
//}