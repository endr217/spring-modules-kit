package dev.moduleskit.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user attempts an unauthorized or invalid action.
 *
 * This extends `BaseException` so it will be handled by the global
 * exception handler and returned to the client with a proper HTTP status
 * instead of producing an internal 500 error.
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }
}
