package me.min.reactiveweb.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombineTests {
    @Test
    void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D","E","F");

        Flux<String> mergeFlux = Flux.merge(flux1, flux2);
        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("D","E","F")
                .verifyComplete();

    }

    @Test
    void combineUsingMerge_withDelay() {
        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofMillis(500));
        Flux<String> flux2 = Flux.just("D","E","F").delayElements(Duration.ofMillis(500));

        Flux<String> mergeFlux = Flux.merge(flux1, flux2);
        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNextCount(6)
//                .expectNext("A","B","C")
//                .expectNext("D","E","F")
                .verifyComplete();
    }

    @Test
    void combineUsingConcat() {
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D","E","F");

        Flux<String> mergeFlux = Flux.concat(flux1, flux2);
        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("D","E","F")
                .verifyComplete();

    }

    @Test
    void combineUsingConcat_withDelay() {
        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofMillis(500));
        Flux<String> flux2 = Flux.just("D","E","F").delayElements(Duration.ofMillis(500));

        Flux<String> mergeFlux = Flux.concat(flux1, flux2);
        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("D","E","F")
                .verifyComplete();
    }

    @Test
    void combineUsingConcat_withDelayUsingVirtualTime() {
        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergeFlux = Flux.concat(flux1, flux2);
        StepVerifier.withVirtualTime(() -> mergeFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void combineUsingZip() {
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D","E","F");

        Flux<String> zipFlux = Flux.zip(flux1, flux2, (f1,f2) -> {
            return f1 + "," + f2;
        }); //A,D : B,E : C:F

        StepVerifier.create(zipFlux.log())
                .expectNext("A,D","B,E","C,F")
                .verifyComplete();

    }
}
