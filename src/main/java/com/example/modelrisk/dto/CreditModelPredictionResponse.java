package com.example.modelrisk.dto;

public record CreditModelPredictionResponse(
        boolean predictedDefault,
        double defaultProbability,
        String decision,
        String modelVersion
) {
}