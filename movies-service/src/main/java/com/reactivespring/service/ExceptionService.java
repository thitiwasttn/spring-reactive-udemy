package com.reactivespring.service;

import com.reactivespring.exception.MoviesInfoClientException;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Service
public class ExceptionService {

    public RetryBackoffSpec getRetryBackoffSpec() {
        return Retry
                .fixedDelay(1, Duration.ofSeconds(1))
                //.filter(ex -> ex instanceof MoviesInfoClientException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
    }
}
