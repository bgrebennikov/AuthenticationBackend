package com.github.bgrebennikov.authenticationbackend.service;

import com.github.bgrebennikov.authenticationbackend.data.dto.profile.ProfileResponse;
import com.github.bgrebennikov.authenticationbackend.data.dto.profile.UpdateProfileRequest;
import com.github.bgrebennikov.authenticationbackend.data.entity.Profile;
import com.github.bgrebennikov.authenticationbackend.data.entity.User;
import com.github.bgrebennikov.authenticationbackend.mapper.ProfileMapper;
import com.github.bgrebennikov.authenticationbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Transactional(readOnly = true)
    public ProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return profileMapper.toResponse(user.getProfile());
    }

    @Transactional
    public ProfileResponse updateUserProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Profile profile = user.getProfile();

        profileMapper.updateProfileFromDto(request, profile);

        return profileMapper.toResponse(profile);
    }
}
