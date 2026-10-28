package com.github.bgrebennikov.authenticationbackend.controller;

import com.github.bgrebennikov.authenticationbackend.data.dto.profile.ProfileResponse;
import com.github.bgrebennikov.authenticationbackend.data.dto.profile.UpdateProfileRequest;
import com.github.bgrebennikov.authenticationbackend.service.UserProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class UserProfileController {


    private final UserProfileService userProfileService;

    @GetMapping()
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {

        String email = jwt.getSubject();
        return ResponseEntity.ok(userProfileService.getUserProfile(email));
    }

    @PatchMapping()
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateProfileRequest request
    ) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(userProfileService.updateUserProfile(email, request));
    }


}
