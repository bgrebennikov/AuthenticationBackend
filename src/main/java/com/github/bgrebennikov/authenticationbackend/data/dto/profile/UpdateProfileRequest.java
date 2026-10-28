package com.github.bgrebennikov.authenticationbackend.data.dto.profile;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String avatarS3Key;
}
