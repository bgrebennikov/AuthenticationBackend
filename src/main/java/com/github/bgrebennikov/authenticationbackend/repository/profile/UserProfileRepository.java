package com.github.bgrebennikov.authenticationbackend.repository.profile;

import com.github.bgrebennikov.authenticationbackend.data.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<Profile, UUID> {

}
