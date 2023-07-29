package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {

    private final ReviewReactiveRepository reviewReactiveRepository;
    private final Validator validator;

    @Autowired
    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository, Validator validator) {
        this.reviewReactiveRepository = reviewReactiveRepository;
        this.validator = validator;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .flatMap(review -> ServerResponse.status(HttpStatus.CREATED)
                        .bodyValue(review)).log();
    }

    private void validate(Review review) {
        Set<ConstraintViolation<Review>> validate = validator.validate(review);
        log.info("validate :{}", validate);
        if (!validate.isEmpty()) {
            String errorMessage = validate.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMessage);
        }
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
        Mono<Review> reviewMono = request.bodyToMono(Review.class);
        Mono<Review> review = reviewReactiveRepository.findById(reviewId)
                .doOnNext(review1 -> log.info("test11"))
                .doOnNext(review1 -> {
                    try {
                        simulateCallApiSleepTime("A1", 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .doOnNext(review1 -> log.info("test22"))
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("review not found " + reviewId)));
        Mono<ServerResponse> serverResponseMono = review.flatMap(review1 -> reviewMono
                .doOnNext(review3 -> log.info("2"))
                .doOnNext(this::testValidateComment)
                .doOnNext(review2 -> {
                    try {
                        simulateCallApiSleepTime("A2", 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .doOnNext(review3 -> log.info("3"))
                .map(review2 -> {
                    review1.setComment(review2.getComment());
                    review1.setRating(review2.getRating());
                    return review1;
                })
                .flatMap(reviewReactiveRepository::save)
                .flatMap(saveReview -> ServerResponse.ok().bodyValue(saveReview)));

        try {
            simulateCallApiSleepTime("A3", 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serverResponseMono;
    }

    private void simulateCallApiSleepTime(String stringKey, int second) throws InterruptedException {
        log.info("simulate call to external api::start :{}", stringKey);
        Thread.sleep(second * 1000L);
        log.info("simulate call to external api::done:{}", stringKey);
    }

    private void testValidateComment(Review review) {
        log.info("testValidateComment");
        // test validate
//        Review block = reviewMono.block();
        if (review.getComment().equals("porn")) {
            throw new ReviewDataException("comment can not be rude");
        }
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
