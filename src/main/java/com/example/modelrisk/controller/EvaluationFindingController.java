package com.example.modelrisk.controller;

import com.example.modelrisk.dto.EvaluationFindingResponse;
import com.example.modelrisk.service.EvaluationFindingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationFindingController {

    private final EvaluationFindingService evaluationFindingService;

    public EvaluationFindingController(
            EvaluationFindingService evaluationFindingService
    ) {
        this.evaluationFindingService = evaluationFindingService;
    }

    @GetMapping("/{runId}/findings")
    public ResponseEntity<List<EvaluationFindingResponse>>
    getFindings(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID runId
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                evaluationFindingService.getFindings(
                        organizationId,
                        runId
                )
        );
    }
}