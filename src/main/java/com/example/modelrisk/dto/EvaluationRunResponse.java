package com.example.modelrisk.dto;

import com.example.modelrisk.enums.EvaluationOutcome;
import com.example.modelrisk.enums.EvaluationRunStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EvaluationRunResponse(
        UUID id,
        String reference,
        String idempotencyKey,
        UUID modelVersionId,
        UUID baselineVersionId,
        UUID policyId,
        UUID requestedById,
        EvaluationRunStatus status,
        EvaluationOutcome outcome,
        BigDecimal accuracy,
        BigDecimal fairnessGap,
        BigDecimal driftScore,
        BigDecimal robustnessScore,
        int totalTestCases,
        int successfulPredictions,
        int failedPredictions,
        boolean complianceEvidencePresent,
        String failureReason,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt
) {
}