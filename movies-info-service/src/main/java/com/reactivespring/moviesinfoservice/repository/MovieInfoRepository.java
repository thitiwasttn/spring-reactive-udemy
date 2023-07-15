package com.reactivespring.moviesinfoservice.repository;


import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {
}
