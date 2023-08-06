package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.service.ExceptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MoviesInfoRestClient {

    private final ExceptionService exceptionService;
    private final WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    @Autowired
    public MoviesInfoRestClient(ExceptionService exceptionService, WebClient webClient) {
        this.exceptionService = exceptionService;
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        String url = moviesInfoUrl
                .concat("/")
                .concat(movieId);
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, this::customThrowError4xx)
                .onStatus(HttpStatus::is5xxServerError, this::customThrowError5xx)
                .bodyToMono(MovieInfo.class)
                .retryWhen(exceptionService.getRetryBackoffSpec());
    }

    public Flux<MovieInfo> retrieveAllMovieInfo() {
        log.info("retrieveAllMovieInfo()");
        String url = moviesInfoUrl;
        log.info("retrieveAllMovieInfo::url:{}", url);
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, this::customThrowError5xx)
                .bodyToFlux(MovieInfo.class);
    }

    private Mono<Throwable> customThrowError4xx(ClientResponse clientResponse) {
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
    }

    private Mono<Throwable> customThrowError5xx(ClientResponse clientResponse) {
        return clientResponse
                .bodyToMono(String.class)
                .flatMap(s -> Mono.error(MoviesInfoServerException.builder()
                        .message(s)
                        .build()));
    }
}
