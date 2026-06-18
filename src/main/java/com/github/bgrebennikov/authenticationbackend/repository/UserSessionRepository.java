package com.github.bgrebennikov.authenticationbackend.repository;

import com.github.bgrebennikov.authenticationbackend.data.entity.User;
import com.github.bgrebennikov.authenticationbackend.data.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByRefreshToken(String refreshToken);

    List<UserSession> findByUser(User user);

    void deleteByIdAndUser(Long id, User user);

    void deleteByUserAndIdNot(User user, Long id);

}
