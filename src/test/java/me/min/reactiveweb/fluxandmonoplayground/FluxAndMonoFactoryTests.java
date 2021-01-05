package me.min.reactiveweb.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FluxAndMonoFactoryTests {

    @Test
    void fluxUsingIterable() {
        Flux<String> names = Flux.fromIterable(List.of("kim","lee","han"))
                .log();

        StepVerifier.create(names)
                .expectNext("kim","lee","han")
                .verifyComplete();
    }

    @Test
    void fluxUsingArray() {
        StepVerifier.create(Flux.fromArray(new String[]{"kim","lee","han"}))
                .expectNext("kim","lee","han")
                .verifyComplete();
    }

    @Test
    void fluxUsingStream() {
        StepVerifier.create(Flux.fromStream(Stream.of("kim","lee","han")).log())
                .expectNext("kim","lee","han");
    }

    @Test
    void monoUsingJustOrEmpty() {
        StepVerifier.create(Mono.justOrEmpty(null).log())
                .verifyComplete();
    }

    @Test
    void monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "min";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMono.log())
                .expectNext("min")
                .verifyComplete();
    }

    @Test
    void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1,10).log();

        StepVerifier.create(integerFlux)
                .expectNext(1,2,3,4,5,6,7,8,9,10)
                .verifyComplete();
    }
}
