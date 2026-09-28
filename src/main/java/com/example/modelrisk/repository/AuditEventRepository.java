package com.example.modelrisk.repository;

import com.example.modelrisk.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditEventRepository
        extends JpaRepository<AuditEvent, UUID> {

    List<AuditEvent>
    findAllByOrganizationIdOrderByOccurredAtDesc(
            UUID organizationId
    );

    List<AuditEvent>
    findAllByOrganizationIdAndCorrelationIdOrderByOccurredAtAsc(
            UUID organizationId,
            String correlationId
    );
}