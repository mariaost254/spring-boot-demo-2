package com.demo.utils.errors;

public class PlayerServiceException extends RuntimeException {
    public PlayerServiceException(String message) {
        super(message);
    }

    public PlayerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
