package com.github.bgrebennikov.authenticationbackend.controller;

import com.github.bgrebennikov.authenticationbackend.data.dto.AuthResponse;
import com.github.bgrebennikov.authenticationbackend.data.dto.LoginRequest;
import com.github.bgrebennikov.authenticationbackend.data.dto.RegisterRequest;
import com.github.bgrebennikov.authenticationbackend.service.AuthService;
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
    public AuthResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }


}
