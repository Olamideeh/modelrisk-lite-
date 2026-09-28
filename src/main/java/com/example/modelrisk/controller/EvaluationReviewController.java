package com.example.modelrisk.controller;

import com.example.modelrisk.dto.EvaluationReviewResponse;
import com.example.modelrisk.dto.SubmitEvaluationReviewRequest;
import com.example.modelrisk.service.EvaluationReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationReviewController {

    private final EvaluationReviewService evaluationReviewService;

    public EvaluationReviewController(
            EvaluationReviewService evaluationReviewService
    ) {
        this.evaluationReviewService = evaluationReviewService;
    }

    @PostMapping("/{runId}/review")
    @PreAuthorize("hasRole('RISK_REVIEWER')")
    public ResponseEntity<EvaluationReviewResponse> submitReview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID runId,
            @Valid @RequestBody
            SubmitEvaluationReviewRequest request
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );
        UUID reviewerId = UUID.fromString(jwt.getSubject());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        evaluationReviewService.submitReview(
                                organizationId,
                                reviewerId,
                                runId,
                                request
                        )
                );
    }

    @GetMapping("/{runId}/review")
    public ResponseEntity<EvaluationReviewResponse> getReview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID runId
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                evaluationReviewService.getReview(
                        organizationId,
                        runId
                )
        );
    }
}