package com.example.modelrisk.dto;

import com.example.modelrisk.enums.UserRole;

import java.util.UUID;

public record OrganizationRegistrationResponse(
        UUID organizationId,
        String organizationName,
        String organizationSlug,
        String countryCode,
        UUID adminUserId,
        String adminFullName,
        String adminEmail,
        UserRole adminRole
) {
}