package com.reactivespring;

import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class MoviesService {
    private final ReviewsRestClient reviewsRestClient;

    @Autowired
    public MoviesService(ReviewsRestClient reviewsRestClient) {
        this.reviewsRestClient = reviewsRestClient;
    }

    public Mono<Movie> mapReviewMovie(MovieInfo movieInfo, Mono<List<Review>> listReviews) {
        return listReviews.map(reviews -> {
            Movie movie = new Movie();
            movie.setMovieInfo(movieInfo);
            movie.setReviewList(reviews);
            return movie;
        });
    }
}
