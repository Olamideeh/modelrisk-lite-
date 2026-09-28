package com.example.modelrisk.service;

import com.example.modelrisk.dto.EvaluationReviewResponse;
import com.example.modelrisk.dto.SubmitEvaluationReviewRequest;
import com.example.modelrisk.entity.EvaluationReview;
import com.example.modelrisk.entity.EvaluationRun;
import com.example.modelrisk.entity.ModelVersion;
import com.example.modelrisk.entity.PlatformUser;
import com.example.modelrisk.enums.*;
import com.example.modelrisk.exception.ConflictException;
import com.example.modelrisk.exception.ResourceNotFoundException;
import com.example.modelrisk.repository.EvaluationReviewRepository;
import com.example.modelrisk.repository.EvaluationRunRepository;
import com.example.modelrisk.repository.PlatformUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class EvaluationReviewService {

    private final EvaluationReviewRepository reviewRepository;
    private final EvaluationRunRepository evaluationRunRepository;
    private final PlatformUserRepository platformUserRepository;

    public EvaluationReviewService(
            EvaluationReviewRepository reviewRepository,
            EvaluationRunRepository evaluationRunRepository,
            PlatformUserRepository platformUserRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.evaluationRunRepository = evaluationRunRepository;
        this.platformUserRepository = platformUserRepository;
    }

    @Transactional
    public EvaluationReviewResponse submitReview(
            UUID organizationId,
            UUID reviewerId,
            UUID runId,
            SubmitEvaluationReviewRequest request
    ) {
        EvaluationRun run = evaluationRunRepository
                .findByIdAndOrganizationId(runId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evaluation run not found"
                ));

        if (run.getStatus() != EvaluationRunStatus.COMPLETED) {
            throw new ConflictException(
                    "Only a completed evaluation can be reviewed"
            );
        }

        if (reviewRepository.existsByEvaluationRunId(runId)) {
            throw new ConflictException(
                    "This evaluation already has a final review"
            );
        }

        if (request.decision() == ReviewDecision.APPROVED
                && run.getOutcome() == EvaluationOutcome.FAILED) {
            throw new ConflictException(
                    "A failed evaluation cannot be approved"
            );
        }

        PlatformUser reviewer = platformUserRepository
                .findByIdAndOrganizationId(
                        reviewerId,
                        organizationId
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Risk reviewer not found"
                ));

        ModelVersion version = run.getModelVersion();
        Instant now = Instant.now();

        switch (request.decision()) {
            case APPROVED -> {
                version.setStatus(ModelVersionStatus.APPROVED);
                version.setApprovedAt(now);
            }

            case REJECTED ->
                    version.setStatus(ModelVersionStatus.REJECTED);

            case CHANGES_REQUIRED ->
                    version.setStatus(
                            ModelVersionStatus.REVIEW_REQUIRED
                    );
        }

        EvaluationReview review = EvaluationReview.builder()
                .evaluationRun(run)
                .reviewer(reviewer)
                .decision(request.decision())
                .reviewNote(request.reviewNote().trim())
                .reviewedAt(now)
                .build();

        return toResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public EvaluationReviewResponse getReview(
            UUID organizationId,
            UUID runId
    ) {
        evaluationRunRepository
                .findByIdAndOrganizationId(runId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evaluation run not found"
                ));

        EvaluationReview review = reviewRepository
                .findByEvaluationRunId(runId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evaluation review not found"
                ));

        return toResponse(review);
    }

    private EvaluationReviewResponse toResponse(
            EvaluationReview review
    ) {
        return new EvaluationReviewResponse(
                review.getId(),
                review.getEvaluationRun().getId(),
                review.getEvaluationRun()
                        .getModelVersion()
                        .getId(),
                review.getReviewer().getId(),
                review.getReviewer().getFullName(),
                review.getDecision(),
                review.getReviewNote(),
                review.getEvaluationRun()
                        .getModelVersion()
                        .getStatus(),
                review.getReviewedAt(),
                review.getUpdatedAt()
        );
    }
}