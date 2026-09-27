package com.example.modelrisk.entity;

import com.example.modelrisk.enums.EvaluationOutcome;
import com.example.modelrisk.enums.EvaluationRunStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "evaluation_runs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_evaluation_reference",
                        columnNames = "reference"
                ),
                @UniqueConstraint(
                        name = "uk_org_evaluation_idempotency",
                        columnNames = {
                                "organization_id",
                                "idempotency_key"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_evaluation_model_version",
                        columnList = "model_version_id"
                ),
                @Index(
                        name = "idx_evaluation_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String reference;

    @Column(
            name = "idempotency_key",
            nullable = false,
            length = 100
    )
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_evaluation_organization"
            )
    )
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "model_version_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_evaluation_model_version"
            )
    )
    private ModelVersion modelVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "baseline_version_id",
            foreignKey = @ForeignKey(
                    name = "fk_evaluation_baseline_version"
            )
    )
    private ModelVersion baselineVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "policy_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_evaluation_policy"
            )
    )
    private EvaluationPolicy policy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "requested_by_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_evaluation_requester"
            )
    )
    private PlatformUser requestedBy;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EvaluationRunStatus status =
            EvaluationRunStatus.QUEUED;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EvaluationOutcome outcome;

    @Column(precision = 6, scale = 5)
    private BigDecimal accuracy;

    @Column(
            name = "fairness_gap",
            precision = 6,
            scale = 5
    )
    private BigDecimal fairnessGap;

    @Column(
            name = "drift_score",
            precision = 6,
            scale = 5
    )
    private BigDecimal driftScore;

    @Column(
            name = "robustness_score",
            precision = 6,
            scale = 5
    )
    private BigDecimal robustnessScore;

    @Column(
            name = "total_test_cases"
    )
    private Integer totalTestCases;

    @Column(
            name = "successful_predictions"
    )
    private Integer successfulPredictions;

    @Column(
            name = "failed_predictions"
    )
    private Integer failedPredictions;

    @Builder.Default
    @Column(
            name = "compliance_evidence_present",
            nullable = false
    )
    private boolean complianceEvidencePresent = false;

    @Column(
            name = "failure_reason",
            length = 1000
    )
    private String failureReason;

    private Instant startedAt;

    private Instant completedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void beforeInsert() {
        Instant now = Instant.now();

        if (status == null) {
            status = EvaluationRunStatus.QUEUED;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = Instant.now();
    }
}