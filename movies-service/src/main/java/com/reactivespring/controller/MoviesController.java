package com.reactivespring.controller;

import com.reactivespring.service.MoviesService;
import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1/movies")
@Slf4j
public class MoviesController {

    private final MoviesInfoRestClient moviesInfoRestClient;
    private final ReviewsRestClient reviewsRestClient;
    private final MoviesService moviesService;

    @Autowired
    public MoviesController(MoviesInfoRestClient moviesInfoRestClient, ReviewsRestClient reviewsRestClient, MoviesService moviesService) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
        this.moviesService = moviesService;
    }

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {
        log.info("retrieveMovieById::{}", movieId);

        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    Mono<List<Review>> listReviews = reviewsRestClient
                            .retrieveReviewsByMovieId(movieInfo.getMovieInfoId())
                            .collectList();
                    return moviesService.mapReviewMovie(movieInfo, listReviews);
                });
    }

    @GetMapping("/all")
    public Flux<Movie> retrieveAllMovie() {
        log.info("retrieveAllMovie()");
        return moviesInfoRestClient.retrieveAllMovieInfo()
                .flatMap(movieInfo -> {
                    Mono<List<Review>> listReviews = reviewsRestClient
                            .retrieveReviewsByMovieId(movieInfo.getMovieInfoId())
                            .collectList();
                    return moviesService.mapReviewMovie(movieInfo, listReviews);
                });
    }
}
