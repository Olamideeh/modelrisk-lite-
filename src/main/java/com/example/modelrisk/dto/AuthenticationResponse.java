package com.example.modelrisk.dto;

import com.example.modelrisk.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record AuthenticationResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        UUID userId,
        UUID organizationId,
        String email,
        UserRole role
) {
}