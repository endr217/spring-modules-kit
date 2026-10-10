package dev.moduleskit.modules.auth.api.request;

import jakarta.validation.constraints.NotBlank;

public class CompleteRegistrationRequest {
    @NotBlank
    private String registrationToken;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
