package dev.moduleskit.modules.auth.api.exception;

import org.springframework.http.HttpStatus;

import dev.moduleskit.shared.exception.BaseException;

public class InvalidPasswordException extends BaseException {
    public InvalidPasswordException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
