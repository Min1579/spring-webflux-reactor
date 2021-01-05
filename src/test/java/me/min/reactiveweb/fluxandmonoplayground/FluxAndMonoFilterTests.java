package me.min.reactiveweb.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxAndMonoFilterTests {
    List<String> names = List.of("kim","lee","han","lim", "jung");

    @Test
    void filterTest() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter( s -> s.startsWith("k") )
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("kim")
                .verifyComplete();
    }

    @Test
    void filerTestLength() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter( s -> s.length() >= 4 )
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("jung")
                .verifyComplete();
    }
}
