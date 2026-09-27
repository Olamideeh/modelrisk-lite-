package com.example.modelrisk.entity;

import com.example.modelrisk.enums.FindingSeverity;
import com.example.modelrisk.enums.FindingType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "evaluation_findings",
        indexes = {
                @Index(
                        name = "idx_finding_evaluation",
                        columnList = "evaluation_run_id"
                ),
                @Index(
                        name = "idx_finding_severity",
                        columnList = "severity"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationFinding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "evaluation_run_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_finding_evaluation"
            )
    )
    private EvaluationRun evaluationRun;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private FindingType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FindingSeverity severity;

    @Column(
            name = "finding_code",
            nullable = false,
            length = 80
    )
    private String findingCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String details;

    @Column(
            name = "observed_value",
            precision = 12,
            scale = 5
    )
    private BigDecimal observedValue;

    @Column(
            name = "threshold_value",
            precision = 12,
            scale = 5
    )
    private BigDecimal thresholdValue;

    @Column(
            name = "affected_group",
            length = 150
    )
    private String affectedGroup;

    @Builder.Default
    @Column(nullable = false)
    private boolean resolved = false;

    @Column(
            name = "resolution_note",
            length = 1000
    )
    private String resolutionNote;

    private Instant resolvedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void beforeInsert() {
        createdAt = Instant.now();
    }
}