package com.travixa.dto.tour;

import com.travixa.enums.CurrencyCode;

import java.math.BigDecimal;

public record GuideOfferingResponse(Long id, Long guideProfileId, String userName,
                                    Long organizationId, String organizationName, String biography,
                                    String languageCodes, String name, String description,
                                    BigDecimal pricePerHour, CurrencyCode currency, boolean active) { }
