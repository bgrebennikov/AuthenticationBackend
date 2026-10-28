package com.github.bgrebennikov.authenticationbackend.repository;

import com.github.bgrebennikov.authenticationbackend.data.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String roleName);
}
