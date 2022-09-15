package caching.item;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service("itemService")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDAOCrud itemDAOCrud;
//    private final LoadingCache<String, Object> cache;

//    public ItemServiceImpl(ItemDAO dao) {
//        this.repository = dao;
//        this.cache = Caffeine.newBuilder()                .build(this::getItem_withAddons);
//    }

    @Override
    @Cacheable("items")
    public Mono<Item> getItem(String id) {
        return itemDAOCrud.findById(id);
    }

    @Override
    public Mono<Item> save(Item item) {
        return itemDAOCrud.save(item);
    }

    @Override
    public Flux<Item> getAll() {

        return itemDAOCrud.findAll();
    }

    @Override
    @Cacheable("items")
    public Mono<Item> getItem_withCache(String id) {
        return itemDAOCrud.findById(id).cache();
    }

//    @Cacheable("items")
//    public Mono<Item> getItem_withAddons(String id) {
//        return CacheMono.lookup(cache.asMap(), id)
//                .onCacheMissResume(() -> repository.findById(id).cast(Object.class)).cast(Item.class);
//    }

}