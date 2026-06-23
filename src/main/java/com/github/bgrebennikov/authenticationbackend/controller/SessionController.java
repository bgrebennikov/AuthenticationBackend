package com.github.bgrebennikov.authenticationbackend.controller;

import com.github.bgrebennikov.authenticationbackend.data.dto.SessionResponse;
import com.github.bgrebennikov.authenticationbackend.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Slf4j
public class SessionController {

    private final AuthService authService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SessionResponse> getActiveSessions(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("currentRefreshToken") String currentRefreshToken
    ) {
        log.info("Getting sessions for principal {}", jwt.getSubject());
        return authService.getActiveSessions(jwt.getSubject(), currentRefreshToken);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeSession(@PathVariable Long id, Principal principal) {
        authService.revokeSession(id, principal.getName());
    }

    @DeleteMapping("/other")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeAllOtherSessions(
            Principal principal,
            @RequestParam("currentRefreshToken") String currentRefreshToken) {
        authService.revokeAllOtherSessions(principal.getName(), currentRefreshToken);
    }
}