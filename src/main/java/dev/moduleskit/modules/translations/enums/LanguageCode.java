package dev.moduleskit.modules.translations.enums;

public enum LanguageCode {
    EN("English"),
    LT("Lithuanian");

    private final String displayName;

    LanguageCode(String displayName) {
        this.displayName = displayName;
    }

    public String getCode() {
        return name().toLowerCase(); // "en", "lt"
    }

    public String getDisplayName() {
        return displayName;
    }
}