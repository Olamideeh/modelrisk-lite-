package com.example.modelrisk.dto;

import com.example.modelrisk.enums.ModelVersionStatus;

import java.time.Instant;
import java.util.UUID;

public record ModelVersionResponse(
        UUID id,
        UUID modelId,
        String modelName,
        String versionLabel,
        String predictionEndpointUrl,
        String algorithm,
        String trainingDatasetVersion,
        String artifactChecksum,
        String notes,
        ModelVersionStatus status,
        UUID createdById,
        Instant submittedAt,
        Instant approvedAt,
        Instant deployedAt,
        Instant retiredAt,
        Instant createdAt
) {
}