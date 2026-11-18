package com.travixa.entity.platform_cms_support;

import com.travixa.entity.identity_access.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_actor_time", columnList = "actor_user_name, occurred_at"),
        @Index(name = "idx_audit_logs_resource", columnList = "resource_type, resource_id")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_name")
    private User actor;

    @NotBlank
    @Size(max = 100)
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Size(max = 80)
    @Column(name = "resource_type", length = 80)
    private String resourceType;

    @Size(max = 120)
    @Column(name = "resource_id", length = 120)
    private String resourceId;

    @Column(name = "details_json", length = 8000)
    private String detailsJson;

    @Size(max = 64)
    @Column(name = "source_ip", length = 64)
    private String sourceIp;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;
}
