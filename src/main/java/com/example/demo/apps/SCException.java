package com.example.demo.apps;

public class SCException extends RuntimeException {
    public SCException(String message) {
        super(message);
    }
    public SCException(String message, Throwable cause) {
        super(message, cause);
    }
}
