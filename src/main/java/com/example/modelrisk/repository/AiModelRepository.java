package com.example.modelrisk.repository;

import com.example.modelrisk.entity.AiModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiModelRepository
        extends JpaRepository<AiModel, UUID> {

    boolean existsByOrganizationIdAndModelKeyIgnoreCase(
            UUID organizationId,
            String modelKey
    );

    Optional<AiModel> findByIdAndOrganizationId(
            UUID id,
            UUID organizationId
    );

    List<AiModel> findAllByOrganizationIdOrderByCreatedAtDesc(
            UUID organizationId
    );
}