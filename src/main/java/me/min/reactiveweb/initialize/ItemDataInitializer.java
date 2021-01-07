package me.min.reactiveweb.initialize;

import lombok.RequiredArgsConstructor;
import me.min.reactiveweb.document.Item;
import me.min.reactiveweb.repository.ItemReactiveRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {
    private final ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetUp();
    }

    private Flux<Item> items() {
        return Flux.fromIterable(List.of(
                new Item(null, "SAMSUNG TV", 400.0),
                new Item(null, "LG TV", 420.0),
                new Item(null, "APPLE WATCH", 299.9),
                new Item(null, "BEATS HEADPHONE", 149.99),
                new Item("ABC", "BOSE HEADPHONE", 149.99)
        ));
    }

    private void initialDataSetUp() {

        itemReactiveRepository.deleteAll()
                .thenMany(items())
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted Item is : " + item.getDescription());
                })
                .thenMany(itemReactiveRepository.findAll().log())
                .subscribe(item -> System.out.println("Item inserted from CommandLineRunner : " + item.getDescription()));
    }
}
