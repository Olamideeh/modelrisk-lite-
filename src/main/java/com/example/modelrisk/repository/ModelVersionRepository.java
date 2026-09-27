package com.example.modelrisk.repository;

import com.example.modelrisk.entity.ModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModelVersionRepository
        extends JpaRepository<ModelVersion, UUID> {

    boolean existsByModelIdAndVersionLabelIgnoreCase(
            UUID modelId,
            String versionLabel
    );

    Optional<ModelVersion> findByIdAndModelOrganizationId(
            UUID versionId,
            UUID organizationId
    );

    List<ModelVersion> findAllByModelIdOrderByCreatedAtDesc(
            UUID modelId
    );
}