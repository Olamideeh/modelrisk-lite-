package com.example.modelrisk.dto;

import com.example.modelrisk.enums.AuditActorType;
import com.example.modelrisk.enums.AuditEventType;

import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(
        UUID id,
        UUID organizationId,
        String correlationId,
        AuditActorType actorType,
        String actorId,
        AuditEventType eventType,
        String entityType,
        String entityId,
        String previousState,
        String newState,
        String details,
        Instant occurredAt
) {
}