package com.dani.luggagebackend.Exception;

/**
 * Exception thrown when a user doesn't have permission to access a resource.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
