package com.example.modelrisk.service;

import com.example.modelrisk.dto.*;
import com.example.modelrisk.exception.ModelEndpointException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ModelEvaluationEngine {

    private final ControlledTestCaseProvider testCaseProvider;
    private final CreditModelClient creditModelClient;

    public ModelEvaluationEngine(
            ControlledTestCaseProvider testCaseProvider,
            CreditModelClient creditModelClient
    ) {
        this.testCaseProvider = testCaseProvider;
        this.creditModelClient = creditModelClient;
    }

    public EvaluationMetrics evaluate(String endpointUrl) {
        List<ControlledCreditTestCase> testCases =
                testCaseProvider.getCases();

        int correctPredictions = 0;
        int successfulPredictions = 0;
        int failedPredictions = 0;

        int groupATotal = 0;
        int groupAApprovals = 0;
        int groupBTotal = 0;
        int groupBApprovals = 0;

        int robustnessTotal = 0;
        int robustnessCorrect = 0;

        for (ControlledCreditTestCase testCase : testCases) {
            String group =
                    testCase.input().demographicGroup();

            if ("GROUP_A".equals(group)) {
                groupATotal++;
            } else if ("GROUP_B".equals(group)) {
                groupBTotal++;
            }

            if (testCase.robustnessCase()) {
                robustnessTotal++;
            }

            try {
                CreditModelPredictionResponse prediction =
                        creditModelClient.predict(
                                endpointUrl,
                                testCase.input()
                        );

                successfulPredictions++;

                boolean correct = testCase.expectedDecision()
                        .equalsIgnoreCase(prediction.decision());

                if (correct) {
                    correctPredictions++;

                    if (testCase.robustnessCase()) {
                        robustnessCorrect++;
                    }
                }

                if ("APPROVE".equalsIgnoreCase(
                        prediction.decision()
                )) {
                    if ("GROUP_A".equals(group)) {
                        groupAApprovals++;
                    } else if ("GROUP_B".equals(group)) {
                        groupBApprovals++;
                    }
                }

            } catch (ModelEndpointException exception) {
                failedPredictions++;
            }
        }

        BigDecimal accuracy = ratio(
                correctPredictions,
                testCases.size()
        );

        BigDecimal groupAApprovalRate = ratio(
                groupAApprovals,
                groupATotal
        );

        BigDecimal groupBApprovalRate = ratio(
                groupBApprovals,
                groupBTotal
        );

        BigDecimal fairnessGap = groupAApprovalRate
                .subtract(groupBApprovalRate)
                .abs()
                .setScale(5, RoundingMode.HALF_UP);

        BigDecimal robustnessScore = ratio(
                robustnessCorrect,
                robustnessTotal
        );

        return new EvaluationMetrics(
                accuracy,
                fairnessGap,
                BigDecimal.ZERO.setScale(
                        5,
                        RoundingMode.HALF_UP
                ),
                robustnessScore,
                testCases.size(),
                successfulPredictions,
                failedPredictions
        );
    }

    private BigDecimal ratio(int numerator, int denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO.setScale(
                    5,
                    RoundingMode.HALF_UP
            );
        }

        return BigDecimal.valueOf(numerator)
                .divide(
                        BigDecimal.valueOf(denominator),
                        5,
                        RoundingMode.HALF_UP
                );
    }
}