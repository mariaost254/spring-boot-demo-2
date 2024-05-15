package com.demo.utils.exceptions;

public class PlayerServiceException extends RuntimeException {
    public PlayerServiceException(String message) {
        super(message);
    }

    public PlayerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
