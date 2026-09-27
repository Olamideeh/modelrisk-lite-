package com.example.modelrisk.entity;

import com.example.modelrisk.enums.ModelVersionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "model_versions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_model_version_label",
                        columnNames = {
                                "model_id",
                                "version_label"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_model_version_model",
                        columnList = "model_id"
                ),
                @Index(
                        name = "idx_model_version_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "model_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_version_model"
            )
    )
    private AiModel model;

    @Column(
            name = "version_label",
            nullable = false,
            length = 50
    )
    private String versionLabel;

    @Column(
            name = "prediction_endpoint_url",
            nullable = false,
            length = 500
    )
    private String predictionEndpointUrl;

    @Column(nullable = false, length = 100)
    private String algorithm;

    @Column(
            name = "training_dataset_version",
            length = 100
    )
    private String trainingDatasetVersion;

    @Column(
            name = "artifact_checksum",
            length = 64
    )
    private String artifactChecksum;

    @Column(length = 1000)
    private String notes;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ModelVersionStatus status =
            ModelVersionStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "created_by_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_version_creator"
            )
    )
    private PlatformUser createdBy;

    private Instant submittedAt;

    private Instant approvedAt;

    private Instant deployedAt;

    private Instant retiredAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long rowVersion;

    @PrePersist
    public void beforeInsert() {
        Instant now = Instant.now();

        if (status == null) {
            status = ModelVersionStatus.DRAFT;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = Instant.now();
    }
}