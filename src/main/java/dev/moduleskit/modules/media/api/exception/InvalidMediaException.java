package dev.moduleskit.modules.media.api.exception;

import org.springframework.http.HttpStatus;

import dev.moduleskit.shared.exception.BaseException;

public class InvalidMediaException extends BaseException {
    public InvalidMediaException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidMediaException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }
}
