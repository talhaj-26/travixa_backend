package com.travixa.repository.customer;

import com.travixa.entity.customer_engagement.TravelJournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelJournalEntryRepository extends JpaRepository<TravelJournalEntry, Long> {
}
