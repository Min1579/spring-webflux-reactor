package me.min.reactiveweb.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTests {
    @Test
    void coldPublisherTest() throws Exception{

        Flux<String> stringFlux = Flux.just("A","B","C","D","E","F")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));
        Thread.sleep(2000);
        stringFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));
        Thread.sleep(4000);
    }

    @Test
    void hotPublisherTest() throws Exception{

        Flux<String> stringFlux = Flux.just("A","B","C","D","E","F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));
        Thread.sleep(3000);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));
        // does not emit the values form beginning
        Thread.sleep(6000);
    }
}
