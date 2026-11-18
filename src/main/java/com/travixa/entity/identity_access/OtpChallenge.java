package com.travixa.entity.identity_access;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.OtpChannel;
import com.travixa.enums.OtpPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "otp_challenges", indexes = {
        @Index(name = "idx_otp_destination_purpose", columnList = "destination, purpose"),
        @Index(name = "idx_otp_user", columnList = "user_name")
})
@Getter
@Setter
@NoArgsConstructor
public class OtpChallenge extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_name")
    private User user;

    @NotBlank
    @Size(max = 254)
    @Column(nullable = false, length = 254)
    private String destination;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OtpChannel channel;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OtpPurpose purpose;

    @NotBlank
    @Column(nullable = false, length = 64)
    private String codeHash;

    @NotNull
    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private int failedAttempts;

    private Instant consumedAt;
}
