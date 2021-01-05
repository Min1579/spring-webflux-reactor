package me.min.reactiveweb.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTests {

    List<String> names = List.of("kim", "lee", "han", "lim", "jung");

    @Test
    void transformTestUsingMap() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("KIM")
                .expectNext("LEE")
                .expectNext("HAN")
                .expectNext("LIM")
                .expectNext("JUNG")
                .verifyComplete();
    }

    @Test
    void transformTestUsingMap_Length() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(3, 3, 3, 3, 4)
                .verifyComplete();
    }

    @Test
    void transformTestUsingMap_length_repeat() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(3, 3, 3, 3, 4)
                .expectNext(3, 3, 3, 3, 4)
                .verifyComplete();
    }

    @Test
    void transformTestUsingMap_Filter() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() == 4)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JUNG")
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMap() throws Exception {
        Flux<String> names = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                })  // db or external service call that returns a flux -> s -> Flux<String>
                .log();

        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(final String alphabet) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return List.of(alphabet, "New Value");
    }

    @Test
    void transformUsingFlatMapUsingParallel() throws Exception {
        Flux<String> names = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                .window(2) // Flux<Flux<String> -> (A,B), (C,D). (E,F)
                .flatMap((s) -> s.map(this::convertToList)
                        .subscribeOn(parallel())
                        .flatMap(s2 -> Flux.fromIterable(s2)))
                .log();

        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMapUsingParallelMaintainOrder() throws Exception {
        Flux<String> names = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                .window(2) // Flux<Flux<String> -> (A,B), (C,D). (E,F)
//                .concatMap((s) -> s.map(this::convertToList)
//                        .subscribeOn(parallel())
//                        .flatMap(s2 -> Flux.fromIterable(s2)))
                .flatMapSequential((s) -> s.map(this::convertToList)
                        .subscribeOn(parallel())
                        .flatMap(s2 -> Flux.fromIterable(s2)))
                .log();

        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }
}
