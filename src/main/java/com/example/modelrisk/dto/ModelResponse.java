package com.example.modelrisk.dto;

import com.example.modelrisk.enums.ModelUseCase;
import com.example.modelrisk.enums.RiskTier;

import java.time.Instant;
import java.util.UUID;

public record ModelResponse(
        UUID id,
        String modelKey,
        String name,
        String description,
        ModelUseCase useCase,
        RiskTier riskTier,
        UUID organizationId,
        UUID ownerId,
        String ownerName,
        boolean active,
        Instant createdAt
) {
}