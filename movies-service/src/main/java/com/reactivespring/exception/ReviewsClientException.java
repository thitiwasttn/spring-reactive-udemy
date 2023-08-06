package com.reactivespring.exception;

import lombok.Builder;

@Builder
public class ReviewsClientException extends RuntimeException{
    private String message;

    public ReviewsClientException(String message) {
        super(message);
        this.message = message;
    }
}
