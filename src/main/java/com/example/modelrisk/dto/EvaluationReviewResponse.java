package com.example.modelrisk.dto;

import com.example.modelrisk.enums.ReviewDecision;
import com.example.modelrisk.enums.ModelVersionStatus;

import java.time.Instant;
import java.util.UUID;

public record EvaluationReviewResponse(
        UUID id,
        UUID evaluationRunId,
        UUID modelVersionId,
        UUID reviewerId,
        String reviewerName,
        ReviewDecision decision,
        String reviewNote,
        ModelVersionStatus resultingModelStatus,
        Instant reviewedAt,
        Instant updatedAt
) {
}