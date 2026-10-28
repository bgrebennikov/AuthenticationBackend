package com.github.bgrebennikov.authenticationbackend.data.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String avatarS3Key;
}