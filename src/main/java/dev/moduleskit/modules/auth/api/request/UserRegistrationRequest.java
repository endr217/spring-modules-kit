package dev.moduleskit.modules.auth.api.request;

import jakarta.validation.constraints.*;

public class UserRegistrationRequest {

    @NotBlank(message = "{error.username_required}")
    @Size(min = 3, max = 20, message = "{error.username_size}")
    private String username;

    @NotBlank(message = "{error.email_required}")
    @Email(message = "{error.email_invalid}")
    private String email;

    @NotBlank(message = "{error.password_required}")
    @Size(min = 6, message = "{error.password_min}")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
