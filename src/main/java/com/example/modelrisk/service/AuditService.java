package com.example.modelrisk.service;

import com.example.modelrisk.dto.AuditEventResponse;
import com.example.modelrisk.entity.AuditEvent;
import com.example.modelrisk.enums.AuditActorType;
import com.example.modelrisk.enums.AuditEventType;
import com.example.modelrisk.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(
            AuditEventRepository auditEventRepository
    ) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public void record(
            UUID organizationId,
            String correlationId,
            AuditActorType actorType,
            String actorId,
            AuditEventType eventType,
            String entityType,
            String entityId,
            String previousState,
            String newState,
            String details
    ) {
        AuditEvent event = AuditEvent.builder()
                .organizationId(organizationId)
                .correlationId(correlationId)
                .actorType(actorType)
                .actorId(actorId)
                .eventType(eventType)
                .entityType(entityType)
                .entityId(entityId)
                .previousState(previousState)
                .newState(newState)
                .details(details)
                .build();

        auditEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> getOrganizationEvents(
            UUID organizationId
    ) {
        return auditEventRepository
                .findAllByOrganizationIdOrderByOccurredAtDesc(
                        organizationId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> getCorrelationEvents(
            UUID organizationId,
            String correlationId
    ) {
        return auditEventRepository
                .findAllByOrganizationIdAndCorrelationIdOrderByOccurredAtAsc(
                        organizationId,
                        correlationId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditEventResponse toResponse(AuditEvent event) {
        return new AuditEventResponse(
                event.getId(),
                event.getOrganizationId(),
                event.getCorrelationId(),
                event.getActorType(),
                event.getActorId(),
                event.getEventType(),
                event.getEntityType(),
                event.getEntityId(),
                event.getPreviousState(),
                event.getNewState(),
                event.getDetails(),
                event.getOccurredAt()
        );
    }
}