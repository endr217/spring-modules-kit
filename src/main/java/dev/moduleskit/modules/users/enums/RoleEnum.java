package dev.moduleskit.modules.users.enums;

/**
 * System roles enum
 */
public enum RoleEnum {
    ROLE_USER("Basic user"),
    ROLE_ADMIN("Administrator with full access");

    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
