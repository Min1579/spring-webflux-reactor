package me.min.reactiveweb.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.reactiveweb.document.Item;
import me.min.reactiveweb.repository.ItemReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static me.min.reactiveweb.constants.ItemConstants.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ItemController {
    private final ItemReactiveRepository itemReactiveRepository;

    @GetMapping(value = ITEM_END_POINT_V1, produces = "application/json")
    public ResponseEntity<Flux<Item>> getAllItems() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemReactiveRepository.findAll());
    }
}
