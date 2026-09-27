package com.example.modelrisk.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartEvaluationRequest(
        UUID baselineVersionId,

        @NotNull
        Boolean complianceEvidencePresent
) {
}