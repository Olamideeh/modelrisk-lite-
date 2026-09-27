package com.example.modelrisk.dto;

import com.example.modelrisk.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        UserRole role,
        UUID organizationId,
        boolean active,
        Instant createdAt
) {
}