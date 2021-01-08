package me.min.reactiveweb.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.reactiveweb.document.Item;
import me.min.reactiveweb.payload.ItemDTO;
import me.min.reactiveweb.payload.ItemUpdateDTO;
import me.min.reactiveweb.repository.ItemReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemHandler {

    private final ItemReactiveRepository itemReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return itemReactiveRepository.findById(id)
                .flatMap(item ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(item))
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> registerItem(ServerRequest serverRequest) {
        Mono<ItemDTO> requestBody = serverRequest.bodyToMono(ItemDTO.class);

        return requestBody.flatMap(payload -> {
            Mono<Item> item = itemReactiveRepository.save(payload.toItemDocument());

            return ServerResponse.status(HttpStatus.CREATED)
                    .body(fromProducer(item, Item.class));
        });
    }


    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");

        return itemReactiveRepository.findById(id)
            .flatMap(item -> serverRequest.bodyToMono(ItemUpdateDTO.class)
                .flatMap(payload -> {
                    if (!payload.getId().equals(item.getId()))
                        return ServerResponse.badRequest().build();

                        Mono<Item> updatedItem = itemReactiveRepository.save(item.update(payload));

                        return ServerResponse.ok().body(fromProducer(updatedItem, Item.class));
                })
            ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return itemReactiveRepository.findById(id)
                .flatMap(item -> ServerResponse.ok().body(itemReactiveRepository.deleteById(id), Void.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
