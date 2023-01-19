//package caching.item;
//
//
//import com.github.javafaker.Faker;
//import com.reactive.spring.repo.ItemRepo;
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
//
//import java.util.Arrays;
//import java.util.List;
//
//import static com.github.javafaker.Faker.instance;
//import static com.reactive.spring.config.MappingsController_v1_CRUD.*;
//import static com.reactive.spring.databuilder.ObjectMotherItem.newItemWithDescPrice;
//import static com.reactive.spring.databuilder.ObjectMotherItem.newItemWithIdDescPrice;
//import static org.springframework.http.HttpStatus.OK;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@DirtiesContext
//@AutoConfigureWebTestClient
//@ActiveProfiles("test")
//public class Controller_GetById {
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
//
//    @Before
//    public void setUpLocal() {
//
//        itemTest = newItemWithIdDescPrice(Faker.instance().idNumber().valid()).create();
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
//    public void jsonPath() {
//        client
//                .get()
//                .uri(VERSION + REQ_MAP + ID_PATH,itemTest.getId())
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectHeader()
//                .contentType(MTYPE_JSON)
//                .expectBody()
//                .jsonPath("$.price",itemTest.getPrice());
//    }
//
//    @Test
//    public void jsonPath_notfound() {
//        client
//                .get()
//                .uri(VERSION + REQ_MAP + ID_PATH,instance().idNumber()
//                                                           .valid())
//                .exchange()
//                .expectStatus()
//                .isNotFound();
//    }
//
//    @Test
//    public void RA() {
//        RestAssuredWebTestClient
//                .given()
//                .webTestClient(client)
//
//                .when()
//                .get(VERSION + REQ_MAP + ID_PATH,itemTest.getId())
//
//                .then()
//                .statusCode(OK.value())
//
//                .body("description",is(itemTest.getDescription()))
//        ;
//    }
//}