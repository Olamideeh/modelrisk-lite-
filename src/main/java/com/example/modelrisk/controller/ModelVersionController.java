package com.example.modelrisk.controller;

import com.example.modelrisk.dto.CreateModelVersionRequest;
import com.example.modelrisk.dto.ModelVersionResponse;
import com.example.modelrisk.service.ModelVersionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ModelVersionController {

    private final ModelVersionService modelVersionService;

    public ModelVersionController(
            ModelVersionService modelVersionService
    ) {
        this.modelVersionService = modelVersionService;
    }

    @PostMapping("/models/{modelId}/versions")
    @PreAuthorize("hasRole('MODEL_OWNER')")
    public ResponseEntity<ModelVersionResponse> createVersion(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID modelId,
            @Valid @RequestBody CreateModelVersionRequest request
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );
        UUID userId = UUID.fromString(jwt.getSubject());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        modelVersionService.createVersion(
                                organizationId,
                                userId,
                                modelId,
                                request
                        )
                );
    }

    @PostMapping("/model-versions/{versionId}/submit")
    @PreAuthorize("hasRole('MODEL_OWNER')")
    public ResponseEntity<ModelVersionResponse> submitVersion(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID versionId
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );
        UUID userId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
                modelVersionService.submitVersion(
                        organizationId,
                        userId,
                        versionId
                )
        );
    }

    @GetMapping("/models/{modelId}/versions")
    public ResponseEntity<List<ModelVersionResponse>>
    getModelVersions(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID modelId
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                modelVersionService.getModelVersions(
                        organizationId,
                        modelId
                )
        );
    }
}