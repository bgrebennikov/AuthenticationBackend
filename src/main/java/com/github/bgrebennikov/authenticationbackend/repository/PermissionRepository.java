package com.github.bgrebennikov.authenticationbackend.repository;

import com.github.bgrebennikov.authenticationbackend.data.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);

}
