package com.travixa.services.customer;

import com.travixa.entity.customer_engagement.TravelChecklistItem;
import com.travixa.repository.customer.TravelChecklistItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TravelChecklistService {

    private final TravelChecklistItemRepository travelChecklistItemRepository;

    public TravelChecklistService(TravelChecklistItemRepository travelChecklistItemRepository) {
        this.travelChecklistItemRepository = travelChecklistItemRepository;
    }

    public List<TravelChecklistItem> findAll() {
        return travelChecklistItemRepository.findAll();
    }

    public Optional<TravelChecklistItem> findById(Long id) {
        return travelChecklistItemRepository.findById(id);
    }

    @Transactional
    public TravelChecklistItem save(TravelChecklistItem travelChecklistItem) {
        return travelChecklistItemRepository.save(travelChecklistItem);
    }
}
