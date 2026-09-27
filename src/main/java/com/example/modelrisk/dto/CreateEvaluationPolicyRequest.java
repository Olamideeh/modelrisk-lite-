package com.example.modelrisk.dto;

import com.example.modelrisk.enums.ModelUseCase;
import com.example.modelrisk.enums.RiskTier;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateEvaluationPolicyRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        ModelUseCase useCase,

        @NotNull
        RiskTier riskTier,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        BigDecimal minimumAccuracy,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        BigDecimal accuracyWarningThreshold,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        BigDecimal maximumFairnessGap,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        BigDecimal fairnessWarningThreshold,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        BigDecimal maximumDrift,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        BigDecimal driftWarningThreshold,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        BigDecimal minimumRobustnessScore,

        @NotNull
        Boolean requireComplianceEvidence
) {
}