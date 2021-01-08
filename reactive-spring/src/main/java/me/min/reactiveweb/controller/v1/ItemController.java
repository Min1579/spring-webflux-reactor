package me.min.reactiveweb.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.reactiveweb.document.Item;
import me.min.reactiveweb.payload.ItemDTO;
import me.min.reactiveweb.payload.ItemUpdateDTO;
import me.min.reactiveweb.repository.ItemReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static me.min.reactiveweb.constants.ItemConstants.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ItemController {
    private final ItemReactiveRepository itemReactiveRepository;

    @GetMapping(value = ITEMS_END_POINT_V1, produces = "application/json")
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(value = ITEM_BY_ID_END_POINT_V1 + "{id}", produces = "application/json")
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable("id") final String id) {

        return itemReactiveRepository.findById(id)
                .map(ResponseEntity.status(HttpStatus.OK)::body)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = ITEM_REGISTER_END_POINT_V1,
            produces = "application/json",
            consumes = "application/json")
    public Mono<ResponseEntity<Item>> registerItem(@RequestBody final ItemDTO payload) {
        return itemReactiveRepository.save(payload.toItemDocument())
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }


    // id and item to be updated in the req - path variable and request body
    // using the id get the item from database
    // updated the item retrieved with the value from the request body
    // save the item
    // return the saved items
    @PutMapping(value = ITEM_UPDATE_END_POINT_V1 + "{id}",
            produces = "application/json",
            consumes = "application/json")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable("id") final String id,
                                                   @RequestBody final ItemUpdateDTO payload) {
        log.info("DTO : {} ", payload);

        if (!id.equals(payload.getId())) {
            return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }

        return itemReactiveRepository.findById(id)
                .flatMap(item -> itemReactiveRepository.save(item.update(payload)))
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping(value = ITEM_DELETE_END_POINT_V1 + "{id}")
    public Mono<ResponseEntity<Object>> deleteItem(@PathVariable("id") final String id) {
        log.info("req id : {}", id);
        return itemReactiveRepository.findById(id)
                .map(item -> {
                    itemReactiveRepository.deleteById(item.getId());
                    return new ResponseEntity<>(HttpStatus.OK);
                })
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}