package com.travixa.dto.tour;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ScheduleStatus;
import java.math.BigDecimal;
import java.time.Instant;
public record PackageDepartureResponse(Long id, Long tourPackageId, Instant startsAt, Instant endsAt, int seatCapacity, int seatsRemaining, BigDecimal priceOverride, CurrencyCode currency, ScheduleStatus status) {}
