package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.handler.TestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TestRouter {

    @Bean
    public RouterFunction<ServerResponse> testRouters(TestHandler testHandler) {
        return route()
                .nest(path("/v1/test"), builder -> {
                    builder.GET("/abc", testHandler::test);
                })
                .build();
    }
}
