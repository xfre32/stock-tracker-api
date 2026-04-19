package com.xfre32.stocktracker.exception;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message);
    }
}
