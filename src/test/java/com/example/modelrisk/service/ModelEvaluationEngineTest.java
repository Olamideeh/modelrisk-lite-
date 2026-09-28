package com.example.modelrisk.service;

import com.example.modelrisk.dto.*;
import com.example.modelrisk.exception.ModelEndpointException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelEvaluationEngineTest {

    @Mock
    private ControlledTestCaseProvider testCaseProvider;

    @Mock
    private CreditModelClient creditModelClient;

    private ModelEvaluationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new ModelEvaluationEngine(
                testCaseProvider,
                creditModelClient
        );

        when(testCaseProvider.getCases())
                .thenReturn(controlledCases());
    }

    @Test
    void shouldCalculatePassingMetrics() {
        when(creditModelClient.predict(
                anyString(),
                any(CreditModelPredictionRequest.class)
        )).thenAnswer(invocation -> {
            CreditModelPredictionRequest request =
                    invocation.getArgument(1);

            boolean highRisk =
                    request.debtToIncome() > 0.50;

            return new CreditModelPredictionResponse(
                    highRisk,
                    highRisk ? 0.90 : 0.10,
                    highRisk ? "DECLINE" : "APPROVE",
                    "1.0.0"
            );
        });

        EvaluationMetrics metrics = engine.evaluate(
                "http://localhost:8090/predict"
        );

        assertThat(metrics.accuracy())
                .isEqualByComparingTo(BigDecimal.ONE);

        assertThat(metrics.fairnessGap())
                .isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(metrics.robustnessScore())
                .isEqualByComparingTo(BigDecimal.ONE);

        assertThat(metrics.totalTestCases()).isEqualTo(4);
        assertThat(metrics.successfulPredictions())
                .isEqualTo(4);
        assertThat(metrics.failedPredictions()).isZero();
    }

    @Test
    void shouldCountUnavailableModelCallsAsFailures() {
        when(creditModelClient.predict(
                anyString(),
                any(CreditModelPredictionRequest.class)
        )).thenThrow(
                new ModelEndpointException(
                        "Endpoint unavailable",
                        null
                )
        );

        EvaluationMetrics metrics = engine.evaluate(
                "http://localhost:8090/predict"
        );

        assertThat(metrics.accuracy())
                .isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(metrics.successfulPredictions()).isZero();
        assertThat(metrics.failedPredictions()).isEqualTo(4);
    }

    private List<ControlledCreditTestCase> controlledCases() {
        return List.of(
                testCase(
                        "APPROVE-A",
                        0.20,
                        "GROUP_A",
                        "APPROVE",
                        true
                ),
                testCase(
                        "APPROVE-B",
                        0.20,
                        "GROUP_B",
                        "APPROVE",
                        true
                ),
                testCase(
                        "DECLINE-A",
                        0.80,
                        "GROUP_A",
                        "DECLINE",
                        true
                ),
                testCase(
                        "DECLINE-B",
                        0.80,
                        "GROUP_B",
                        "DECLINE",
                        true
                )
        );
    }

    private ControlledCreditTestCase testCase(
            String id,
            double debtToIncome,
            String group,
            String expectedDecision,
            boolean robustnessCase
    ) {
        CreditModelPredictionRequest input =
                new CreditModelPredictionRequest(
                        80_000,
                        debtToIncome,
                        8,
                        1,
                        2,
                        35,
                        group
                );

        return new ControlledCreditTestCase(
                id,
                input,
                expectedDecision,
                robustnessCase
        );
    }
}