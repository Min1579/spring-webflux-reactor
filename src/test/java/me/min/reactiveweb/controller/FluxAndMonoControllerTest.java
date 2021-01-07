package me.min.reactiveweb.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@AutoConfigureWebTestClient
@SpringBootTest
@DirtiesContext
class FluxAndMonoControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void testcase1_returnFLux() {

        webClient.get().uri("/api/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType("application/json")
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    void testcase2_returnStreamFLux() {

        Flux<Integer> responseBody = webClient
                .get()
                .uri("/api/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void testcase3() {
        List<Integer> expected = List.of(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> entityExchangeResult = webClient
                .get().uri("/api/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertThat(entityExchangeResult.getResponseBody()).isEqualTo(expected);
    }

    @Test
    void testcase4() {
        List<Integer> expected = List.of(1, 2, 3, 4);

        webClient
                .get()
                .uri("/api/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> {
                    assertThat(response.getResponseBody()).isEqualTo(expected);
                });
    }

    @Test
    void testcase1_stream() {

        Flux<Long> longStreamFlux = webClient
                .get()
                .uri("/api/fluxStream")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType("application/stream+json")
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longStreamFlux)
                .expectSubscription()
                .expectNext(0l,1l,2l,3l)
                .thenCancel()
                .verify();
    }

    @Test
    void testcase1_mono() {

        EntityExchangeResult<Integer> mono = webClient
                .get()
                .uri("/api/mono")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(Integer.class)
                .returnResult();

        assertThat(mono.getResponseBody()).isEqualTo(1);
    }

    @Test
    void testcase2_mono() {

        Integer expected = 1;

        webClient
                .get()
                .uri("/api/mono")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(Integer.class)
                .consumeWith(response ->
                    assertThat(response.getResponseBody()).isEqualTo(expected)
                );
    }

}