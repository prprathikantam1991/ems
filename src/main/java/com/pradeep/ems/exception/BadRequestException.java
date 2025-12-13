package com.pradeep.ems.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super("Bad request");
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String fieldName, Object fieldValue) {
        super(String.format("Invalid value for %s: '%s'", fieldName, fieldValue));
    }
}
