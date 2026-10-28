package com.github.bgrebennikov.authenticationbackend.repository;

import com.github.bgrebennikov.authenticationbackend.data.entity.User;
import com.github.bgrebennikov.authenticationbackend.data.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findByRefreshToken(String refreshToken);

    Optional<UserSession> findByUserAndUserAgentAndIpAddress(User user, String userAgent, String ipAddress);

    List<UserSession> findByUser(User user);

    void deleteByIdAndUser(UUID id, User user);

    void deleteByUserAndIdNot(User user, UUID id);

    void deleteByUserAndExpiresAtBefore(User user, java.time.Instant now);



}
