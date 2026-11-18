package com.travixa.dto.intelligence;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiTripPlanRequest(@NotBlank @Size(max = 150) String destination) {
}
