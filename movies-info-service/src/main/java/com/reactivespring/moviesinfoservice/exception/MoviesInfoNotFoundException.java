package com.reactivespring.moviesinfoservice.exception;

public class MoviesInfoNotFoundException extends RuntimeException {
    public MoviesInfoNotFoundException(String id) {
        super("Movie Info Id:" + id + " is not found.");
    }
}
