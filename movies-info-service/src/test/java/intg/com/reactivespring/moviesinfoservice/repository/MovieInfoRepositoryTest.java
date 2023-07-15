package com.reactivespring.moviesinfoservice.repository;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

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
    void findAll() {

        Flux<MovieInfo> movies = movieInfoRepository.findAll().log();

        StepVerifier.create(movies)
                .expectNextCount(3)
                .verifyComplete();
    }


    @Test
    void findById() {
        Mono<MovieInfo> movie = movieInfoRepository.findById("abc").log();

        StepVerifier.create(movie)
                // .expectNextCount(1)
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        Mono<MovieInfo> movie = movieInfoRepository.save(new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")));

        StepVerifier.create(movie)
                // .expectNextCount(1)
                .assertNext(movieInfo -> {
                    System.out.println(movieInfo);
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals("Batman Begins1", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo() {

        MovieInfo movie1 = movieInfoRepository.findById("abc").log().block();
        movie1.setYear(2021);

        Mono<MovieInfo> movie = movieInfoRepository.save(movie1);


        StepVerifier.create(movie)
                // .expectNextCount(1)
                .assertNext(movieInfo -> {
                    System.out.println(movieInfo);
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals(2021, movieInfo.getYear());
                })
                .verifyComplete();
    }


    @Test
    void deleteMovieInfo() {

        // MovieInfo movie1 = movieInfoRepository.findById("abc").log().block();

        movieInfoRepository.deleteById("abc").block();

        Flux<MovieInfo> movies = movieInfoRepository.findAll().log();


        StepVerifier.create(movies)
                .expectNextCount(2)
                .verifyComplete();
    }
}