package com.infracore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "activity_logs")
public class ActivityLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "performed_by_id", nullable = false)
    private Long performedById;

    @Column(name = "performed_by_name", nullable = false)
    private String performedByName;

    @Column(name = "performed_by_username", nullable = false)
    private String performedByUsername;

    @Column(name = "target_entity_id", nullable = false)
    private Long targetEntityId;

    @Column(name = "target_entity_name", nullable = false)
    private String targetEntityName;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_entity_type", nullable = false)
    private EntityType targetEntityType;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(name = "related_entity_name")
    private String relatedEntityName;

    @Enumerated(EnumType.STRING)
    @Column(name = "related_entity_type")
    private EntityType relatedEntityType;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    public enum ActivityType {
        CLIENT_CREATED,
        CLIENT_UPDATED,
        CLIENT_DELETED,
        CASE_CREATED,
        CASE_UPDATED,
        CASE_DELETED,
        CASE_ASSIGNED,
        DOCUMENT_CREATED,
        DOCUMENT_UPDATED,
        DOCUMENT_DELETED,
        USER_CREATED,
        USER_UPDATED
    }

    public enum EntityType {
        CLIENT,
        CASE,
        DOCUMENT,
        USER
    }
} 