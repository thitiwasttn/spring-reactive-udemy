package com.reactivespring.handler;

import com.mongodb.internal.connection.Server;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Slf4j
public class ReviewHandler {

    private final ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(reviewReactiveRepository::save)
                .flatMap(review -> ServerResponse.status(HttpStatus.CREATED)
                        .bodyValue(review));
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        Optional<String> movieInfoId = request.queryParam("movieInfoId");
        if (movieInfoId.isPresent()) {
            Flux<Review> byMovieInfoId = reviewReactiveRepository.findByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return ServerResponse.ok().body(byMovieInfoId, Review.class);
        }
        Flux<Review> all = reviewReactiveRepository.findAll();
        return ServerResponse.ok().body(all, Review.class);
    }

    public Mono<ServerResponse> updateView(ServerRequest request) {
        String reviewId = request.pathVariable("id");
        Mono<Review> review = reviewReactiveRepository.findById(reviewId);

        Mono<ServerResponse> serverResponseMono = review.flatMap(review1 -> {
            return request.bodyToMono(Review.class)
                    .map(review2 -> {
                        review1.setComment(review2.getComment());
                        review1.setRating(review2.getRating());
                        return review1;
                    })
                    .flatMap(reviewReactiveRepository::save)
                    .flatMap(saveReview -> ServerResponse.ok().bodyValue(saveReview));
        });
        return serverResponseMono;
    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        String reviewId = serverRequest.pathVariable("id");
        return reviewReactiveRepository.findById(reviewId)
                .flatMap(reviewReactiveRepository::delete)
                .then(Mono.defer(() -> ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> getReviewsById(ServerRequest serverRequest) {
        log.info("getReviewsById");
        String reviewId = serverRequest.pathVariable("id");
        Mono<Review> byId = reviewReactiveRepository.findById(reviewId);
        Mono<Review> review = byId.switchIfEmpty(Mono.error(() -> new ReviewNotFoundException("not found")));

        return ServerResponse.ok().body(review, Review.class);
    }
}
