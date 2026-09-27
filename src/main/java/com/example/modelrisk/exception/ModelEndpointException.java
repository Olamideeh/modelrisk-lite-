package com.example.modelrisk.exception;

public class ModelEndpointException extends RuntimeException {

    public ModelEndpointException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}