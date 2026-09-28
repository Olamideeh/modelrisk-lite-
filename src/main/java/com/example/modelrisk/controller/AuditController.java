package com.example.modelrisk.controller;

import com.example.modelrisk.dto.AuditEventResponse;
import com.example.modelrisk.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-events")
@PreAuthorize(
        "hasAnyRole('ADMIN', 'RISK_REVIEWER', 'COMPLIANCE_OFFICER')"
)
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<List<AuditEventResponse>> getEvents(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false)
            String correlationId
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        if (correlationId != null
                && !correlationId.isBlank()) {
            return ResponseEntity.ok(
                    auditService.getCorrelationEvents(
                            organizationId,
                            correlationId.trim()
                    )
            );
        }

        return ResponseEntity.ok(
                auditService.getOrganizationEvents(
                        organizationId
                )
        );
    }
}