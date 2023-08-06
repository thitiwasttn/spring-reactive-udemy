package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MoviesInfoRestClient {

    private final WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    @Autowired
    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        String url = moviesInfoUrl.concat("/").concat(movieId);
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.info("retrieveMovieInfo:: clientResponse.statusCode():{}", clientResponse.statusCode());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(s -> Mono.error(MoviesInfoClientException.builder()
                                        .message(s)
                                        .statusCode(clientResponse.statusCode().value())
                                        .build()));
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(s -> Mono.error(MoviesInfoClientException.builder()
                                        .message(s)
                                        .statusCode(clientResponse.statusCode().value())
                                        .build()));
                    }
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse
                        .bodyToMono(String.class)
                        .flatMap(s -> Mono.error(MoviesInfoServerException.builder()
                                .message(s)
                                .build())))
                .bodyToMono(MovieInfo.class);
    }
}
