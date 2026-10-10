package dev.moduleskit.modules.auth.api.response;

public class UserLoginResponse {
    private String email;
    private String token;
    private boolean needsProfileCompletion = false;
    private String registrationToken;
    private Long userId;
    private String username;

    public UserLoginResponse() {
    }

    public UserLoginResponse(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public static UserLoginResponse forRegistration(Long userId, String email, String username, String registrationToken) {
        UserLoginResponse r = new UserLoginResponse();
        r.userId = userId;
        r.email = email;
        r.username = username;
        r.registrationToken = registrationToken;
        r.needsProfileCompletion = true;
        return r;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isNeedsProfileCompletion() {
        return needsProfileCompletion;
    }

    public void setNeedsProfileCompletion(boolean needsProfileCompletion) {
        this.needsProfileCompletion = needsProfileCompletion;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
