package dev.moduleskit.modules.auth.api.exception;

import org.springframework.http.HttpStatus;
import dev.moduleskit.shared.exception.BaseException;

public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
