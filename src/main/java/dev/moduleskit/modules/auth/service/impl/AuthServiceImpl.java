package dev.moduleskit.modules.auth.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.moduleskit.modules.auth.api.exception.InvalidPasswordException;
import dev.moduleskit.modules.auth.api.exception.UserAlreadyExistsException;
import dev.moduleskit.modules.auth.api.exception.UserNotFoundException;
import dev.moduleskit.modules.auth.api.request.UserLoginRequest;
import dev.moduleskit.modules.auth.api.request.UserRegistrationRequest;
import dev.moduleskit.modules.auth.api.response.UserLoginResponse;
import dev.moduleskit.modules.auth.api.response.UserRegisterResponse;
import dev.moduleskit.modules.auth.enums.AuthError;
import dev.moduleskit.modules.auth.service.AuthService;
import dev.moduleskit.modules.users.domain.User;
import dev.moduleskit.modules.users.infrastructure.repository.UserRepository;
import dev.moduleskit.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            MessageSource messageSource, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
        this.jwtService = jwtService;
    }

    public UserRegisterResponse register(UserRegistrationRequest request) {

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new UserAlreadyExistsException(AuthError.EMAIL_ALREADY_REGISTERED.getMessage(messageSource));
        }

        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), request.getEmail(), hashedPassword);

        userRepository.save(user);

        return new UserRegisterResponse(user.getId(), user.getEmail());
    }

    public UserLoginResponse login(UserLoginRequest request) {

        User user = userRepository.findByEmailIgnoreCase(request.getIdentifier().toLowerCase())
                .or(() -> userRepository.findByUsernameIgnoreCase(request.getIdentifier().toLowerCase()))
                .orElseThrow(() -> new UserNotFoundException(AuthError.USER_NOT_FOUND.getMessage(messageSource)));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException(AuthError.INVALID_PASSWORD.getMessage(messageSource));
        }

        String token = jwtService.generateJwtToken(user);
        return new UserLoginResponse(user.getEmail(), token);
    }

    @Override
    public String generateToken(Long userId, String username) {
        @SuppressWarnings("null")
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return jwtService.generateJwtToken(user);
    }

    @Override
    public String generateRegistrationToken(Long userId) {
        @SuppressWarnings("null")
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return jwtService.generateRegistrationToken(user);
    }

    @Override
    public UserLoginResponse completeRegistration(String registrationToken, String username, String password) {
        // validate token
        var jwsOpt = jwtService.validateTokenAndGetJws(registrationToken);
        if (jwsOpt.isEmpty()) {
            throw new UserNotFoundException("Invalid or expired registration token");
        }
        Jws<Claims> jws = jwsOpt.get();
        Claims claims = jws.getPayload();
        Boolean isRegistration = claims.get("registration", Boolean.class);
        if (isRegistration == null || !isRegistration) {
            throw new UserNotFoundException("Invalid registration token");
        }

        Long userId = Long.parseLong(claims.getSubject());
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        // check username availability
        if (userRepository.existsByUsernameIgnoreCase(username) && !user.getUsername().equalsIgnoreCase(username)) {
            throw new RuntimeException("Username already taken");
        }

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        String token = jwtService.generateJwtToken(user);
        return new UserLoginResponse(user.getEmail(), token);
    }
}
