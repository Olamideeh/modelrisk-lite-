package com.example.modelrisk.controller;

import com.example.modelrisk.dto.ModelConnectionTestResponse;
import com.example.modelrisk.service.ModelConnectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/model-versions")
public class ModelConnectionController {

    private final ModelConnectionService modelConnectionService;

    public ModelConnectionController(
            ModelConnectionService modelConnectionService
    ) {
        this.modelConnectionService = modelConnectionService;
    }

    @PostMapping("/{versionId}/connection-test")
    @PreAuthorize(
            "hasAnyRole('MODEL_OWNER', 'RISK_REVIEWER', 'ADMIN')"
    )
    public ResponseEntity<ModelConnectionTestResponse> testConnection(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID versionId
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                modelConnectionService.testConnection(
                        organizationId,
                        versionId
                )
        );
    }
}