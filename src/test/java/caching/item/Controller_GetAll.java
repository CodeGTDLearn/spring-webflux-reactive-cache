//package caching.item;
//
//
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
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Flux;
//import reactor.test.StepVerifier;
//import reactor.test.scheduler.VirtualTimeScheduler;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static com.reactive.spring.config.MappingsController_v1_CRUD.REQ_MAP;
//import static com.reactive.spring.config.MappingsController_v1_CRUD.VERSION;
//import static com.reactive.spring.databuilder.ObjectMotherItem.newItemWithDescPrice;
//import static com.reactive.spring.databuilder.ObjectMotherItem.newItemWithIdDescPrice;
//import static org.junit.Assert.assertTrue;
//import static org.springframework.http.HttpStatus.OK;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@DirtiesContext
//@ActiveProfiles("test")
//@AutoConfigureWebTestClient(timeout = "30000")
//public class Controller_GetAll {
//
//    @Autowired
//    WebTestClient client;
//
//    private List<Item> itemList;
//    private Item item;
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
//        VirtualTimeScheduler.getOrSet();
//
//        item = newItemWithIdDescPrice("ABC").create();
//
//        itemList = Arrays.asList(newItemWithDescPrice().create(),
//                                 newItemWithDescPrice().create(),
//                                 newItemWithDescPrice().create(),
//                                 item
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
//    public void HasSize() {
//        client
//
//                .get()
//                .uri(VERSION + REQ_MAP)
//                .exchange()
//                .expectHeader()
//                .contentType(MTYPE_JSON)
//                .expectBodyList(Item.class)
//
//                .hasSize(4);
//    }
//
//    @Test
//    public void ConsumesWith() {
//        client
//                .get()
//                .uri(VERSION + REQ_MAP)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectHeader()
//                .contentType(MTYPE_JSON)
//                .expectBodyList(Item.class)
//                .hasSize(4)
//                .consumeWith((response) -> {
//                    List<Item> listItems = response.getResponseBody();
//                    listItems.forEach((item) -> assertTrue(item.getId() != null));
//                });
//    }
//
//    @Test
//    public void StepVerifier() {
//        Flux<Item> itemFlux =
//                client
//                        .get()
//                        .uri(VERSION + REQ_MAP)
//                        .exchange()
//                        .expectStatus()
//                        .isOk()
//                        .expectHeader()
//                        .contentType(MTYPE_JSON)
//                        .returnResult(Item.class)
//                        .getResponseBody();
//
//        StepVerifier
//                .create(itemFlux.log("Value from Network - StepVerifier: "))
//                .expectNextCount(4)
//                .verifyComplete();
//
//
//    }
//
//    @Test
//    public void RA() {
//        RestAssuredWebTestClient
//                .given()
//                .webTestClient(client)
//                .header("Accept",CONT_ANY)
//                .header("Content-type",CONT_JSON)
//
//                .when()
//                .get(VERSION + REQ_MAP)
//
//                .then()
//                .statusCode(OK.value())
//                .log()
//                .headers()
//                .and()
//                .log()
//                .body()
//                .and()
//
//                .body("id",hasItem(item.getId()))
//                .body("description",hasItem(item.getDescription()))
//                .body("id",hasItem(itemList.get(0)
//                                           .getId()))
//                .body("id",hasItem(itemList.get(1)
//                                           .getId()))
//                .body("id",hasItem(itemList.get(2)
//                                           .getId()))
//
//        ;
//    }
//}