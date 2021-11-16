package com.locus.maps.directions.exception;

import org.springframework.http.HttpStatus;

public class LocusException extends Exception {
    private final HttpStatus errorCode;

    public LocusException(String message, HttpStatus errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public LocusException(String message, HttpStatus errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
