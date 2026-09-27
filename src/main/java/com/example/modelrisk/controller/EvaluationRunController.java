package com.example.modelrisk.controller;

import com.example.modelrisk.dto.EvaluationRunResponse;
import com.example.modelrisk.dto.StartEvaluationRequest;
import com.example.modelrisk.service.EvaluationRunService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.example.modelrisk.service.EvaluationExecutionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationRunController {

    private final EvaluationExecutionService evaluationExecutionService;
    private final EvaluationRunService evaluationRunService;

    public EvaluationRunController(
            EvaluationRunService evaluationRunService,
            EvaluationExecutionService evaluationExecutionService
    ) {
        this.evaluationRunService = evaluationRunService;
        this.evaluationExecutionService =
                evaluationExecutionService;
    }
    @PostMapping("/model-versions/{modelVersionId}")
    @PreAuthorize(
            "hasAnyRole('RISK_REVIEWER', 'ADMIN')"
    )
    public ResponseEntity<EvaluationRunResponse> startEvaluation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID modelVersionId,
            @RequestHeader("Idempotency-Key")
            String idempotencyKey,
            @Valid @RequestBody StartEvaluationRequest request
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );
        UUID userId = UUID.fromString(jwt.getSubject());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        evaluationRunService.startEvaluation(
                                organizationId,
                                userId,
                                modelVersionId,
                                idempotencyKey,
                                request
                        )
                );
    }

    @GetMapping("/{runId}")
    public ResponseEntity<EvaluationRunResponse> getEvaluation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID runId
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                evaluationRunService.getEvaluation(
                        organizationId,
                        runId
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<EvaluationRunResponse>>
    getEvaluations(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                evaluationRunService.getEvaluations(
                        organizationId
                )
        );
    }
    @PostMapping("/{runId}/execute")
    @PreAuthorize(
            "hasAnyRole('RISK_REVIEWER', 'ADMIN')"
    )
    public ResponseEntity<EvaluationRunResponse> executeEvaluation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID runId
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        evaluationExecutionService.execute(
                organizationId,
                runId
        );

        return ResponseEntity.ok(
                evaluationRunService.getEvaluation(
                        organizationId,
                        runId
                )
        );
    }
}