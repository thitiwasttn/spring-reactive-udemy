package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MovieInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {

    private final MovieInfoService movieInfoService;

    @Autowired
    public MovieInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/movie-infos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
    }


    @GetMapping("/movie-infos")
    public ResponseEntity<Flux<MovieInfo>> getAllMovies() {
        return ResponseEntity.ok(movieInfoService.findAll().log());
    }

    @GetMapping("/movie-infos/{id}")
    public ResponseEntity<Mono<MovieInfo>> getMovieById(@PathVariable String id) {
        return ResponseEntity.ok(movieInfoService.getMovieInfoById(id).log());
    }

    @PutMapping("/movie-infos/{id}")
    public Mono<MovieInfo> updateMovie(@RequestBody MovieInfo movieInfo, @PathVariable String id) {
        return movieInfoService.update(movieInfo, id).log();
    }
}
