package com.reactivespring.moviesinfoservice.service;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.exception.MoviesInfoNotFoundException;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MovieInfoService {
    private final MovieInfoRepository movieInfoRepository;

    @Autowired
    public MovieInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> findAll() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> update(MovieInfo movieInfo, String id) {

        return movieInfoRepository.findById(id)
                .switchIfEmpty(Mono.error(new MoviesInfoNotFoundException(id)))
                .doOnNext(movieInfo1 -> {
                    log.info("do 1 :{}", movieInfo1);
                })
                .doOnNext(movieInfo1 -> {
                    log.info("do 2 :{}", movieInfo1);
                })
                /*.handle((movieInfo1, synchronousSink) -> {
                    log.info("do handle 1 :{}", movieInfo1);
                    if (movieInfo.getName().equals("99")) {
                        synchronousSink.error(new RuntimeException("Sample Throw errer"));
                    } else {
                        return ;
                    }
                })*/
                .flatMap(movieInfo1 -> {
                    if (movieInfo.getName().equals("99")) {
                        return Mono.error(new RuntimeException("Sample Throw errer"));
                    }
                    log.info("do flatMap 1 :{}", movieInfo1);
                    movieInfo1.setCast(movieInfo.getCast());
                    movieInfo1.setReleaseDate(movieInfo.getReleaseDate());
                    movieInfo1.setName(movieInfo.getName());
                    movieInfo1.setYear(movieInfo.getYear());
                    return movieInfoRepository.save(movieInfo1);
                })
                .doOnNext(movieInfo1 -> {
                    log.info("do 3 :{}", movieInfo1);
                });
    }
}
