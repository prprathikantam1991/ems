package com.pradeep.ems.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Unauthorized access");
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String operation, String username) {
        super(String.format("User '%s' is not authorized to perform operation: %s", username, operation));
    }
}
