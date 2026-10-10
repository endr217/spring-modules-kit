package dev.moduleskit.modules.auth.api.request;

import jakarta.validation.constraints.*;

public class UserLoginRequest {

    @NotBlank(message = "{error.identifier_required}")
    private String identifier;

    @NotBlank(message = "{error.password_required}")
    @Size(min = 6, message = "{error.password_min}")
    private String password;

    public UserLoginRequest() {
    }

    public UserLoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
