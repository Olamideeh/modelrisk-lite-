package com.example.modelrisk.repository;

import com.example.modelrisk.entity.EvaluationReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EvaluationReviewRepository
        extends JpaRepository<EvaluationReview, UUID> {

    Optional<EvaluationReview> findByEvaluationRunId(
            UUID evaluationRunId
    );

    boolean existsByEvaluationRunId(UUID evaluationRunId);
}