package com.example.modelrisk.service;

import com.example.modelrisk.dto.CreateModelVersionRequest;
import com.example.modelrisk.dto.ModelVersionResponse;
import com.example.modelrisk.entity.AiModel;
import com.example.modelrisk.entity.ModelVersion;
import com.example.modelrisk.entity.PlatformUser;
import com.example.modelrisk.enums.ModelVersionStatus;
import com.example.modelrisk.exception.ConflictException;
import com.example.modelrisk.exception.ResourceNotFoundException;
import com.example.modelrisk.repository.AiModelRepository;
import com.example.modelrisk.repository.ModelVersionRepository;
import com.example.modelrisk.repository.PlatformUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ModelVersionService {

    private final ModelVersionRepository modelVersionRepository;
    private final AiModelRepository aiModelRepository;
    private final PlatformUserRepository platformUserRepository;

    public ModelVersionService(
            ModelVersionRepository modelVersionRepository,
            AiModelRepository aiModelRepository,
            PlatformUserRepository platformUserRepository
    ) {
        this.modelVersionRepository = modelVersionRepository;
        this.aiModelRepository = aiModelRepository;
        this.platformUserRepository = platformUserRepository;
    }

    @Transactional
    public ModelVersionResponse createVersion(
            UUID organizationId,
            UUID userId,
            UUID modelId,
            CreateModelVersionRequest request
    ) {
        AiModel model = findOwnedModel(
                organizationId,
                userId,
                modelId
        );

        String versionLabel = request.versionLabel().trim();

        if (modelVersionRepository
                .existsByModelIdAndVersionLabelIgnoreCase(
                        modelId,
                        versionLabel
                )) {
            throw new ConflictException(
                    "Version label already exists for this model"
            );
        }

        PlatformUser creator = platformUserRepository
                .findByIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Model owner not found"
                ));

        ModelVersion version = ModelVersion.builder()
                .model(model)
                .versionLabel(versionLabel)
                .predictionEndpointUrl(
                        request.predictionEndpointUrl().trim()
                )
                .algorithm(request.algorithm().trim())
                .trainingDatasetVersion(
                        request.trainingDatasetVersion().trim()
                )
                .artifactChecksum(
                        request.artifactChecksum()
                                .trim()
                                .toLowerCase()
                )
                .notes(
                        request.notes() == null
                                ? null
                                : request.notes().trim()
                )
                .status(ModelVersionStatus.DRAFT)
                .createdBy(creator)
                .build();

        return toResponse(modelVersionRepository.save(version));
    }

    @Transactional
    public ModelVersionResponse submitVersion(
            UUID organizationId,
            UUID userId,
            UUID versionId
    ) {
        ModelVersion version = modelVersionRepository
                .findByIdAndModelOrganizationId(
                        versionId,
                        organizationId
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Model version not found"
                ));

        if (!version.getModel().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "Only the model owner can submit this version"
            );
        }

        if (version.getStatus() != ModelVersionStatus.DRAFT) {
            throw new ConflictException(
                    "Only a DRAFT version can be submitted"
            );
        }

        version.setStatus(ModelVersionStatus.SUBMITTED);
        version.setSubmittedAt(Instant.now());

        return toResponse(modelVersionRepository.save(version));
    }

    @Transactional(readOnly = true)
    public List<ModelVersionResponse> getModelVersions(
            UUID organizationId,
            UUID modelId
    ) {
        aiModelRepository
                .findByIdAndOrganizationId(modelId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AI model not found"
                ));

        return modelVersionRepository
                .findAllByModelIdOrderByCreatedAtDesc(modelId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AiModel findOwnedModel(
            UUID organizationId,
            UUID userId,
            UUID modelId
    ) {
        AiModel model = aiModelRepository
                .findByIdAndOrganizationId(modelId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AI model not found"
                ));

        if (!model.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "Only the model owner can create its versions"
            );
        }

        if (!model.isActive()) {
            throw new ConflictException("AI model is inactive");
        }

        return model;
    }

    private ModelVersionResponse toResponse(ModelVersion version) {
        return new ModelVersionResponse(
                version.getId(),
                version.getModel().getId(),
                version.getModel().getName(),
                version.getVersionLabel(),
                version.getPredictionEndpointUrl(),
                version.getAlgorithm(),
                version.getTrainingDatasetVersion(),
                version.getArtifactChecksum(),
                version.getNotes(),
                version.getStatus(),
                version.getCreatedBy().getId(),
                version.getSubmittedAt(),
                version.getApprovedAt(),
                version.getDeployedAt(),
                version.getRetiredAt(),
                version.getCreatedAt()
        );
    }
}