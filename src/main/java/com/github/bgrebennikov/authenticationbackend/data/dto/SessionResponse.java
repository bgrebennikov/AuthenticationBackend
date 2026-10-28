package com.github.bgrebennikov.authenticationbackend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SessionResponse {
    private UUID id;
    private String userAgent;
    private String ipAddress;
    private Instant createdAt;
    private boolean isCurrent;
}
