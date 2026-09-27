package com.example.modelrisk.dto;

import java.math.BigDecimal;

public record EvaluationMetrics(
        BigDecimal accuracy,
        BigDecimal fairnessGap,
        BigDecimal driftScore,
        BigDecimal robustnessScore,
        int totalTestCases,
        int successfulPredictions,
        int failedPredictions
) {
}