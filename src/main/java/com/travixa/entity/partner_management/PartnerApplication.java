package com.travixa.entity.partner_management;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.PartnerApplicationStatus;
import com.travixa.enums.PartnerBusinessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "partner_applications", indexes = {
		@Index(name = "idx_partner_application_user", columnList = "applicant_user_name"),
		@Index(name = "idx_partner_application_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class PartnerApplication extends BaseAuditEntity {

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "applicant_user_name", nullable = false)
	private User applicant;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", unique = true)
	private PartnerOrganization organization;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private PartnerBusinessType businessType;

	@NotBlank
	@Size(max = 160)
	@Column(nullable = false, length = 160)
	private String businessName;

	@Size(max = 180)
	@Column(length = 180)
	private String legalName;

	@Size(max = 100)
	@Column(length = 100)
	private String registrationNumber;

	@NotBlank
	@Email
	@Size(max = 254)
	@Column(nullable = false, length = 254)
	private String contactEmail;

	@Size(max = 32)
	@Column(length = 32)
	private String contactPhone;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 24)
	private PartnerApplicationStatus status = PartnerApplicationStatus.DRAFT;

	private Instant submittedAt;
}
