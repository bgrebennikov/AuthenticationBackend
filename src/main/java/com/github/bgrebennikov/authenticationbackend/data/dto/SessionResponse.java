package com.github.bgrebennikov.authenticationbackend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private String userAgent;
    private String ipAddress;
    private Instant createdAt;
    private boolean isCurrent;
}
