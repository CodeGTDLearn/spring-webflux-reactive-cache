//package caching.item;
//
//import io.restassured.http.ContentType;
//import io.restassured.module.webtestclient.RestAssuredWebTestClient;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.reactive.server.EntityExchangeResult;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.blockhound.BlockingOperationError;
//import reactor.core.publisher.Flux;
//import reactor.core.scheduler.Schedulers;
//import reactor.test.StepVerifier;
//
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.FutureTask;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//import static org.hamcrest.Matchers.containsString;
//import static org.hamcrest.Matchers.stringContainsInOrder;
//import static org.junit.Assert.assertEquals;
//import static org.springframework.http.HttpStatus.OK;
//
//public class Controller extends ControllersConfig {
//
//  //DEFAULT: WEB-TEST-CLIENT WITH MOCK-SERVER
//  @Autowired
//  WebTestClient client;
//
//  final MediaType MTYPE_JSON = MediaType.APPLICATION_JSON;
//
//  @Before
//  public void setUpLocal() {
//
//    client = client
//         .mutate()
//         .responseTimeout(Duration.ofMillis(75000L))
//         .build();
//    //REAL-SERVER(non-blocking client)  IN WEB-TEST-CLIENT:
//    //webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:8080/dilipi")
//    // .build();
//  }
//
//  @Ignore
//  @Test
//  public void bHWorks() {
//
//    try {
//      FutureTask<?> task = new FutureTask<>(() -> {
//        Thread.sleep(0);
//        return "";
//      });
//
//      Schedulers.parallel()
//                .schedule(task);
//
//      task.get(10, TimeUnit.SECONDS);
//      Assert.fail("should fail");
//    }
//    catch (ExecutionException | InterruptedException | TimeoutException e) {
//      Assert.assertTrue("detected", e.getCause() instanceof BlockingOperationError);
//    }
//  }
//
//  @Test
//  public void StepVerifier() {
//
//    Flux<Integer> integerFlux = client
//         .get()
//         .uri("/dilipi/flux")
//         .accept(MTYPE_JSON)
//         .exchange()
//         .expectStatus()
//         .isOk()
//         .returnResult(Integer.class)
//         .getResponseBody();
//
//    StepVerifier
//         .create(integerFlux)
//         .expectSubscription()
//         .expectNext(1)
//         .expectNext(2)
//         .expectNext(3)
//         .verifyComplete();
//  }
//
//  @Test
//  public void HasSize() {
//
//    client
//         .get()
//         .uri("/dilipi/flux")
//         .accept(MTYPE_JSON)
//         .exchange()
//         .expectStatus()
//         .isOk()
//         .expectHeader()
//         .contentType(MTYPE_JSON)
//         .expectBodyList(Integer.class)
//         .hasSize(3);
//  }
//
//  @Test
//  public void AssertEquals() {
//
//    List<Integer> expectedList = Arrays.asList(1, 2, 3);
//
//    EntityExchangeResult<List<Integer>> result =
//         client
//              .get()
//              .uri("/dilipi/flux")
//              .accept(MTYPE_JSON)
//              .exchange()
//              .expectStatus()
//              .isOk()
//              .expectBodyList(Integer.class)
//              .returnResult();
//
//    assertEquals(expectedList, result.getResponseBody());
//  }
//
//  @Ignore
//  @Test
//  public void ConsumeWith() {
//
//    List<Integer> expectedList = Arrays.asList(1, 2, 3);
//
//    client
//         .get()
//         .uri("/dilipi/flux")
//         .accept(MTYPE_JSON)
//         .exchange()
//         .expectStatus()
//         .isOk()
//         .expectBodyList(Integer.class)
//         .consumeWith((response) -> assertEquals(expectedList, response.getResponseBody()));
//  }
//
//  @Ignore
//  @Test
//  public void Infinite() {
//
//    Flux<Long> LongFlux = client
//         .get()
//         .uri("/dilipi/flux-stream-infinite")
//         .accept(MTYPE_JSON)
//         .exchange()
//         .expectStatus()
//         .isOk()
//         .returnResult(Long.class)
//         .getResponseBody();
//
//    StepVerifier
//         .create(LongFlux)
//         .expectSubscription()
//         .expectNext(0L)
//         .expectNext(1L)
//         .expectNext(2L)
//         .thenCancel()
//         .verify();
//  }
//
//  @Test
//  public void Mono() {
//
//    Integer expectedValue = 1;
//
//    client
//         .get()
//         .uri("/dilipi/mono")
//         .accept(MTYPE_JSON)
//         .exchange()
//         .expectStatus()
//         .isOk()
//         .expectBody(Integer.class)
//         .consumeWith((response) -> {
//           assertEquals(expectedValue, response.getResponseBody());
//         });
//  }
//
//  @Test
//  public void RA() {
//
//    RestAssuredWebTestClient
//         .given()
//         .webTestClient(client)
//         .header("Accept", ContentType.ANY)
//         .header("Content-type", ContentType.JSON)
//
//         .when()
//         .get("flux")
//
//         .then()
//         .statusCode(OK.value())
//         .log()
//         .headers()
//         .and()
//         .log()
//         .body()
//         .and()
//
//         .body(containsString("1"))
//         .body(stringContainsInOrder("1", "2", "3"))
//    ;
//  }
//}