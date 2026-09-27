package com.example.modelrisk.dto;

import java.util.UUID;

public record ModelConnectionTestResponse(
        UUID modelVersionId,
        String endpointUrl,
        boolean reachable,
        String modelVersion,
        String decision,
        double defaultProbability,
        String message
) {
}