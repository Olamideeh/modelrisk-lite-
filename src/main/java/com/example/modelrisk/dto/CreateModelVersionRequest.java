package com.example.modelrisk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateModelVersionRequest(

        @NotBlank
        @Size(max = 50)
        String versionLabel,

        @NotBlank
        @Pattern(
                regexp = "^https?://.+$",
                message = "Prediction endpoint must be a valid HTTP or HTTPS URL"
        )
        @Size(max = 500)
        String predictionEndpointUrl,

        @NotBlank
        @Size(max = 150)
        String algorithm,

        @NotBlank
        @Size(max = 100)
        String trainingDatasetVersion,

        @NotBlank
        @Pattern(
                regexp = "^[A-Fa-f0-9]{64}$",
                message = "Artifact checksum must be a 64-character SHA-256 value"
        )
        String artifactChecksum,

        @Size(max = 1000)
        String notes
) {
}