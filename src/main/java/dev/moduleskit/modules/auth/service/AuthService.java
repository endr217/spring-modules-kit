package dev.moduleskit.modules.auth.service;

import dev.moduleskit.modules.auth.api.request.UserLoginRequest;
import dev.moduleskit.modules.auth.api.request.UserRegistrationRequest;
import dev.moduleskit.modules.auth.api.response.UserLoginResponse;
import dev.moduleskit.modules.auth.api.response.UserRegisterResponse;

public interface AuthService {
    /**
     * @param request
     * @return
     */
    UserRegisterResponse register(UserRegistrationRequest request);

    /**
     * @param request
     * @return
     */
    public UserLoginResponse login(UserLoginRequest request);

    /**
     * Generate JWT token for a user (used for OAuth2 login)
     * @param userId
     * @param username
     * @return
     */
    String generateToken(Long userId, String username);

    /**
     * Generate short-lived registration token for completing OAuth-created accounts
     */
    String generateRegistrationToken(Long userId);

    /**
     * Complete registration for a user created via OAuth using the registration token.
     */
    UserLoginResponse completeRegistration(String registrationToken, String username, String password);
}
