package com.example.modelrisk.entity;

import com.example.modelrisk.enums.ModelUseCase;
import com.example.modelrisk.enums.RiskTier;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "evaluation_policies",
        indexes = {
                @Index(
                        name = "idx_policy_organization",
                        columnList = "organization_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "model_use_case",
            nullable = false,
            length = 40
    )
    private ModelUseCase modelUseCase;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "risk_tier",
            nullable = false,
            length = 20
    )
    private RiskTier riskTier;

    @Column(
            name = "minimum_accuracy",
            nullable = false,
            precision = 6,
            scale = 5
    )
    private BigDecimal minimumAccuracy;

    @Column(
            name = "accuracy_warning_threshold",
            nullable = false,
            precision = 6,
            scale = 5
    )
    private BigDecimal accuracyWarningThreshold;

    @Column(
            name = "maximum_fairness_gap",
            nullable = false,
            precision = 6,
            scale = 5
    )
    private BigDecimal maximumFairnessGap;

    @Column(
            name = "fairness_warning_threshold",
            nullable = false,
            precision = 6,
            scale = 5
    )
    private BigDecimal fairnessWarningThreshold;

    @Column(
            name = "maximum_drift",
            nullable = false,
            precision = 6,
            scale = 5
    )
    private BigDecimal maximumDrift;

    @Column(
            name = "drift_warning_threshold",
            nullable = false,
            precision = 6,
            scale = 5
    )
    private BigDecimal driftWarningThreshold;

    @Column(
            name = "minimum_robustness_score",
            nullable = false,
            precision = 6,
            scale = 5
    )
    private BigDecimal minimumRobustnessScore;

    @Builder.Default
    @Column(
            name = "require_compliance_evidence",
            nullable = false
    )
    private boolean requireComplianceEvidence = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_policy_organization"
            )
    )
    private Organization organization;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void beforeInsert() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = Instant.now();
    }
}