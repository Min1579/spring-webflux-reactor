package me.min.reactiveweb.handler;

import me.min.reactiveweb.document.Item;
import me.min.reactiveweb.payload.ItemDTO;
import me.min.reactiveweb.payload.ItemUpdateDTO;
import me.min.reactiveweb.repository.ItemReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static me.min.reactiveweb.constants.ItemConstants.*;
import static org.assertj.core.api.Assertions.*;

@AutoConfigureWebTestClient
@SpringBootTest
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ItemHandlerTests {
    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        Flux<Item> items = Flux.fromIterable(List.of(
                new Item(null, "SAMSUNG TV", 400.0),
                new Item(null, "LG TV", 420.0),
                new Item(null, "APPLE WATCH", 299.9),
                new Item(null, "BEATS HEADPHONE", 149.99),
                new Item("ABC", "BOSE HEADPHONE", 149.99)
        ));

        itemReactiveRepository.deleteAll()
                .thenMany(items)
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted Item is : " + item.getDescription());
                })
                .blockLast();
    }

    @AfterEach
    void after() {
        itemReactiveRepository.findAll()
                .doOnNext(item ->
                    System.out.println("after item : " + item.getDescription())
        ).blockLast();
    }

    @Test
    void getAllItemsTest_testcase1() {
        webTestClient.get().uri(ITEMS_END_FUN_POINT_V1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType("application/json")
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    void getAllItemsTest_testcase2() {
        Flux<Item> items = webTestClient.get().uri(ITEMS_END_FUN_POINT_V1)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(items)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void getAllItemsTest_testcase3() {
        webTestClient.get().uri(ITEMS_END_FUN_POINT_V1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType("application/json")
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> {
                    response.getResponseBody().forEach(item -> {
                        assertThat(item).isNotNull();
                        assertThat(item.getId()).isNotNull();
                    });
                });
    }

    @Test
    void getItemById_testcase1() {
        String givenId = "ABC";
        webTestClient.get()
                .uri(ITEM_BY_ID_FUN_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(givenId)
                .jsonPath("$.description")
                .isEqualTo("BOSE HEADPHONE")
                .jsonPath("$.price")
                .isEqualTo(149.99);
    }

    @Test
    void getItemById_testcase2() {
        String givenId = "ABC";

        webTestClient.get()
                .uri(ITEM_BY_ID_FUN_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(Item.class)
                .consumeWith(response -> {
                   Item actual = response.getResponseBody();
                   assertThat(actual.getId()).isEqualTo(givenId);
                   assertThat(actual.getDescription()).isEqualTo("BOSE HEADPHONE");
                   assertThat(actual.getPrice()).isEqualTo(149.99);
                });
    }

    @Test
    void getItemById_testcase3() {
        String givenId = "ABC";

        EntityExchangeResult<Item> itemEntityExchangeResult = webTestClient.get()
                .uri(ITEM_BY_ID_FUN_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(Item.class)
                .returnResult();

        Item actual = itemEntityExchangeResult.getResponseBody();

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(givenId);
        assertThat(actual.getDescription()).isEqualTo("BOSE HEADPHONE");
        assertThat(actual.getPrice()).isEqualTo(149.99);
    }

    @Test
    void registerItemTest_testcase1() {
        String description = "IMAC PRO";
        Double price = 299.88;

        var payload = new ItemDTO(description, price);

        webTestClient.post()
                .uri(ITEM_REGISTER_FUN_END_POINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemDTO.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Item.class)
                .consumeWith(response -> {
                    Item actual = response.getResponseBody();
                    assertThat(actual.getId()).isNotNull();
                    assertThat(actual.getDescription()).isEqualTo(description);
                    assertThat(actual.getPrice()).isEqualTo(price);
                });
    }

    @Test
    void registerItemTest_testcase2() {
        String description = "IMAC PRO";
        Double price = 299.88;

        var payload = new ItemDTO(description, price);

        webTestClient.post()
                .uri(ITEM_REGISTER_FUN_END_POINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemDTO.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id")
                .isNotEmpty()
                .jsonPath("$.description")
                .isEqualTo(description)
                .jsonPath("$.price")
                .isEqualTo(price);
    }

    @Test
    void getItemById_whenIdNotFound() {
        String givenId = "zzzzzz";

        webTestClient.get()
                .uri(ITEM_BY_ID_FUN_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateItem() {
        String givenId = "ABC";
        String description = "MACBOOK 2020";
        Double price = 2000.00;
        // given
        ItemUpdateDTO payload = new ItemUpdateDTO(givenId, description, price);

        webTestClient.put()
                .uri(ITEM_UPDATE_FUN_END_POINT_V1.concat("{id}"), givenId)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemUpdateDTO.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(givenId)
                .jsonPath("$.description")
                .isEqualTo(description)
                .jsonPath("$.price")
                .isEqualTo(price);
    }

    @Test
    void updateItemWhenPathVariableAndDtoIdDifferent() {
        String givenId = "qwe";
        String description = "MACBOOK 2020";
        Double price = 2000.00;
        // given
        ItemUpdateDTO payload = new ItemUpdateDTO(givenId, description, price);

        webTestClient.put()
                .uri(ITEM_UPDATE_FUN_END_POINT_V1.concat("{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemUpdateDTO.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void updateItemWhenItemNotFound() {
        String givenId = "qwe";
        String description = "MACBOOK 2020";
        Double price = 2000.00;
        // given
        ItemUpdateDTO payload = new ItemUpdateDTO(givenId, description, price);

        webTestClient.put()
                .uri(ITEM_UPDATE_FUN_END_POINT_V1.concat("{id}"), givenId)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemUpdateDTO.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void deleteItem() throws Exception{
        String givenId = "ABC";

        webTestClient.delete()
                .uri(ITEM_DELETE_FUN_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void deleteItemWhenIdNotFound() throws Exception{
        String givenId = "zzzz";

        webTestClient.delete()
                .uri(ITEM_DELETE_FUN_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
