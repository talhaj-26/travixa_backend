package com.travixa.repository.customer;

import com.travixa.entity.customer_engagement.ExpenseEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseEntryRepository extends JpaRepository<ExpenseEntry, Long> {
}
