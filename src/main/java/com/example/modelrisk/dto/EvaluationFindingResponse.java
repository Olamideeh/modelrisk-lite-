package com.example.modelrisk.dto;

import com.example.modelrisk.enums.FindingSeverity;
import com.example.modelrisk.enums.FindingType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EvaluationFindingResponse(
        UUID id,
        UUID evaluationRunId,
        FindingType findingType,
        FindingSeverity severity,
        String findingCode,
        String title,
        String details,
        BigDecimal observedValue,
        BigDecimal thresholdValue,
        String affectedGroup,
        boolean resolved,
        String resolutionNote,
        Instant resolvedAt,
        Instant createdAt
) {
}