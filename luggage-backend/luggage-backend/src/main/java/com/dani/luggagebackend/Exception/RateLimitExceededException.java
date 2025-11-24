package com.dani.luggagebackend.Exception;

/**
 * Exception thrown when a client exceeds the rate limit.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
