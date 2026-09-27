package com.example.modelrisk.repository;

import com.example.modelrisk.entity.EvaluationPolicy;
import com.example.modelrisk.enums.ModelUseCase;
import com.example.modelrisk.enums.RiskTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationPolicyRepository
        extends JpaRepository<EvaluationPolicy, UUID> {

    boolean existsByOrganizationIdAndNameIgnoreCase(
            UUID organizationId,
            String name
    );

    Optional<EvaluationPolicy>
    findFirstByOrganizationIdAndModelUseCaseAndRiskTierAndActiveTrue(
            UUID organizationId,
            ModelUseCase modelUseCase,
            RiskTier riskTier
    );

    List<EvaluationPolicy>
    findAllByOrganizationIdOrderByCreatedAtDesc(
            UUID organizationId
    );
}