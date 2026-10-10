package dev.moduleskit.modules.auth.api.response;

public class UserRegisterResponse {
    
    private Long id;
    private String email;

    public UserRegisterResponse(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
