package com.example.modelrisk.service;

import com.example.modelrisk.dto.CreateEvaluationPolicyRequest;
import com.example.modelrisk.dto.EvaluationPolicyResponse;
import com.example.modelrisk.entity.EvaluationPolicy;
import com.example.modelrisk.entity.Organization;
import com.example.modelrisk.exception.ConflictException;
import com.example.modelrisk.exception.ResourceNotFoundException;
import com.example.modelrisk.repository.EvaluationPolicyRepository;
import com.example.modelrisk.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EvaluationPolicyService {

    private final EvaluationPolicyRepository policyRepository;
    private final OrganizationRepository organizationRepository;

    public EvaluationPolicyService(
            EvaluationPolicyRepository policyRepository,
            OrganizationRepository organizationRepository
    ) {
        this.policyRepository = policyRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public EvaluationPolicyResponse createPolicy(
            UUID organizationId,
            CreateEvaluationPolicyRequest request
    ) {
        validateThresholdRelationships(request);

        if (policyRepository
                .existsByOrganizationIdAndNameIgnoreCase(
                        organizationId,
                        request.name().trim()
                )) {
            throw new ConflictException(
                    "Evaluation policy name already exists"
            );
        }

        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found"
                ));

        EvaluationPolicy policy = EvaluationPolicy.builder()
                .name(request.name().trim())
                .modelUseCase(request.useCase())
                .riskTier(request.riskTier())
                .minimumAccuracy(request.minimumAccuracy())
                .accuracyWarningThreshold(
                        request.accuracyWarningThreshold()
                )
                .maximumFairnessGap(
                        request.maximumFairnessGap()
                )
                .fairnessWarningThreshold(
                        request.fairnessWarningThreshold()
                )
                .maximumDrift(request.maximumDrift())
                .driftWarningThreshold(
                        request.driftWarningThreshold()
                )
                .minimumRobustnessScore(
                        request.minimumRobustnessScore()
                )
                .requireComplianceEvidence(
                        request.requireComplianceEvidence()
                )
                .organization(organization)
                .active(true)
                .build();

        return toResponse(policyRepository.save(policy));
    }

    @Transactional(readOnly = true)
    public List<EvaluationPolicyResponse> getPolicies(
            UUID organizationId
    ) {
        return policyRepository
                .findAllByOrganizationIdOrderByCreatedAtDesc(
                        organizationId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateThresholdRelationships(
            CreateEvaluationPolicyRequest request
    ) {
        if (request.accuracyWarningThreshold()
                .compareTo(request.minimumAccuracy()) < 0) {
            throw new IllegalArgumentException(
                    "Accuracy warning threshold cannot be below minimum accuracy"
            );
        }

        if (request.fairnessWarningThreshold()
                .compareTo(request.maximumFairnessGap()) > 0) {
            throw new IllegalArgumentException(
                    "Fairness warning threshold cannot exceed the maximum fairness gap"
            );
        }

        if (request.driftWarningThreshold()
                .compareTo(request.maximumDrift()) > 0) {
            throw new IllegalArgumentException(
                    "Drift warning threshold cannot exceed maximum drift"
            );
        }
    }

    private EvaluationPolicyResponse toResponse(
            EvaluationPolicy policy
    ) {
        return new EvaluationPolicyResponse(
                policy.getId(),
                policy.getName(),
                policy.getModelUseCase(),
                policy.getRiskTier(),
                policy.getMinimumAccuracy(),
                policy.getAccuracyWarningThreshold(),
                policy.getMaximumFairnessGap(),
                policy.getFairnessWarningThreshold(),
                policy.getMaximumDrift(),
                policy.getDriftWarningThreshold(),
                policy.getMinimumRobustnessScore(),
                policy.isRequireComplianceEvidence(),
                policy.getOrganization().getId(),
                policy.isActive(),
                policy.getCreatedAt()
        );
    }
}