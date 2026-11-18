package com.travixa.services.customer;

import com.travixa.entity.customer_engagement.LoyaltyAccount;
import com.travixa.repository.customer.LoyaltyAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LoyaltyService {

    private final LoyaltyAccountRepository loyaltyAccountRepository;

    public LoyaltyService(LoyaltyAccountRepository loyaltyAccountRepository) {
        this.loyaltyAccountRepository = loyaltyAccountRepository;
    }

    public List<LoyaltyAccount> findAll() {
        return loyaltyAccountRepository.findAll();
    }

    public Optional<LoyaltyAccount> findById(Long id) {
        return loyaltyAccountRepository.findById(id);
    }

    @Transactional
    public LoyaltyAccount save(LoyaltyAccount loyaltyAccount) {
        return loyaltyAccountRepository.save(loyaltyAccount);
    }
}
