package dev.moduleskit.modules.auth.api.controller;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import reactor.util.retry.Retry;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import dev.moduleskit.modules.auth.api.request.OAuth2TokenExchangeRequest;
import dev.moduleskit.modules.auth.api.response.UserLoginResponse;
import dev.moduleskit.modules.auth.api.response.OAuth2ErrorResponse;
import dev.moduleskit.modules.auth.config.OAuth2Properties;
import dev.moduleskit.modules.auth.service.AuthService;
import dev.moduleskit.modules.users.domain.User;
import dev.moduleskit.modules.users.infrastructure.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class OAuth2Controller {

    private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);

    private final OAuth2Properties oauth2Props;
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final AuthService authService;

        public OAuth2Controller(OAuth2Properties oauth2Props, WebClient.Builder webClientBuilder, UserRepository userRepository, AuthService authService) {
        this.oauth2Props = oauth2Props;

        // Configure Reactor Netty HTTP client with sensible timeouts to avoid long blocking
        TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
            .doOnConnected(conn -> conn
                .addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(10))
                .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(10)));

        @SuppressWarnings({"deprecation", "null"})
        HttpClient httpClient = HttpClient.from(tcpClient)
            .responseTimeout(Duration.ofSeconds(10));

        this.webClient = webClientBuilder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();

        this.userRepository = userRepository;
        this.authService = authService;
        }

    /**
     * Backend endpoint for frontend to exchange OAuth2 authorization code for JWT token.
     * Frontend redirects from OAuth2 provider directly to frontend, then calls this endpoint.
     */
    @PostMapping("/oauth2/callback")
    public ResponseEntity<?> handleOAuth2Callback(@Valid @RequestBody OAuth2TokenExchangeRequest request) {
        try {
            // Validate provider
            if (!isValidProvider(request.getProvider())) {
                return ResponseEntity.badRequest().body(new OAuth2ErrorResponse("invalid_provider", "Provider not supported", "INVALID_PROVIDER"));
            }

            // Exchange authorization code for access token (use redirectUri supplied by frontend)
            String accessToken = exchangeCodeForToken(request.getProvider(), request.getCode(), request.getRedirectUri());
            
            // Fetch user profile from OAuth2 provider
            Map<String, Object> userProfile = getUserProfile(request.getProvider(), accessToken);
            
            // Extract user info
            String email = (String) userProfile.get("email");
            String providerId = (String) userProfile.get("id");

            // Find or create user
            Optional<User> existingUser = userRepository.findByEmailIgnoreCase(email);
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                // existing user -> regular login
                String token = authService.generateToken(user.getId(), user.getUsername());
                return ResponseEntity.ok(new UserLoginResponse(email, token));
            } else {
                // Auto-create user from OAuth2 profile
                String username = email.split("@")[0] + "_" + UUID.randomUUID().toString().substring(0, 6);
                user = new User(username, email, "oauth_" + request.getProvider() + "_" + providerId);
                user = userRepository.save(user);
                // generate a short-lived registration token and ask frontend to complete profile
                String registrationToken = authService.generateRegistrationToken(user.getId());
                return ResponseEntity.ok(UserLoginResponse.forRegistration(user.getId(), user.getEmail(), user.getUsername(), registrationToken));
            }
        } catch (WebClientRequestException wcre) {
            // Network / DNS / connection problems when calling external provider
            log.warn("OAuth2 provider network error: {}", wcre.getMessage());
            return ResponseEntity.status(503).body(new OAuth2ErrorResponse("provider_unavailable", 
                "OAuth2 provider is temporarily unavailable. Please try again later.", "PROVIDER_NETWORK_ERROR"));
        } catch (Exception e) {
            log.error("OAuth2 callback processing failed", e);
            return ResponseEntity.status(401).body(new OAuth2ErrorResponse("auth_failed", e.getMessage(), "CALLBACK_ERROR"));
        }
    }

    private boolean isValidProvider(String provider) {
        return "google".equalsIgnoreCase(provider) || "facebook".equalsIgnoreCase(provider);
    }

    private String exchangeCodeForToken(String provider, String code, String redirectUri) throws Exception {
        if ("google".equalsIgnoreCase(provider)) {
            return exchangeGoogleCodeForToken(code, redirectUri);
        } else if ("facebook".equalsIgnoreCase(provider)) {
            return exchangeFacebookCodeForToken(code, redirectUri);
        }
        throw new IllegalArgumentException("Invalid provider");
    }

        private String exchangeGoogleCodeForToken(String code, String redirectUri) throws Exception {
            JsonNode response = webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", oauth2Props.getGoogle().getClientId())
                    .with("client_secret", oauth2Props.getGoogle().getClientSecret())
                    .with("code", code)
                    .with("grant_type", "authorization_code")
                    .with("redirect_uri", redirectUri))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(200)).maxBackoff(Duration.ofSeconds(2)).filter(throwable -> throwable instanceof WebClientRequestException))
                .block();
            return response.get("access_token").asText();
        }

        private String exchangeFacebookCodeForToken(String code, String redirectUri) throws Exception {
        JsonNode response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("graph.facebook.com")
                .path("/v12.0/oauth/access_token")
                .queryParam("client_id", oauth2Props.getFacebook().getClientId())
                .queryParam("client_secret", oauth2Props.getFacebook().getClientSecret())
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri)
                .build())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .retryWhen(Retry.backoff(3, Duration.ofMillis(200)).maxBackoff(Duration.ofSeconds(2)).filter(throwable -> throwable instanceof WebClientRequestException))
            .block();
        return response.get("access_token").asText();
        }

    private Map<String, Object> getUserProfile(String provider, String accessToken) throws Exception {
        if ("google".equalsIgnoreCase(provider)) {
            return getGoogleUserProfile(accessToken);
        } else if ("facebook".equalsIgnoreCase(provider)) {
            return getFacebookUserProfile(accessToken);
        }
        throw new IllegalArgumentException("Invalid provider");
    }

    private Map<String, Object> getGoogleUserProfile(String accessToken) throws Exception {
        JsonNode response = webClient.get()
            .uri("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .retryWhen(Retry.backoff(3, Duration.ofMillis(200)).maxBackoff(Duration.ofSeconds(2)).filter(throwable -> throwable instanceof WebClientRequestException))
            .block();
        return Map.of(
                "id", response.get("id").asText(),
                "email", response.get("email").asText(),
                "name", response.get("name").asText()
        );
    }

    private Map<String, Object> getFacebookUserProfile(String accessToken) throws Exception {
        JsonNode response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("graph.facebook.com")
                .path("/me")
                .queryParam("fields", "id,name,email")
                .queryParam("access_token", accessToken)
                .build())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .retryWhen(Retry.backoff(3, Duration.ofMillis(200)).maxBackoff(Duration.ofSeconds(2)).filter(throwable -> throwable instanceof WebClientRequestException))
            .block();
        return Map.of(
                "id", response.get("id").asText(),
                "email", response.get("email").asText(),
                "name", response.get("name").asText()
        );
    }
}
