package me.min.itemclient.controller;

import lombok.extern.slf4j.Slf4j;
import me.min.itemclient.domain.Item;
import me.min.itemclient.domain.ItemDTO;
import me.min.itemclient.domain.ItemUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class ItemController {
    private WebClient webClient = WebClient.create("http://localhost:8080");

    /**
     * retrieve -> only return body
     * exchange  -> return body + http information
     */


    @GetMapping("client/retrieve")
    public Flux<Item> getAllItemUsingRetrieve() {
        return webClient.get()
                .uri("/v1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in Client Project retrieve : ");
    }

    @GetMapping("client/exchange")
    public Flux<Item> getAllItemsUsingExchange() {

        // gonna deprecated!
//        return webClient.get()
//                .uri("/v1/items")
//                .exchange()
//                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
//                .log("Items in Client Project exchange : ");
        return webClient.get()
                .uri("/v1/items")
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Item.class));
    }

    @GetMapping("client/retrieve/singleItem/{id}")
    public Mono<ResponseEntity<Item>> getSingleItemUsingRetrieve(@PathVariable("id") final String id) {

        return webClient.get()
                .uri(String.format("/v1/item/%s", id))
                .retrieve()
                .bodyToMono(Item.class)
                .log("Single Item in Client Project retrieve : ")
                .flatMap(item -> Mono.just(ResponseEntity.ok(item)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("client/registerItem")
    public Mono<Item> registerItem(@RequestBody final ItemDTO payload) {

        return webClient.post()
                .uri("/v1/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemDTO.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Created Item :  ");
    }

    @PutMapping("client/updateItem/{id}")
    public Mono<Item> updateItem(@PathVariable("id") final String id,
                                 @RequestBody ItemUpdateDTO payload) {

        return webClient.put()
                .uri("/v1/item/".concat("{id}"), id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payload), ItemUpdateDTO.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Updated Item : ");
    }

    @DeleteMapping("client/deleteItem/{id}")
    public Mono<Void> deleteItem(@PathVariable("id") final String id) {

        return webClient.delete()
                .uri("/v1/item/".concat("{id}"), id)
                .retrieve()
                .bodyToMono(Void.class)
                .log("DELETED ITEM IS : ");
    }
}
