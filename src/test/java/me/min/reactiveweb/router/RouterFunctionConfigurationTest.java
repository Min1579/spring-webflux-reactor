package me.min.reactiveweb.router;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RouterFunctionConfigurationTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void route_flux_testcase1() {

        FluxExchangeResult<Integer> actual = webClient.get()
                .uri("/api/functional/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Integer.class);

        StepVerifier.create(actual.getResponseBody().log())
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    void route_mono_testcase1() {

        List<Integer> expected = List.of(1,2);

        webClient.get()
                .uri("/api/functional/mono")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(List.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isEqualTo(expected);
                });
    }
}