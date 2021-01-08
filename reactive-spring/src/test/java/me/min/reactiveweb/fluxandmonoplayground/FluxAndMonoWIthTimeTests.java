package me.min.reactiveweb.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWIthTimeTests {

    @Test
    void infiniteSequenceTest() throws Exception {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)) // start from 0 -> ........
                .log();

        infiniteFlux.subscribe(element -> System.out.println("Value is " + element));

        Thread.sleep(3000);
    }

    @Test
    void infiniteSequenceTakeTest() throws Exception {
        Flux<Integer> infiniteFlux = Flux.interval(Duration.ofMillis(200)) // start from 0 -> ........
                .delayElements(Duration.ofSeconds(1))
                .take(3)
                .map(l -> l.intValue())
                .log();

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }
}
