package com.travixa.services.customer;

import com.travixa.entity.customer_engagement.SavedTrip;
import com.travixa.repository.customer.SavedTripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SavedTripService {

    private final SavedTripRepository savedTripRepository;

    public SavedTripService(SavedTripRepository savedTripRepository) {
        this.savedTripRepository = savedTripRepository;
    }

    public List<SavedTrip> findAll() {
        return savedTripRepository.findAll();
    }

    public Optional<SavedTrip> findById(Long id) {
        return savedTripRepository.findById(id);
    }

    @Transactional
    public SavedTrip save(SavedTrip savedTrip) {
        return savedTripRepository.save(savedTrip);
    }
}
