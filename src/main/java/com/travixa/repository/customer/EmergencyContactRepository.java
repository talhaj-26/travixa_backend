package com.travixa.repository.customer;

import com.travixa.entity.customer_engagement.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
}
