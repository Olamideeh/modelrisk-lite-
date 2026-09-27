package com.example.modelrisk.service;

import com.example.modelrisk.dto.EvaluationMetrics;
import com.example.modelrisk.entity.EvaluationRun;
import com.example.modelrisk.enums.EvaluationOutcome;
import com.example.modelrisk.enums.EvaluationRunStatus;
import com.example.modelrisk.enums.ModelVersionStatus;
import com.example.modelrisk.exception.ConflictException;
import com.example.modelrisk.exception.ResourceNotFoundException;
import com.example.modelrisk.repository.EvaluationRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class EvaluationExecutionService {

    private final EvaluationRunRepository evaluationRunRepository;
    private final ModelEvaluationEngine evaluationEngine;
    private final EvaluationOutcomeDecider outcomeDecider;

    public EvaluationExecutionService(
            EvaluationRunRepository evaluationRunRepository,
            ModelEvaluationEngine evaluationEngine,
            EvaluationOutcomeDecider outcomeDecider
    ) {
        this.evaluationRunRepository = evaluationRunRepository;
        this.evaluationEngine = evaluationEngine;
        this.outcomeDecider = outcomeDecider;
    }

    @Transactional
    public void execute(
            UUID organizationId,
            UUID runId
    ) {
        EvaluationRun run = evaluationRunRepository
                .findByIdAndOrganizationId(runId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evaluation run not found"
                ));

        if (run.getStatus() != EvaluationRunStatus.QUEUED) {
            throw new ConflictException(
                    "Only a QUEUED evaluation can be executed"
            );
        }

        run.setStatus(EvaluationRunStatus.RUNNING);
        run.setStartedAt(Instant.now());

        run.getModelVersion().setStatus(
                ModelVersionStatus.EVALUATING
        );

        EvaluationMetrics metrics = evaluationEngine.evaluate(
                run.getModelVersion().getPredictionEndpointUrl()
        );

        EvaluationOutcome outcome = outcomeDecider.decide(
                metrics,
                run.getPolicy(),
                run.isComplianceEvidencePresent()
        );

        run.setAccuracy(metrics.accuracy());
        run.setFairnessGap(metrics.fairnessGap());
        run.setDriftScore(metrics.driftScore());
        run.setRobustnessScore(metrics.robustnessScore());
        run.setTotalTestCases(metrics.totalTestCases());
        run.setSuccessfulPredictions(
                metrics.successfulPredictions()
        );
        run.setFailedPredictions(
                metrics.failedPredictions()
        );
        run.setOutcome(outcome);
        run.setStatus(EvaluationRunStatus.COMPLETED);
        run.setCompletedAt(Instant.now());

        if (metrics.failedPredictions() > 0) {
            run.setFailureReason(
                    "One or more calls to the AI model failed"
            );
        }

        if (outcome == EvaluationOutcome.FAILED) {
            run.getModelVersion().setStatus(
                    ModelVersionStatus.REJECTED
            );
        } else if (
                outcome == EvaluationOutcome.REVIEW_REQUIRED
        ) {
            run.getModelVersion().setStatus(
                    ModelVersionStatus.REVIEW_REQUIRED
            );
        }

        evaluationRunRepository.save(run);
    }
}