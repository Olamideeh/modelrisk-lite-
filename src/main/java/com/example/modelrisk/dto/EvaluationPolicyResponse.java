package com.example.modelrisk.dto;

import com.example.modelrisk.enums.ModelUseCase;
import com.example.modelrisk.enums.RiskTier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EvaluationPolicyResponse(
        UUID id,
        String name,
        ModelUseCase useCase,
        RiskTier riskTier,
        BigDecimal minimumAccuracy,
        BigDecimal accuracyWarningThreshold,
        BigDecimal maximumFairnessGap,
        BigDecimal fairnessWarningThreshold,
        BigDecimal maximumDrift,
        BigDecimal driftWarningThreshold,
        BigDecimal minimumRobustnessScore,
        boolean requireComplianceEvidence,
        UUID organizationId,
        boolean active,
        Instant createdAt
) {
}