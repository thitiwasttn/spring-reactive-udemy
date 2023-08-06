package com.reactivespring.exception.global;

import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {
    @ExceptionHandler(MoviesInfoClientException.class)
    public ResponseEntity<String> handlerClientExceptionMoviesInfoClientException(MoviesInfoClientException clientException) {
        log.error("clientException:{}", clientException.toString());
        log.error("handlerClientException::{}", clientException.getMessage());
        return ResponseEntity.status(clientException.getStatusCode()).body(clientException.getMessage());
    }

    @ExceptionHandler(MoviesInfoServerException.class)
    public ResponseEntity<String> handlerClientExceptionMoviesInfoServerException(MoviesInfoServerException clientException) {
        log.error("clientException:{}", clientException.toString());
        log.error("handlerClientException::{}", clientException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(clientException.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handlerClientExceptionRuntimeException(RuntimeException clientException) {
        log.error("clientException:{}", clientException.toString());
        log.error("handlerClientException::{}", clientException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(clientException.getMessage());
    }
}
