package com.example.modelrisk.dto;

import com.example.modelrisk.enums.ModelUseCase;
import com.example.modelrisk.enums.RiskTier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterModelRequest(

        @NotBlank
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "Model key must contain lowercase letters, numbers, or hyphens"
        )
        @Size(max = 80)
        String modelKey,

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        ModelUseCase useCase,

        @NotNull
        RiskTier riskTier
) {
}