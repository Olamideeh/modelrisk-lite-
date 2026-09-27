package com.example.modelrisk.controller;

import com.example.modelrisk.dto.ModelResponse;
import com.example.modelrisk.dto.RegisterModelRequest;
import com.example.modelrisk.service.AiModelService;
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
@RequestMapping("/api/v1/models")
public class AiModelController {

    private final AiModelService aiModelService;

    public AiModelController(AiModelService aiModelService) {
        this.aiModelService = aiModelService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MODEL_OWNER')")
    public ResponseEntity<ModelResponse> registerModel(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RegisterModelRequest request
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        UUID ownerId = UUID.fromString(jwt.getSubject());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        aiModelService.registerModel(
                                organizationId,
                                ownerId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<ModelResponse>> getModels(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                aiModelService.getOrganizationModels(organizationId)
        );
    }

    @GetMapping("/{modelId}")
    public ResponseEntity<ModelResponse> getModel(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID modelId
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                aiModelService.getModel(
                        organizationId,
                        modelId
                )
        );
    }
}