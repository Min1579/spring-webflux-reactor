package me.min.reactiveweb.router;

import me.min.reactiveweb.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static me.min.reactiveweb.constants.ItemConstants.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {
    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemHandler itemHandler) {
        return RouterFunctions
                .route(GET(ITEMS_END_FUN_POINT_V1)
                        , itemHandler::getAllItems)

                .andRoute(GET(ITEM_BY_ID_FUN_END_POINT_V1.concat("{id}"))
                        , itemHandler::getItemById)

                .andRoute(POST(ITEM_REGISTER_FUN_END_POINT_V1)
                                .and(accept(MediaType.APPLICATION_JSON))
                        , itemHandler::registerItem)

                .andRoute(PUT(ITEM_UPDATE_FUN_END_POINT_V1.concat("{id}"))
                                .and(accept(MediaType.APPLICATION_JSON))
                        , itemHandler::updateItem)

                .andRoute(DELETE(ITEM_DELETE_FUN_END_POINT_V1.concat("{id}"))
                        , itemHandler::deleteItem);

    }
}
