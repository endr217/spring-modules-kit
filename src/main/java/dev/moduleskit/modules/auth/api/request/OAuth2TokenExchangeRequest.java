package dev.moduleskit.modules.auth.api.request;

import jakarta.validation.constraints.NotBlank;

public class OAuth2TokenExchangeRequest {
    @NotBlank(message = "Authorization code is required")
    private String code;

    @NotBlank(message = "Provider is required")
    private String provider;

    @NotBlank(message = "State is required for CSRF validation")
    private String state;
    
    @NotBlank(message = "Redirect URI is required")
    private String redirectUri;

    public OAuth2TokenExchangeRequest() {}

    public OAuth2TokenExchangeRequest(String code, String provider, String state, String redirectUri) {
        this.code = code;
        this.provider = provider;
        this.state = state;
        this.redirectUri = redirectUri;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
