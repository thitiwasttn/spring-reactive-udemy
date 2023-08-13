package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.exception.MoviesInfoNotFoundException;
import com.reactivespring.moviesinfoservice.service.MovieInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {

    private final MovieInfoService movieInfoService;
    Sinks.Many<MovieInfo> movieInfoSink = Sinks.many().replay().latest();

    @Autowired
    public MovieInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/movie-infos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo)
                .doOnNext(saved -> movieInfoSink.tryEmitNext(saved))
                .log();
    }


    @GetMapping("/movie-infos")
    public Flux<MovieInfo> getAllMovies() {
        return movieInfoService.findAll().log();
    }

    @GetMapping(value = "/movie-infos/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> getAllMoviesStream() {
        return movieInfoSink.asFlux();
    }

    @GetMapping(value = "/movie-infos/stream2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieInfo> getAllMoviesStream2() {
        return movieInfoSink.asFlux();
    }

    @PutMapping("/movie-infos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovie(@RequestBody MovieInfo movieInfo, @PathVariable String id) {
        return movieInfoService.update(movieInfo, id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.error(new MoviesInfoNotFoundException(id)))
                .log();
    }

    @DeleteMapping("/movie-infos/{id}")
    public ResponseEntity<Mono<Void>> deleteMovieInfo(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(movieInfoService.deleteMovieInfo(id).log());
    }
}
