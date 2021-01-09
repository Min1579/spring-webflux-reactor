package me.min.itemclient.controller;

import me.min.itemclient.domain.Item;
import me.min.itemclient.domain.ItemDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Disabled
    @Test
    void getAllItemUsingRetrieveOrExchangeTest() {
        webClient.get()
                .uri("/client/retrieve")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Disabled
    @Test
    void getSingleItemUsingRetrieveTest() {
        final String givenId = "ABC";

        webClient.get()
                .uri("/client/retrieve/singleItem/".concat("{id}"), "ABC")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo("ABC")
                .jsonPath("$.description")
                .isEqualTo("BOSE HEADPHONE")
                .jsonPath("$.price")
                .isEqualTo(149.99);
    }

    @Disabled
    @Test
    void getSingleItemUsingRetrieveWhenIDNotFoundTest() {
        final String givenId = "zzzzz";

        webClient.get()
                .uri("/client/retrieve/singleItem/".concat("{id}"),  givenId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Disabled
    @Test
    void registerItem() {
        ItemDTO payload = new ItemDTO("IMAC", 2999.99);

        webClient.post()
                .uri("/client/registerItem")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemDTO.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(Item.class)
                .consumeWith(response -> {
                    var actual = response.getResponseBody();
                    assertThat(actual).isNotNull();
                    assertThat(actual.getId()).isNotNull();
                    assertThat(actual.getDescription()).isEqualTo("IMAC");
                    assertThat(actual.getPrice()).isEqualTo(2999.99);
                });

        webClient.get()
                .uri("/client/retrieve")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(6);

    }

    @Disabled
    @Test
    void updateItem() {
    }

    @Disabled
    @Test
    void deleteItem() {
    }
}