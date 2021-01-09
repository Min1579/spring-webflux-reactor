package me.min.reactiveweb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;


//@Slf4j
//@Component
//public class FunctionalWebExceptionHandler extends DefaultErrorWebExceptionHandler {
//
//
//    public FunctionalWebExceptionHandler(ErrorAttributes errorAttributes,
//                                         WebProperties.Resources resources,
//                                         //ErrorProperties errorProperties,
//                                         ApplicationContext applicationContext) {
//        super(errorAttributes, resources, new ErrorProperties(), applicationContext);
//        super.setMessageReaders(errorAttributes.get);
//        super.setMessageWriters(null);
//    }
//
//    @Override
//    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
//        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
//    }
//
//
//    protected Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {
//        Map<String, Object> errorAttributesMap = getErrorAttributes(serverRequest, null);
//        log.info("errorAttributesMap : {}", errorAttributesMap);
//        return ServerResponse
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(errorAttributesMap.get("message")));
//    }
//}
