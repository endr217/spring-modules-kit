package dev.moduleskit.modules.media.enums;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public enum MediaError {
    IMAGE_UNSUPPORTED_TYPE("media.error.imageUnsupportedType"),
    IMAGE_INVALID("media.error.imageInvalid");

    private final String messageKey;

    MediaError(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(MessageSource messageSource) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }
}
