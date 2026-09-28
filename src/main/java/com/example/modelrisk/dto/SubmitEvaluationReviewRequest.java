package com.example.modelrisk.dto;

import com.example.modelrisk.enums.ReviewDecision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitEvaluationReviewRequest(

        @NotNull
        ReviewDecision decision,

        @NotBlank
        @Size(max = 1000)
        String reviewNote
) {
}