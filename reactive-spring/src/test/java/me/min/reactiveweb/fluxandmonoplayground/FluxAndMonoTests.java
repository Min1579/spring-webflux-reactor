package me.min.reactiveweb.fluxandmonoplayground;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTests {

    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

    @Disabled
    @Test
    public void fluxTestChangeOrder() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring Boot")
                .expectNext("Spring")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

    @Test
    public void fluxTestCount() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void fluxTestWithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        stringFlux.subscribe(System.out::println, e -> System.err.println(e));

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectError(RuntimeException.class)
                .verify();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectErrorMessage("Exception Occurred")
                .verify();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectError()
                .verify();
    }

    @Test
    public void fluxTestWithError2() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                // can't not reach after error
                .concatWith(Flux.just("After Error1", "After Error2"))
                .log();

        stringFlux.subscribe(System.out::println,
                (e) -> System.err.println("Error is " + e),
                // can't not reach "Completed"
                () -> System.out.println("Completed"));

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectError();
    }


    @Test
    void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();

        StepVerifier.create(stringMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void monoTestError() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError();
    }
}
