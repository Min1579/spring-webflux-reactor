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

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
//        log.error("Exception caught in handleRuntimeException : {]", ex);
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ex.getMessage());
//    }

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

    @GetMapping(ITEMS_END_POINT_V1 + "/runtimeException")
    public Flux<Item> runtimeException() {
        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("RuntimeException Occurred")));
    }

    @PostMapping(value = ITEM_REGISTER_END_POINT_V1,
            produces = "application/json",
            consumes = "application/json")
    public Mono<ResponseEntity<Item>> registerItem(@RequestBody final ItemDTO payload) {

        return itemReactiveRepository.save(payload.toItemDocument())
                .map(ResponseEntity.status(HttpStatus.CREATED)::body)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping(value = ITEM_UPDATE_END_POINT_V1 + "{id}",
            produces = "application/json",
            consumes = "application/json")
    public Mono<ResponseEntity<?>> updateItem(@PathVariable("id") final String id,
                                                 @RequestBody final ItemUpdateDTO payload) {

        return Mono.just(id.equals(payload.getId()))
                .flatMap(valid -> {
                    if (valid) {
                        return itemReactiveRepository.findById(id)
                                .flatMap(selectedItem -> itemReactiveRepository.save(selectedItem.update(payload)))
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    }
                    return Mono.just(ResponseEntity.badRequest().body("request Id != payload Id"));
                });
    }


    @DeleteMapping(value = ITEM_DELETE_END_POINT_V1 + "{id}")
    public Mono<ResponseEntity<Void>> deleteItem(@PathVariable("id") final String id) {

        return itemReactiveRepository.findById(id)
                .flatMap(selectedItem ->
                        itemReactiveRepository.delete(selectedItem)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
