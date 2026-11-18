package com.travixa.entity.hotel_stays;

import com.travixa.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "destinations", indexes = {
        @Index(name = "idx_destinations_country_region", columnList = "country_code, region")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_destinations_slug", columnNames = "slug")
})
@Getter
@Setter
@NoArgsConstructor
public class Destination extends BaseAuditEntity {

    @NotBlank
    @Size(max = 140)
    @Column(nullable = false, length = 140)
    private String name;

    @NotBlank
    @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String slug;

    @NotBlank
    @Size(max = 2)
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Size(max = 120)
    @Column(length = 120)
    private String region;

    @Size(max = 3000)
    @Column(length = 3000)
    private String description;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    @Size(max = 50)
    @Column(length = 50)
    private String category;

    @Size(max = 1000)
    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(nullable = false)
    private boolean published;
}
