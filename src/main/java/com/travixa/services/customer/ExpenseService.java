package com.travixa.services.customer;

import com.travixa.entity.customer_engagement.ExpenseEntry;
import com.travixa.repository.customer.ExpenseEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseEntryRepository expenseEntryRepository;

    public ExpenseService(ExpenseEntryRepository expenseEntryRepository) {
        this.expenseEntryRepository = expenseEntryRepository;
    }

    public List<ExpenseEntry> findAll() {
        return expenseEntryRepository.findAll();
    }

    public Optional<ExpenseEntry> findById(Long id) {
        return expenseEntryRepository.findById(id);
    }

    @Transactional
    public ExpenseEntry save(ExpenseEntry expenseEntry) {
        return expenseEntryRepository.save(expenseEntry);
    }
}
