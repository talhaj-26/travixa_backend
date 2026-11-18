package com.travixa.services.customer;

import com.travixa.entity.customer_engagement.EmergencyContact;
import com.travixa.repository.customer.EmergencyContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;

    public EmergencyContactService(EmergencyContactRepository emergencyContactRepository) {
        this.emergencyContactRepository = emergencyContactRepository;
    }

    public List<EmergencyContact> findAll() {
        return emergencyContactRepository.findAll();
    }

    public Optional<EmergencyContact> findById(Long id) {
        return emergencyContactRepository.findById(id);
    }

    @Transactional
    public EmergencyContact save(EmergencyContact emergencyContact) {
        return emergencyContactRepository.save(emergencyContact);
    }
}
