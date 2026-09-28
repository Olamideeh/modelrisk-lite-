package com.example.modelrisk.service;

import com.example.modelrisk.dto.EvaluationMetrics;
import com.example.modelrisk.entity.EvaluationPolicy;
import com.example.modelrisk.enums.EvaluationOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationOutcomeDeciderTest {

    private EvaluationOutcomeDecider decider;
    private EvaluationPolicy policy;

    @BeforeEach
    void setUp() {
        decider = new EvaluationOutcomeDecider();

        policy = EvaluationPolicy.builder()
                .minimumAccuracy(new BigDecimal("0.75"))
                .accuracyWarningThreshold(
                        new BigDecimal("0.82")
                )
                .maximumFairnessGap(
                        new BigDecimal("0.20")
                )
                .fairnessWarningThreshold(
                        new BigDecimal("0.10")
                )
                .maximumDrift(new BigDecimal("0.20"))
                .driftWarningThreshold(
                        new BigDecimal("0.10")
                )
                .minimumRobustnessScore(
                        new BigDecimal("0.70")
                )
                .requireComplianceEvidence(true)
                .build();
    }

    @Test
    void shouldPassWhenAllMetricsMeetPolicy() {
        EvaluationMetrics metrics = metrics(
                "0.95",
                "0.02",
                "0.03",
                "0.90",
                12,
                0
        );

        EvaluationOutcome outcome = decider.decide(
                metrics,
                policy,
                true
        );

        assertThat(outcome)
                .isEqualTo(EvaluationOutcome.PASSED);
    }

    @Test
    void shouldRequireReviewForWarningLevelAccuracy() {
        EvaluationMetrics metrics = metrics(
                "0.80",
                "0.02",
                "0.03",
                "0.90",
                12,
                0
        );

        EvaluationOutcome outcome = decider.decide(
                metrics,
                policy,
                true
        );

        assertThat(outcome)
                .isEqualTo(
                        EvaluationOutcome.REVIEW_REQUIRED
                );
    }

    @Test
    void shouldFailWhenModelEndpointCallsFail() {
        EvaluationMetrics metrics = metrics(
                "0.95",
                "0.02",
                "0.03",
                "0.90",
                10,
                2
        );

        EvaluationOutcome outcome = decider.decide(
                metrics,
                policy,
                true
        );

        assertThat(outcome)
                .isEqualTo(EvaluationOutcome.FAILED);
    }

    @Test
    void shouldFailWhenComplianceEvidenceIsMissing() {
        EvaluationMetrics metrics = metrics(
                "0.95",
                "0.02",
                "0.03",
                "0.90",
                12,
                0
        );

        EvaluationOutcome outcome = decider.decide(
                metrics,
                policy,
                false
        );

        assertThat(outcome)
                .isEqualTo(EvaluationOutcome.FAILED);
    }

    private EvaluationMetrics metrics(
            String accuracy,
            String fairnessGap,
            String driftScore,
            String robustnessScore,
            int successfulPredictions,
            int failedPredictions
    ) {
        return new EvaluationMetrics(
                new BigDecimal(accuracy),
                new BigDecimal(fairnessGap),
                new BigDecimal(driftScore),
                new BigDecimal(robustnessScore),
                12,
                successfulPredictions,
                failedPredictions
        );
    }
}