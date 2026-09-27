package com.example.modelrisk.service;

import com.example.modelrisk.dto.ModelResponse;
import com.example.modelrisk.dto.RegisterModelRequest;
import com.example.modelrisk.entity.AiModel;
import com.example.modelrisk.entity.PlatformUser;
import com.example.modelrisk.exception.ConflictException;
import com.example.modelrisk.exception.ResourceNotFoundException;
import com.example.modelrisk.repository.AiModelRepository;
import com.example.modelrisk.repository.PlatformUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AiModelService {

    private final AiModelRepository aiModelRepository;
    private final PlatformUserRepository platformUserRepository;

    public AiModelService(
            AiModelRepository aiModelRepository,
            PlatformUserRepository platformUserRepository
    ) {
        this.aiModelRepository = aiModelRepository;
        this.platformUserRepository = platformUserRepository;
    }

    @Transactional
    public ModelResponse registerModel(
            UUID organizationId,
            UUID ownerId,
            RegisterModelRequest request
    ) {
        PlatformUser owner = platformUserRepository
                .findByIdAndOrganizationId(ownerId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Model owner not found"
                ));

        String modelKey = request.modelKey()
                .trim()
                .toLowerCase();

        if (aiModelRepository
                .existsByOrganizationIdAndModelKeyIgnoreCase(
                        organizationId,
                        modelKey
                )) {
            throw new ConflictException(
                    "Model key is already registered"
            );
        }

        AiModel model = AiModel.builder()
                .modelKey(modelKey)
                .name(request.name().trim())
                .description(
                        request.description() == null
                                ? null
                                : request.description().trim()
                )
                .useCase(request.useCase())
                .riskTier(request.riskTier())
                .organization(owner.getOrganization())
                .owner(owner)
                .active(true)
                .build();

        return toResponse(aiModelRepository.save(model));
    }

    @Transactional(readOnly = true)
    public List<ModelResponse> getOrganizationModels(
            UUID organizationId
    ) {
        return aiModelRepository
                .findAllByOrganizationIdOrderByCreatedAtDesc(
                        organizationId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ModelResponse getModel(
            UUID organizationId,
            UUID modelId
    ) {
        AiModel model = aiModelRepository
                .findByIdAndOrganizationId(modelId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AI model not found"
                ));

        return toResponse(model);
    }

    private ModelResponse toResponse(AiModel model) {
        return new ModelResponse(
                model.getId(),
                model.getModelKey(),
                model.getName(),
                model.getDescription(),
                model.getUseCase(),
                model.getRiskTier(),
                model.getOrganization().getId(),
                model.getOwner().getId(),
                model.getOwner().getFullName(),
                model.isActive(),
                model.getCreatedAt()
        );
    }
}