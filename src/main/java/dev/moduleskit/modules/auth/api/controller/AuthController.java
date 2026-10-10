package dev.moduleskit.modules.auth.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.moduleskit.modules.auth.api.request.UserLoginRequest;
import dev.moduleskit.modules.auth.api.request.UserRegistrationRequest;
import dev.moduleskit.modules.auth.api.request.CompleteRegistrationRequest;
import dev.moduleskit.modules.auth.api.response.UserLoginResponse;
import dev.moduleskit.modules.auth.api.response.UserRegisterResponse;
import dev.moduleskit.modules.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserRegisterResponse response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<UserLoginResponse> completeRegistration(@Valid @RequestBody CompleteRegistrationRequest request) {
        UserLoginResponse response = authService.completeRegistration(request.getRegistrationToken(), request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }
}
