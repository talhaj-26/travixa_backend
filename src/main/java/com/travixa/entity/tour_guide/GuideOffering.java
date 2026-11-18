package com.travixa.entity.tour_guide;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "guide_offerings")
@Getter
@Setter
@NoArgsConstructor
public class GuideOffering extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_profile_id", nullable = false)
    private TourGuideProfile guideProfile;

    @NotBlank
    @Size(max = 140)
    @Column(nullable = false, length = 140)
    private String name;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "price_per_hour", nullable = false, precision = 14, scale = 2)
    private BigDecimal pricePerHour;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;

    @Column(nullable = false)
    private boolean active = true;
}
