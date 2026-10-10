package dev.moduleskit.modules.auth.api.response;

/**
 * Error response for OAuth2 callback endpoint.
 * Returned when OAuth2 provider is unreachable (network/DNS issues).
 */
public class OAuth2ErrorResponse {
    private String error;
    private String message;
    private String code;

    public OAuth2ErrorResponse(String error, String message, String code) {
        this.error = error;
        this.message = message;
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
