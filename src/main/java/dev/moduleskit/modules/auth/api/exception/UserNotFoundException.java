package dev.moduleskit.modules.auth.api.exception;

import org.springframework.http.HttpStatus;

import dev.moduleskit.shared.exception.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
