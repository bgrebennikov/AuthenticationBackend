package com.github.bgrebennikov.authenticationbackend.controller;

import com.github.bgrebennikov.authenticationbackend.data.dto.*;
import com.github.bgrebennikov.authenticationbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        return authService.register(registerRequest, request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return authService.login(loginRequest, request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refresh(@RequestBody TokenRefreshRequest refreshRequest, HttpServletRequest request) {
        return authService.refresh(refreshRequest, request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody TokenRefreshRequest refreshRequest, HttpServletRequest request) {
        authService.logout(refreshRequest);
    }
}