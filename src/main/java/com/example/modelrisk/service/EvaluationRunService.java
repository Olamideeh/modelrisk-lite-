package com.example.modelrisk.service;

import com.example.modelrisk.dto.EvaluationRunResponse;
import com.example.modelrisk.dto.StartEvaluationRequest;
import com.example.modelrisk.entity.*;
import com.example.modelrisk.enums.EvaluationRunStatus;
import com.example.modelrisk.enums.ModelVersionStatus;
import com.example.modelrisk.exception.ConflictException;
import com.example.modelrisk.exception.ResourceNotFoundException;
import com.example.modelrisk.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EvaluationRunService {

    private final EvaluationRunRepository evaluationRunRepository;
    private final ModelVersionRepository modelVersionRepository;
    private final EvaluationPolicyRepository policyRepository;
    private final PlatformUserRepository platformUserRepository;

    public EvaluationRunService(
            EvaluationRunRepository evaluationRunRepository,
            ModelVersionRepository modelVersionRepository,
            EvaluationPolicyRepository policyRepository,
            PlatformUserRepository platformUserRepository
    ) {
        this.evaluationRunRepository = evaluationRunRepository;
        this.modelVersionRepository = modelVersionRepository;
        this.policyRepository = policyRepository;
        this.platformUserRepository = platformUserRepository;
    }

    @Transactional
    public EvaluationRunResponse startEvaluation(
            UUID organizationId,
            UUID userId,
            UUID modelVersionId,
            String idempotencyKey,
            StartEvaluationRequest request
    ) {
        validateIdempotencyKey(idempotencyKey);

        var existingRun = evaluationRunRepository
                .findByOrganizationIdAndIdempotencyKey(
                        organizationId,
                        idempotencyKey.trim()
                );

        if (existingRun.isPresent()) {
            validateIdempotentRetry(
                    existingRun.get(),
                    modelVersionId,
                    request
            );

            return toResponse(existingRun.get());
        }

        ModelVersion modelVersion = modelVersionRepository
                .findByIdAndModelOrganizationId(
                        modelVersionId,
                        organizationId
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Model version not found"
                ));

        if (modelVersion.getStatus()
                != ModelVersionStatus.SUBMITTED) {
            throw new ConflictException(
                    "Only a SUBMITTED model version can be evaluated"
            );
        }

        EvaluationPolicy policy = policyRepository
                .findFirstByOrganizationIdAndModelUseCaseAndRiskTierAndActiveTrue(
                        organizationId,
                        modelVersion.getModel().getUseCase(),
                        modelVersion.getModel().getRiskTier()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active evaluation policy matches this model"
                ));

        PlatformUser requestedBy = platformUserRepository
                .findByIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Requesting user not found"
                ));

        ModelVersion baselineVersion = null;

        if (request.baselineVersionId() != null) {
            baselineVersion = modelVersionRepository
                    .findByIdAndModelOrganizationId(
                            request.baselineVersionId(),
                            organizationId
                    )
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Baseline model version not found"
                    ));
        }

        EvaluationRun run = EvaluationRun.builder()
                .reference(generateReference())
                .idempotencyKey(idempotencyKey.trim())
                .organization(
                        modelVersion.getModel().getOrganization()
                )
                .modelVersion(modelVersion)
                .baselineVersion(baselineVersion)
                .policy(policy)
                .requestedBy(requestedBy)
                .status(EvaluationRunStatus.QUEUED)
                .complianceEvidencePresent(
                        request.complianceEvidencePresent()
                )
                .totalTestCases(0)
                .successfulPredictions(0)
                .failedPredictions(0).build();

        return toResponse(evaluationRunRepository.save(run));
    }

    @Transactional(readOnly = true)
    public EvaluationRunResponse getEvaluation(
            UUID organizationId,
            UUID runId
    ) {
        EvaluationRun run = evaluationRunRepository
                .findByIdAndOrganizationId(runId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evaluation run not found"
                ));

        return toResponse(run);
    }

    @Transactional(readOnly = true)
    public List<EvaluationRunResponse> getEvaluations(
            UUID organizationId
    ) {
        return evaluationRunRepository
                .findAllByOrganizationIdOrderByCreatedAtDesc(
                        organizationId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null
                || idempotencyKey.isBlank()
                || idempotencyKey.length() > 100) {
            throw new IllegalArgumentException(
                    "Idempotency-Key must contain between 1 and 100 characters"
            );
        }
    }

    private void validateIdempotentRetry(
            EvaluationRun existing,
            UUID modelVersionId,
            StartEvaluationRequest request
    ) {
        UUID existingBaselineId =
                existing.getBaselineVersion() == null
                        ? null
                        : existing.getBaselineVersion().getId();

        if (!existing.getModelVersion().getId()
                .equals(modelVersionId)
                || !java.util.Objects.equals(
                existingBaselineId,
                request.baselineVersionId()
        )
                || existing.isComplianceEvidencePresent()
                != request.complianceEvidencePresent()) {
            throw new ConflictException(
                    "Idempotency-Key was already used for a different request"
            );
        }
    }

    private String generateReference() {
        return "EVR-" + UUID.randomUUID()
                .toString()
                .substring(0, 12)
                .toUpperCase();
    }

    private EvaluationRunResponse toResponse(EvaluationRun run) {
        return new EvaluationRunResponse(
                run.getId(),
                run.getReference(),
                run.getIdempotencyKey(),
                run.getModelVersion().getId(),
                run.getBaselineVersion() == null
                        ? null
                        : run.getBaselineVersion().getId(),
                run.getPolicy().getId(),
                run.getRequestedBy().getId(),
                run.getStatus(),
                run.getOutcome(),
                run.getAccuracy(),
                run.getFairnessGap(),
                run.getDriftScore(),
                run.getRobustnessScore(),
                run.getTotalTestCases(),
                run.getSuccessfulPredictions(),
                run.getFailedPredictions(),
                run.isComplianceEvidencePresent(),
                run.getFailureReason(),
                run.getStartedAt(),
                run.getCompletedAt(),
                run.getCreatedAt()
        );
    }
}