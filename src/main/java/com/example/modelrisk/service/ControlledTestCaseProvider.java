package com.example.modelrisk.service;

import com.example.modelrisk.dto.ControlledCreditTestCase;
import com.example.modelrisk.dto.CreditModelPredictionRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ControlledTestCaseProvider {

    public List<ControlledCreditTestCase> getCases() {
        return List.of(
                testCase(
                        "LOW-RISK-A-01",
                        120_000, 0.18, 12,
                        0, 1, 34,
                        "GROUP_A", "APPROVE", false
                ),
                testCase(
                        "LOW-RISK-B-01",
                        120_000, 0.18, 12,
                        0, 1, 34,
                        "GROUP_B", "APPROVE", false
                ),
                testCase(
                        "LOW-RISK-A-02",
                        90_000, 0.25, 8,
                        1, 2, 41,
                        "GROUP_A", "APPROVE", false
                ),
                testCase(
                        "LOW-RISK-B-02",
                        90_000, 0.25, 8,
                        1, 2, 41,
                        "GROUP_B", "APPROVE", false
                ),
                testCase(
                        "HIGH-RISK-A-01",
                        22_000, 0.85, 1,
                        9, 7, 23,
                        "GROUP_A", "DECLINE", false
                ),
                testCase(
                        "HIGH-RISK-B-01",
                        22_000, 0.85, 1,
                        9, 7, 23,
                        "GROUP_B", "DECLINE", false
                ),
                testCase(
                        "HIGH-RISK-A-02",
                        30_000, 0.75, 2,
                        7, 6, 28,
                        "GROUP_A", "DECLINE", false
                ),
                testCase(
                        "HIGH-RISK-B-02",
                        30_000, 0.75, 2,
                        7, 6, 28,
                        "GROUP_B", "DECLINE", false
                ),
                testCase(
                        "ROBUST-APPROVE-A",
                        118_000, 0.20, 11.5,
                        0, 1, 35,
                        "GROUP_A", "APPROVE", true
                ),
                testCase(
                        "ROBUST-APPROVE-B",
                        118_000, 0.20, 11.5,
                        0, 1, 35,
                        "GROUP_B", "APPROVE", true
                ),
                testCase(
                        "ROBUST-DECLINE-A",
                        24_000, 0.82, 1.5,
                        8, 7, 24,
                        "GROUP_A", "DECLINE", true
                ),
                testCase(
                        "ROBUST-DECLINE-B",
                        24_000, 0.82, 1.5,
                        8, 7, 24,
                        "GROUP_B", "DECLINE", true
                )
        );
    }

    private ControlledCreditTestCase testCase(
            String caseId,
            double annualIncome,
            double debtToIncome,
            double creditHistoryYears,
            int missedPayments,
            int existingLoans,
            int age,
            String demographicGroup,
            String expectedDecision,
            boolean robustnessCase
    ) {
        CreditModelPredictionRequest input =
                new CreditModelPredictionRequest(
                        annualIncome,
                        debtToIncome,
                        creditHistoryYears,
                        missedPayments,
                        existingLoans,
                        age,
                        demographicGroup
                );

        return new ControlledCreditTestCase(
                caseId,
                input,
                expectedDecision,
                robustnessCase
        );
    }
}