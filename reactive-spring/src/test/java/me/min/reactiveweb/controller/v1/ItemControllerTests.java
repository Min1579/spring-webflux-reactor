package me.min.reactiveweb.controller.v1;

import me.min.reactiveweb.document.Item;
import me.min.reactiveweb.payload.ItemDTO;
import me.min.reactiveweb.payload.ItemUpdateDTO;
import me.min.reactiveweb.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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
public class ItemControllerTests {

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


    @Test
    void getAllItemsTest() {
        Flux<Item> items = webTestClient
                .get()
                .uri(ITEMS_END_POINT_V1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType("application/json")
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(items.log("value from network : "))
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void getAllItemsTest_approach2() {
        webTestClient.get().uri(ITEMS_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType("application/json")
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item -> {
                        assertThat(item).isNotNull();
                        assertThat(item.getId()).isNotNull();
                    });
                });
    }

    @Test
    void getAllItemsTest_approach3() {
        webTestClient.get().uri(ITEMS_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType("application/json")
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    void getItemByIdTest() {
        //given
        final String givenId = "ABC";

        webTestClient.get()
                .uri(ITEM_BY_ID_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType("application/json")
                .expectBody(Item.class)
                .consumeWith(response -> {
                    Item actual = response.getResponseBody();
                    assertThat(actual).isNotNull();
                    assertThat(actual.getId()).isEqualTo(givenId);
                    assertThat(actual.getDescription()).isEqualTo("BOSE HEADPHONE");
                });

    }

    @Test
    void getItemByIdTest_approach2() {
        //given
        final String givenId = "ABC";

        webTestClient.get()
                .uri(ITEM_BY_ID_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType("application/json")
                .expectBody()
                .jsonPath("$.id", givenId)
                .exists()
                .jsonPath("$.description", "BOSE HEADPHONE")
                .exists()
                .jsonPath("$.price", 149.99)
                .exists();
    }

    @Test
    void getItemByIdTest_notFound() {
        //given
        final String givenId = "이런 아이디 없슴다~";

        webTestClient.get()
                .uri(ITEM_BY_ID_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void registerItemTest() {
        ItemDTO payload
                = new ItemDTO("MACBOOK",1000.0);

        webTestClient.post()
                .uri(ITEM_REGISTER_END_POINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemDTO.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(Item.class)
                .consumeWith(response -> {
                    Item actual = response.getResponseBody();
                    assertThat(actual).isNotNull();
                    assertThat(actual.getId()).isNotNull();
                    assertThat(actual.getDescription()).isEqualTo("MACBOOK");
                    assertThat(actual.getPrice()).isEqualTo(1000.0);
                });
    }

    @Test
    void registerItemTest_approach2() {
        ItemDTO payload
                = new ItemDTO("MACBOOK",1000.0);

        webTestClient.post()
                .uri(ITEM_REGISTER_END_POINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemDTO.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id")
                .isNotEmpty()
                .jsonPath("$.description")
                .isEqualTo("MACBOOK")
                .jsonPath("$.price")
                .isEqualTo(1000.0);
    }


    @Test
    void updateItemWhenIdIsValid() {
        final String givenId = "ABC";
        ItemUpdateDTO payload = new ItemUpdateDTO(givenId, "IMAC", 2200.00);

        webTestClient.put()
                .uri(ITEM_UPDATE_END_POINT_V1.concat("{id}"), givenId)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemDTO.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(givenId)
                .jsonPath("$.description")
                .isEqualTo("IMAC")
                .jsonPath("$.price")
                .isEqualTo(2200.00);
    }

    @Test
    void updateItemWhenItemNotFound() {
        final String givenId = "ㅋㅋㅋㅋ";
        ItemUpdateDTO payload = new ItemUpdateDTO(givenId, "IMAC", 2200.00);

        webTestClient.put()
                .uri(ITEM_UPDATE_END_POINT_V1.concat("{id}"), givenId)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemDTO.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }


    @Test
    void updateItemWhenHasDifferentBetweenPayloadAndURI() {
        final String givenId = "ABC";
        ItemUpdateDTO payload = new ItemUpdateDTO(givenId, "IMAC", 2200.00);

        webTestClient.put()
                .uri(ITEM_UPDATE_END_POINT_V1.concat("{id}"), "DEF")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemDTO.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void deleteItemTest() throws Exception {
        webTestClient.delete()
                .uri(ITEM_DELETE_END_POINT_V1.concat("{id}"), "ABC")
                .exchange()
                .expectStatus()
                .isOk();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void deleteItemTest_no_ID() {
        final String givenId = "이런아이디없음!";

        webTestClient.delete()
                .uri(ITEM_DELETE_END_POINT_V1.concat("{id}"), givenId)
                .exchange()
                .expectStatus()
                .isNotFound();
        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }
}
