package com.example.modelrisk.service;

import com.example.modelrisk.dto.CreditModelPredictionRequest;
import com.example.modelrisk.dto.CreditModelPredictionResponse;
import com.example.modelrisk.dto.ModelConnectionTestResponse;
import com.example.modelrisk.entity.ModelVersion;
import com.example.modelrisk.exception.ResourceNotFoundException;
import com.example.modelrisk.repository.ModelVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ModelConnectionService {

    private final ModelVersionRepository modelVersionRepository;
    private final CreditModelClient creditModelClient;

    public ModelConnectionService(
            ModelVersionRepository modelVersionRepository,
            CreditModelClient creditModelClient
    ) {
        this.modelVersionRepository = modelVersionRepository;
        this.creditModelClient = creditModelClient;
    }

    @Transactional(readOnly = true)
    public ModelConnectionTestResponse testConnection(
            UUID organizationId,
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

        CreditModelPredictionRequest controlledCase =
                new CreditModelPredictionRequest(
                        120_000,
                        0.18,
                        12,
                        0,
                        1,
                        34,
                        "CONNECTION_TEST"
                );

        CreditModelPredictionResponse prediction =
                creditModelClient.predict(
                        version.getPredictionEndpointUrl(),
                        controlledCase
                );

        return new ModelConnectionTestResponse(
                version.getId(),
                version.getPredictionEndpointUrl(),
                true,
                prediction.modelVersion(),
                prediction.decision(),
                prediction.defaultProbability(),
                "Spring Boot successfully reached the AI model"
        );
    }
}