package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieInfoControllerTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String ADD_MOVIE_INFO_URL = "/v1/movie-infos";
    static String FIND_ALL_MOVIE_INFO = "/v1/movie-infos";
    static String FIND_MOVIE_BY_ID = "/v1/movie-infos";
    static String DELETE_MOVIE_BY_ID = "/v1/movie-infos";

    @BeforeEach
    void setup() {
        List<MovieInfo> movieInfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));
        movieInfoRepository.saveAll(movieInfos)
                .log()
                .blockLast(); // <--- important ถ้าไม่ใช้ไว้ จะเป็น asynce อาจจะทำให้ find all ก่อนจะ save
    }

    @AfterEach
    void afterEach() {
        movieInfoRepository.deleteAll()
                .log()
                .block(); // <--- กันไม่ให้เป็น asynce
    }

    @Test
    void addMovieInfo() {

        MovieInfo movieInfo = new MovieInfo(null, "The Dark Knight",
                2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18"));

        webTestClient
                .post()
                .uri(ADD_MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    assertNotNull(responseBody.getMovieInfoId());
                });
    }

    @Test
    void getAllMovies() {
        webTestClient.get()
                .uri(FIND_ALL_MOVIE_INFO)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieById() {
        String movieId = "abc";
        webTestClient.get()
                .uri(FIND_MOVIE_BY_ID + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(movieId, responseBody.getMovieInfoId());
                });

        // OR
        webTestClient.get()
                .uri(FIND_MOVIE_BY_ID + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.movieInfoId")
                .isEqualTo(movieId);


    }

    @Test
    void deleteMovieInfoById() {
        String movieId = "abc";

        webTestClient.delete()
                .uri(DELETE_MOVIE_BY_ID + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNoContent();

        webTestClient.get()
                .uri(FIND_MOVIE_BY_ID + "/{id}", movieId)
                .exchange()
                .expectBody(MovieInfo.class)
                .consumeWith(entityExchangeResult -> {
                    MovieInfo responseBody = entityExchangeResult.getResponseBody();
                    assertNull(responseBody);
                });
    }
}