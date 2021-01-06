package me.min.reactiveweb.repository;

import me.min.reactiveweb.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class ItemReactiveRepositoryTest {
    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    void setup() {
        List<Item> items =
                List.of(
                        new Item(null, "SAMSUNG TV", 400.0),
                        new Item(null, "LG TV", 420.0),
                        new Item(null, "APPLE WATCH", 299.9),
                        new Item(null, "BEATS HEADPHONE", 149.99),
                        new Item("ABC", "BOSE HEADPHONE", 149.99)
                );


        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items).log())
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted Item is : " + item.getDescription());
                })
                .blockLast();
    }

    @Test
    void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
//                .assertNext(item -> {
//                    assertThat(item.getId()).isEqualTo("ABC");
//                    assertThat(item.getDescription()).isEqualTo("BOSE HEADPHONE");
//                    assertThat(item.getPrice()).isEqualTo(149.99);
//                })
                .expectNextMatches(item ->
                        item.getId().equals("ABC")
                                && item.getPrice().equals(149.99)
                                && item.getDescription().equals("BOSE HEADPHONE")
                )
                .verifyComplete();
    }

    @Test
    void getItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("SAMSUNG TV").log("findItemByDescription : "))
                .expectSubscription()
                .expectNextMatches(item ->
                        item.getDescription().equals("SAMSUNG TV")
                                && item.getPrice().equals(400.0)
                )
                .verifyComplete();
    }

    @Test
    void saveItem_testcase1() {
        Item item = Item.builder()
                .id(null)
                .description("IPHONE 7+")
                .price(899.99).build();

        StepVerifier.create(itemReactiveRepository.save(item).log("SAVE ITEM : "))
                .expectSubscription()
                .expectNextMatches(item1 -> item.getId() != null
                        && item1.getDescription().equals(item.getDescription())
                        && item1.getPrice().equals(item.getPrice()))
                .verifyComplete();
    }

    @Test
    void saveItem_testcase2() {
        Item item = Item.builder()
                .id(null)
                .description("IPHONE 7+")
                .price(899.99)
                .build();

        itemReactiveRepository.save(item).log("SAVE ITEM : ")
                .doOnNext(item1 -> {
                    assertThat(item1.getId()).isNotNull();
                    assertThat(item1.getDescription()).isEqualTo("IPHONE 7+");
                    assertThat(item1.getPrice()).isEqualTo(899.99);
                })
                .block();


        StepVerifier.create(itemReactiveRepository.findAll().log("FIND ALL ITEM LIST : "))
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void updateItem() {
        Mono<Item> actual = itemReactiveRepository.findByDescription("APPLE WATCH")
                .map(item -> {
                    item.setPrice(item.getPrice() * 2);
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item));

        StepVerifier.create(actual.log()).
                expectSubscription()
                .expectNextMatches(item ->
                        item.getDescription().equals("APPLE WATCH")
                                && item.getPrice().equals(299.9 * 2)
                )
                .verifyComplete();
    }

    @Test
    void deleteItemById_testcase1() {
        String id = "ABC";
        itemReactiveRepository.deleteById(id)
                .doOnNext(Void -> System.out.println("DELTED ITEM ID: " + id))
                .block();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void deleteItemById_testcase2() {
        String id = "ABC";

        Mono<Void> deleteItem = itemReactiveRepository.findById(id)
                .map(Item::getId)
                .flatMap(target -> itemReactiveRepository.deleteById(target));

        StepVerifier.create(deleteItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("NEW ITEM LIST : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void deleteItem_testcase3() {
        Mono<Void> deleted =  itemReactiveRepository.findByDescription("LG TV")
                .flatMap(item -> itemReactiveRepository.delete(item));

        StepVerifier.create(deleted.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("NEW ITEM LIST : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}