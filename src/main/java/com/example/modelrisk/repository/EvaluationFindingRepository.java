package com.example.modelrisk.repository;

import com.example.modelrisk.entity.EvaluationFinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluationFindingRepository
        extends JpaRepository<EvaluationFinding, UUID> {

    List<EvaluationFinding>
    findAllByEvaluationRunIdOrderByCreatedAtAsc(
            UUID evaluationRunId
    );

    boolean existsByEvaluationRunId(UUID evaluationRunId);
}