package com.travixa.services.customer;

import com.travixa.entity.customer_engagement.TravelJournalEntry;
import com.travixa.repository.customer.TravelJournalEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TravelJournalService {

    private final TravelJournalEntryRepository travelJournalEntryRepository;

    public TravelJournalService(TravelJournalEntryRepository travelJournalEntryRepository) {
        this.travelJournalEntryRepository = travelJournalEntryRepository;
    }

    public List<TravelJournalEntry> findAll() {
        return travelJournalEntryRepository.findAll();
    }

    public Optional<TravelJournalEntry> findById(Long id) {
        return travelJournalEntryRepository.findById(id);
    }

    @Transactional
    public TravelJournalEntry save(TravelJournalEntry travelJournalEntry) {
        return travelJournalEntryRepository.save(travelJournalEntry);
    }
}
