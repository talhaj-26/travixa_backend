package com.travixa.entity.customer_engagement;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "emergency_contacts", indexes = {
        @Index(name = "idx_emergency_contacts_user", columnList = "user_name")
})
@Getter
@Setter
@NoArgsConstructor
public class EmergencyContact extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_name", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String contactName;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String relationship;

    @NotBlank
    @Size(max = 32)
    @Column(nullable = false, length = 32)
    private String phoneNumber;

    @Email
    @Size(max = 254)
    @Column(length = 254)
    private String email;

    @Column(nullable = false)
    private boolean primaryContact;

    @Column(nullable = false)
    private boolean active = true;
}
