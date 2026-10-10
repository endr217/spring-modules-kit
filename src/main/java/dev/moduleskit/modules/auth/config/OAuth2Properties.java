package dev.moduleskit.modules.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {
    private Google google = new Google();
    private Facebook facebook = new Facebook();
    private String frontendRedirectUri;

    public static class Google {
        private String clientId;
        private String clientSecret;
        private String redirectUri;

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }

        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

        public String getRedirectUri() { return redirectUri; }
        public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
    }

    public static class Facebook {
        private String clientId;
        private String clientSecret;
        private String redirectUri;

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }

        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

        public String getRedirectUri() { return redirectUri; }
        public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
    }

    public Google getGoogle() { return google; }
    public void setGoogle(Google google) { this.google = google; }

    public Facebook getFacebook() { return facebook; }
    public void setFacebook(Facebook facebook) { this.facebook = facebook; }

    public String getFrontendRedirectUri() { return frontendRedirectUri; }
    public void setFrontendRedirectUri(String frontendRedirectUri) { this.frontendRedirectUri = frontendRedirectUri; }
}
