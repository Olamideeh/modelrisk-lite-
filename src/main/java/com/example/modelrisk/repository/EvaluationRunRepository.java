package com.example.modelrisk.repository;

import com.example.modelrisk.entity.EvaluationRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationRunRepository
        extends JpaRepository<EvaluationRun, UUID> {

    Optional<EvaluationRun>
    findByOrganizationIdAndIdempotencyKey(
            UUID organizationId,
            String idempotencyKey
    );

    Optional<EvaluationRun> findByIdAndOrganizationId(
            UUID runId,
            UUID organizationId
    );

    List<EvaluationRun>
    findAllByOrganizationIdOrderByCreatedAtDesc(
            UUID organizationId
    );
}