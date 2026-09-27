package com.example.modelrisk.entity;

import com.example.modelrisk.enums.AuditActorType;
import com.example.modelrisk.enums.AuditEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "audit_events",
        indexes = {
                @Index(
                        name = "idx_audit_organization",
                        columnList = "organization_id"
                ),
                @Index(
                        name = "idx_audit_correlation",
                        columnList = "correlation_id"
                ),
                @Index(
                        name = "idx_audit_entity",
                        columnList = "entity_type, entity_id"
                )
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "organization_id",
            nullable = false
    )
    private UUID organizationId;

    @Column(
            name = "correlation_id",
            nullable = false,
            length = 100
    )
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "actor_type",
            nullable = false,
            length = 20
    )
    private AuditActorType actorType;

    @Column(
            name = "actor_id",
            length = 100
    )
    private String actorId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "event_type",
            nullable = false,
            length = 50
    )
    private AuditEventType eventType;

    @Column(
            name = "entity_type",
            nullable = false,
            length = 50
    )
    private String entityType;

    @Column(
            name = "entity_id",
            nullable = false,
            length = 100
    )
    private String entityId;

    @Column(
            name = "previous_state",
            length = 50
    )
    private String previousState;

    @Column(
            name = "new_state",
            length = 50
    )
    private String newState;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false, updatable = false)
    private Instant occurredAt;

    @PrePersist
    public void beforeInsert() {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}