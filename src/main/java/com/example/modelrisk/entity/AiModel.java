package com.example.modelrisk.entity;

import com.example.modelrisk.enums.ModelUseCase;
import com.example.modelrisk.enums.RiskTier;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ai_models",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_organization_model_key",
                        columnNames = {
                                "organization_id",
                                "model_key"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_ai_model_organization",
                        columnList = "organization_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "model_key",
            nullable = false,
            length = 100
    )
    private String modelKey;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "use_case",
            nullable = false,
            length = 40
    )
    private ModelUseCase useCase;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "risk_tier",
            nullable = false,
            length = 20
    )
    private RiskTier riskTier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_model_organization"
            )
    )
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_model_owner"
            )
    )
    private PlatformUser owner;

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