package dev.moduleskit.modules.media.enums;

/**
 * Example target types. Add one entry per entity that owns media,
 * and a matching profile in config/media-profiles.yml.
 */
public enum MediaTargetType {
    ITEM("Item", "images"),
    USER("User", "avatars");

    private final String targetType;
    private final String defaultCollection;

    MediaTargetType(String targetType, String defaultCollection) {
        this.targetType = targetType;
        this.defaultCollection = defaultCollection;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getDefaultCollection() {
        return defaultCollection;
    }
}
