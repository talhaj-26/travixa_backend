package com.travixa.dto.customer_companion;

public record EmergencyContactResponse(
        Long id,
        String contactName,
        String relationship,
        String phoneNumber,
        String email,
        boolean primaryContact,
        boolean active) {
}
