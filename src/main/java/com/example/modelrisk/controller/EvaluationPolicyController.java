package com.example.modelrisk.controller;

import com.example.modelrisk.dto.CreateEvaluationPolicyRequest;
import com.example.modelrisk.dto.EvaluationPolicyResponse;
import com.example.modelrisk.service.EvaluationPolicyService;
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
@RequestMapping("/api/v1/evaluation-policies")
public class EvaluationPolicyController {

    private final EvaluationPolicyService evaluationPolicyService;

    public EvaluationPolicyController(
            EvaluationPolicyService evaluationPolicyService
    ) {
        this.evaluationPolicyService = evaluationPolicyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EvaluationPolicyResponse> createPolicy(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateEvaluationPolicyRequest request
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        evaluationPolicyService.createPolicy(
                                organizationId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<EvaluationPolicyResponse>> getPolicies(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                evaluationPolicyService.getPolicies(
                        organizationId
                )
        );
    }
}