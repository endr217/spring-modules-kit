package dev.moduleskit.modules.auth.enums;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public enum AuthError {

    EMAIL_ALREADY_REGISTERED("error.email_already_registered"),
    USER_NOT_FOUND("error.user_not_found"),
    INVALID_PASSWORD("error.invalid_password");

    private final String code;

    AuthError(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getMessage(MessageSource messageSource) {
        String safeCode = this.code != null ? this.code : "unknown_error";
        return messageSource.getMessage(safeCode, null, safeCode, LocaleContextHolder.getLocale());
    }

}
