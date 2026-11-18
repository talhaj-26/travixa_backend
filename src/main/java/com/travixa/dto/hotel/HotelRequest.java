package com.travixa.dto.hotel;

import com.travixa.enums.ListingStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalTime;

public record HotelRequest(
        @NotNull @Positive Long organizationId,
        @NotNull @Positive Long destinationId,
        @NotBlank @Size(max = 180) String name,
        @NotBlank @Size(max = 200) String slug,
        @Size(max = 10000) String description,
        @NotBlank @Size(max = 500) String address,
        @NotBlank
        @Email
        @Size(max = 254)
        String contactEmail,
        @Size(max = 40) String contactPhone,
        BigDecimal latitude,
        BigDecimal longitude,
        @Min(0) @Max(5) int starRating,
        @NotNull ListingStatus listingStatus,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        @Size(max = 5000) String cancellationPolicy,
        @Size(max = 2000) String childPolicy,
        @Size(max = 2000) String additionalCharges) {
}
