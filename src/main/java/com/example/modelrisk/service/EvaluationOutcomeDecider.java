package com.example.modelrisk.service;

import com.example.modelrisk.dto.EvaluationMetrics;
import com.example.modelrisk.entity.EvaluationPolicy;
import com.example.modelrisk.enums.EvaluationOutcome;
import org.springframework.stereotype.Component;

@Component
public class EvaluationOutcomeDecider {

    public EvaluationOutcome decide(
            EvaluationMetrics metrics,
            EvaluationPolicy policy,
            boolean complianceEvidencePresent
    ) {
        if (metrics.failedPredictions() > 0) {
            return EvaluationOutcome.FAILED;
        }

        if (policy.isRequireComplianceEvidence()
                && !complianceEvidencePresent) {
            return EvaluationOutcome.FAILED;
        }

        if (metrics.accuracy()
                .compareTo(policy.getMinimumAccuracy()) < 0) {
            return EvaluationOutcome.FAILED;
        }

        if (metrics.fairnessGap()
                .compareTo(policy.getMaximumFairnessGap()) > 0) {
            return EvaluationOutcome.FAILED;
        }

        if (metrics.driftScore()
                .compareTo(policy.getMaximumDrift()) > 0) {
            return EvaluationOutcome.FAILED;
        }

        if (metrics.robustnessScore()
                .compareTo(
                        policy.getMinimumRobustnessScore()
                ) < 0) {
            return EvaluationOutcome.FAILED;
        }

        if (metrics.accuracy()
                .compareTo(
                        policy.getAccuracyWarningThreshold()
                ) < 0) {
            return EvaluationOutcome.REVIEW_REQUIRED;
        }

        if (metrics.fairnessGap()
                .compareTo(
                        policy.getFairnessWarningThreshold()
                ) > 0) {
            return EvaluationOutcome.REVIEW_REQUIRED;
        }

        if (metrics.driftScore()
                .compareTo(
                        policy.getDriftWarningThreshold()
                ) > 0) {
            return EvaluationOutcome.REVIEW_REQUIRED;
        }

        return EvaluationOutcome.PASSED;
    }
}