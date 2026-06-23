package com.github.bgrebennikov.authenticationbackend.service;

import com.github.bgrebennikov.authenticationbackend.data.dto.*;
import com.github.bgrebennikov.authenticationbackend.data.entity.Permission;
import com.github.bgrebennikov.authenticationbackend.data.entity.Role;
import com.github.bgrebennikov.authenticationbackend.data.entity.UserSession;
import com.github.bgrebennikov.authenticationbackend.repository.RoleRepository;
import com.github.bgrebennikov.authenticationbackend.repository.UserRepository;
import com.github.bgrebennikov.authenticationbackend.repository.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.github.bgrebennikov.authenticationbackend.data.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserSessionRepository userSessionRepository;

    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest, HttpServletRequest request) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalArgumentException("cannot find default role"));

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        user.setRoles(roles);

        userRepository.save(user);

        return createUserSession(user, request);
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Incorrect account credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect account credentials");
        }

        return createUserSession(user, request);
    }

    @Transactional
    public AuthResponse refresh(TokenRefreshRequest refreshRequest, HttpServletRequest request) {
        UserSession userSession = userSessionRepository.findByRefreshToken(
                refreshRequest.getRefreshToken()
        ).orElseThrow(() -> new IllegalArgumentException("Incorrect refresh token"));

        if (userSession.getExpiresAt().isBefore(Instant.now())) {
            userSessionRepository.delete(userSession);
            throw new IllegalArgumentException("Refresh token has expired. Please sign in again.");
        }

        userSession.setRefreshToken(UUID.randomUUID().toString());
        userSession.setExpiresAt(Instant.now().plusSeconds(604800L));
        userSession.setCreatedAt(Instant.now());
        userSession.setIpAddress(extractIp(request));
        userSession.setUserAgent(request.getHeader("User-Agent"));

        userSessionRepository.save(userSession);

        String accessToken = generateAccessToken(userSession.getUser());
        return new AuthResponse(
                accessToken, userSession.getRefreshToken(),
                userSession.getUser().getUsername()
        );

    }


    @Transactional(readOnly = true)
    public List<SessionResponse> getActiveSessions(String username, String currentRefreshToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userSessionRepository.findByUser(user).stream()
                .map(session -> new SessionResponse(
                        session.getId(),
                        session.getUserAgent(),
                        session.getIpAddress(),
                        session.getCreatedAt(),
                        session.getRefreshToken().equals(currentRefreshToken)
                ))
                .collect(Collectors.toList());

    }

    @Transactional
    public void revokeSession(Long sessionId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userSessionRepository.deleteByIdAndUser(sessionId, user);
    }

    @Transactional
    public void revokeAllOtherSessions(String username, String currentRefreshToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserSession currentSession = userSessionRepository.findByRefreshToken(currentRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        userSessionRepository.deleteByUserAndIdNot(user, currentSession.getId());
    }


    private String generateAccessToken(User user) {
        Instant now = Instant.now();
        long exp = 3600L;

        Set<String> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            authorities.add(role.getName());
            if (role.getPermissions() != null) {
                for (Permission permission : role.getPermissions()) {
                    authorities.add(permission.getName());
                }
            }
        }

        String scope = String.join(" ", authorities);
        JwtClaimsSet claimsSet = JwtClaimsSet
                .builder()
                .issuer("authentication-backend")
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(exp))
                .claim("scope", scope)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();

    }


    private AuthResponse createUserSession(User user, HttpServletRequest request) {
        Instant now = Instant.now();
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = extractIp(request);
        String newRefreshToken = UUID.randomUUID().toString();
        Instant newExpiryDate = now.plusSeconds(604800L); // 7 дней

        userSessionRepository.deleteByUserAndExpiresAtBefore(user, now);

        Optional<UserSession> existingSessionOpt =
                userSessionRepository.findByUserAndUserAgentAndIpAddress(user, userAgent, ipAddress);

        UserSession session;
        if (existingSessionOpt.isPresent()) {
            session = existingSessionOpt.get();
            session.setRefreshToken(newRefreshToken);
            session.setCreatedAt(now);
            session.setExpiresAt(newExpiryDate);
            log.info("Updating existing session ID: {} for user: {}", session.getId(), user.getUsername());
        } else {
            session = new UserSession();
            session.setUser(user);
            session.setRefreshToken(newRefreshToken);
            session.setCreatedAt(now);
            session.setExpiresAt(newExpiryDate);
            session.setUserAgent(userAgent);
            session.setIpAddress(ipAddress);
            log.info("Creating new session for user: {} on new device", user.getUsername());
        }

        userSessionRepository.save(session);

        String accessToken = generateAccessToken(user);
        return new AuthResponse(accessToken, session.getRefreshToken(), user.getUsername());
    }

    @Transactional
    public void logout(TokenRefreshRequest logoutRequest) {
        UserSession session = userSessionRepository.findByRefreshToken(logoutRequest.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        userSessionRepository.delete(session);
        log.info("Session with ID: {} successfully closed via logged out", session.getId());
    }

    private String extractIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

}
