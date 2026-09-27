package com.example.modelrisk.entity;

import com.example.modelrisk.enums.ReviewDecision;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "evaluation_reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_review_evaluation",
                        columnNames = "evaluation_run_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "evaluation_run_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_review_evaluation"
            )
    )
    private EvaluationRun evaluationRun;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "reviewer_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_review_reviewer"
            )
    )
    private PlatformUser reviewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReviewDecision decision;

    @Column(nullable = false, length = 1500)
    private String reviewNote;

    @Column(nullable = false, updatable = false)
    private Instant reviewedAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void beforeInsert() {
        Instant now = Instant.now();
        reviewedAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = Instant.now();
    }
}