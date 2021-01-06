package me.min.reactiveweb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RequestMapping("api")
@RestController
public class FluxAndMonoController {

    @GetMapping(value = "flux", produces = "application/json")
    public ResponseEntity<Flux<Integer>> returnFLux() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Flux.just(1, 2, 3, 4)
                        .delayElements(Duration.ofSeconds(1))
                        .log());
    }

    @GetMapping(value = "fluxStream", produces = "application/stream+json")
    public ResponseEntity<Flux<Long>> returnStreamFLux() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Flux.interval(Duration.ofSeconds(1))
                        .take(4)
                        .log());
    }

    @GetMapping(value = "mono", produces = "application/json")
    public ResponseEntity<Mono<Integer>> returnMono() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Mono.just(1)
                        .log());
    }
}

