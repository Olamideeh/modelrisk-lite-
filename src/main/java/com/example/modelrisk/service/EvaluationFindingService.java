package com.example.modelrisk.service;

import com.example.modelrisk.dto.EvaluationFindingResponse;
import com.example.modelrisk.entity.EvaluationFinding;
import com.example.modelrisk.entity.EvaluationPolicy;
import com.example.modelrisk.entity.EvaluationRun;
import com.example.modelrisk.enums.FindingSeverity;
import com.example.modelrisk.enums.FindingType;
import com.example.modelrisk.exception.ResourceNotFoundException;
import com.example.modelrisk.repository.EvaluationFindingRepository;
import com.example.modelrisk.repository.EvaluationRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.modelrisk.enums.EvaluationRunStatus;

import java.util.List;
import java.util.UUID;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class EvaluationFindingService {


    private final EvaluationFindingRepository findingRepository;
    private final EvaluationRunRepository evaluationRunRepository;

    public EvaluationFindingService(
            EvaluationFindingRepository findingRepository,
            EvaluationRunRepository evaluationRunRepository
    ) {
        this.findingRepository = findingRepository;
        this.evaluationRunRepository = evaluationRunRepository;
    }

    @Transactional
    public void generateFindings(EvaluationRun run) {
        if (findingRepository.existsByEvaluationRunId(
                run.getId()
        )) {
            return;
        }

        EvaluationPolicy policy = run.getPolicy();
        List<EvaluationFinding> findings = new ArrayList<>();

        findings.add(accuracyFinding(run, policy));
        findings.add(fairnessFinding(run, policy));
        findings.add(driftFinding(run, policy));
        findings.add(robustnessFinding(run, policy));
        findings.add(availabilityFinding(run));
        findings.add(complianceFinding(run, policy));

        findingRepository.saveAll(findings);
    }

    @Transactional
    public List<EvaluationFindingResponse> getFindings(
            UUID organizationId,
            UUID runId
    ) {
        EvaluationRun run = evaluationRunRepository
                .findByIdAndOrganizationId(runId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evaluation run not found"
                ));

        if (run.getStatus() == EvaluationRunStatus.COMPLETED
                && !findingRepository.existsByEvaluationRunId(runId)) {
            generateFindings(run);
        }

        return findingRepository
                .findAllByEvaluationRunIdOrderByCreatedAtAsc(runId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    private EvaluationFinding accuracyFinding(
            EvaluationRun run,
            EvaluationPolicy policy
    ) {
        FindingSeverity severity;

        if (run.getAccuracy().compareTo(
                policy.getMinimumAccuracy()
        ) < 0) {
            severity = FindingSeverity.CRITICAL;
        } else if (run.getAccuracy().compareTo(
                policy.getAccuracyWarningThreshold()
        ) < 0) {
            severity = FindingSeverity.WARNING;
        } else {
            severity = FindingSeverity.INFO;
        }

        return finding(
                run,
                FindingType.ACCURACY,
                severity,
                "ACCURACY_RESULT",
                "Accuracy evaluation completed",
                "The model accuracy was compared with the configured minimum and warning thresholds.",
                run.getAccuracy(),
                policy.getMinimumAccuracy(),
                null
        );
    }

    private EvaluationFinding fairnessFinding(
            EvaluationRun run,
            EvaluationPolicy policy
    ) {
        FindingSeverity severity;

        if (run.getFairnessGap().compareTo(
                policy.getMaximumFairnessGap()
        ) > 0) {
            severity = FindingSeverity.CRITICAL;
        } else if (run.getFairnessGap().compareTo(
                policy.getFairnessWarningThreshold()
        ) > 0) {
            severity = FindingSeverity.WARNING;
        } else {
            severity = FindingSeverity.INFO;
        }

        return finding(
                run,
                FindingType.FAIRNESS,
                severity,
                "FAIRNESS_GAP_RESULT",
                "Fairness gap evaluation completed",
                "Approval rates for GROUP_A and GROUP_B were compared using equivalent financial profiles.",
                run.getFairnessGap(),
                policy.getMaximumFairnessGap(),
                "GROUP_A,GROUP_B"
        );
    }

    private EvaluationFinding driftFinding(
            EvaluationRun run,
            EvaluationPolicy policy
    ) {
        FindingSeverity severity;

        if (run.getDriftScore().compareTo(
                policy.getMaximumDrift()
        ) > 0) {
            severity = FindingSeverity.CRITICAL;
        } else if (run.getDriftScore().compareTo(
                policy.getDriftWarningThreshold()
        ) > 0) {
            severity = FindingSeverity.WARNING;
        } else {
            severity = FindingSeverity.INFO;
        }

        return finding(
                run,
                FindingType.DRIFT,
                severity,
                "DRIFT_RESULT",
                "Model drift evaluation completed",
                "The model drift score was compared with the configured policy.",
                run.getDriftScore(),
                policy.getMaximumDrift(),
                null
        );
    }

    private EvaluationFinding robustnessFinding(
            EvaluationRun run,
            EvaluationPolicy policy
    ) {
        FindingSeverity severity =
                run.getRobustnessScore().compareTo(
                        policy.getMinimumRobustnessScore()
                ) < 0
                        ? FindingSeverity.HIGH
                        : FindingSeverity.INFO;

        return finding(
                run,
                FindingType.ROBUSTNESS,
                severity,
                "ROBUSTNESS_RESULT",
                "Robustness evaluation completed",
                "The model was tested with small changes to low-risk and high-risk applications.",
                run.getRobustnessScore(),
                policy.getMinimumRobustnessScore(),
                null
        );
    }

    private EvaluationFinding availabilityFinding(
            EvaluationRun run
    ) {
        boolean unavailable = run.getFailedPredictions() > 0;

        return finding(
                run,
                FindingType.MODEL_AVAILABILITY,
                unavailable
                        ? FindingSeverity.CRITICAL
                        : FindingSeverity.INFO,
                "MODEL_AVAILABILITY_RESULT",
                "Model endpoint availability checked",
                unavailable
                        ? "One or more model endpoint requests failed."
                        : "All controlled model endpoint requests succeeded.",
                BigDecimal.valueOf(
                        run.getFailedPredictions()
                ),
                BigDecimal.ZERO,
                null
        );
    }

    private EvaluationFinding complianceFinding(
            EvaluationRun run,
            EvaluationPolicy policy
    ) {
        boolean missing = policy.isRequireComplianceEvidence()
                && !run.isComplianceEvidencePresent();

        return finding(
                run,
                FindingType.COMPLIANCE_EVIDENCE,
                missing
                        ? FindingSeverity.CRITICAL
                        : FindingSeverity.INFO,
                "COMPLIANCE_EVIDENCE_RESULT",
                "Compliance evidence checked",
                missing
                        ? "Required regulatory evidence is missing."
                        : "Required regulatory evidence is present.",
                run.isComplianceEvidencePresent()
                        ? BigDecimal.ONE
                        : BigDecimal.ZERO,
                policy.isRequireComplianceEvidence()
                        ? BigDecimal.ONE
                        : BigDecimal.ZERO,
                null
        );
    }

    private EvaluationFinding finding(
            EvaluationRun run,
            FindingType type,
            FindingSeverity severity,
            String code,
            String title,
            String details,
            BigDecimal observedValue,
            BigDecimal thresholdValue,
            String affectedGroup
    ) {
        return EvaluationFinding.builder()
                .evaluationRun(run)
                .type(type)
                .severity(severity)
                .findingCode(code)
                .title(title)
                .details(details)
                .observedValue(observedValue)
                .thresholdValue(thresholdValue)
                .affectedGroup(affectedGroup)
                .resolved(false)
                .build();
    }

    private EvaluationFindingResponse toResponse(
            EvaluationFinding finding
    ) {
        return new EvaluationFindingResponse(
                finding.getId(),
                finding.getEvaluationRun().getId(),
                finding.getType(),
                finding.getSeverity(),
                finding.getFindingCode(),
                finding.getTitle(),
                finding.getDetails(),
                finding.getObservedValue(),
                finding.getThresholdValue(),
                finding.getAffectedGroup(),
                finding.isResolved(),
                finding.getResolutionNote(),
                finding.getResolvedAt(),
                finding.getCreatedAt()
        );
    }
}