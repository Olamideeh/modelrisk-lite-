package com.example.modelrisk.dto;

public record CreditModelPredictionRequest(
        double annualIncome,
        double debtToIncome,
        double creditHistoryYears,
        int missedPayments,
        int existingLoans,
        int age,
        String demographicGroup
) {
}