package caching.config.utils;

import caching.item.Item;
import caching.item.ItemDAOCrud;
import caching.item.ItemDAOTempl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestDbUtils {

  @Autowired
  private ItemDAOCrud daoCrud;

  @Autowired
  private ItemDAOTempl daoTempl;


  public <E> void countAndExecuteFlux(Flux<E> flux, int total) {

    StepVerifier
         .create(flux)
         .expectSubscription()
         .expectNextCount(total)
         .verifyComplete();
  }

  public Flux<Item> cleanDbAndSaveList(List<Item> list) {

    return daoCrud.deleteAll()
                  .thenMany(Flux.fromIterable(list))
                  .flatMap(daoCrud::save)
                  .doOnNext(item -> daoCrud.findAll())
                  .doOnNext(item -> System.out.printf(
                       """
                            >=> FindAll DB Elements >=>
                            >=> Saved 'Item' in DB:
                                |> ID: %s
                                |> Name: %s

                            """
                            ,
                            item.get_id(),
                            item.getName()
                                                          ));
  }


  public <E> void checkFluxListElements(Flux<E> listFlux, List<E> listCompare) {

    StepVerifier.create(listFlux)
                .recordWith(ArrayList::new)
                .expectNextCount(listCompare.size())
                .thenConsumeWhile(listCompare::equals)
                .verifyComplete();
  }

  public void cleanTestDb() {

    StepVerifier
         .create(daoTempl.dropCollectionsTemplate())
         .expectSubscription()
         .verifyComplete();

    System.out.println(
         """
              >==================================================>
              >===============> CLEAN-DB-TO-TEST >===============>
              >==================================================>

              """
                      );
  }

}