package com.example.modelrisk.dto;

public record ControlledCreditTestCase(
        String caseId,
        CreditModelPredictionRequest input,
        String expectedDecision,
        boolean robustnessCase
) {
}