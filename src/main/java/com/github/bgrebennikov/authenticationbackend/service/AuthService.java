package com.github.bgrebennikov.authenticationbackend.service;

import com.github.bgrebennikov.authenticationbackend.data.dto.AuthResponse;
import com.github.bgrebennikov.authenticationbackend.data.dto.LoginRequest;
import com.github.bgrebennikov.authenticationbackend.data.dto.RegisterRequest;
import com.github.bgrebennikov.authenticationbackend.data.entity.Permission;
import com.github.bgrebennikov.authenticationbackend.data.entity.Role;
import com.github.bgrebennikov.authenticationbackend.repository.RoleRepository;
import com.github.bgrebennikov.authenticationbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.github.bgrebennikov.authenticationbackend.data.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
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

        String token = generateToken(user);
        return new AuthResponse(
                token, user.getUsername()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Incorrect account credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect account credentials");
        }
        String token = generateToken(user);
        return new AuthResponse(token, user.getUsername());
    }


    private String generateToken(User user) {
        Instant now = Instant.now();
        long exp = 3_600L;

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
                .issuedAt(now)
                .expiresAt(now.plusSeconds(exp))
                .claim("scope", scope)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();

    }

}
