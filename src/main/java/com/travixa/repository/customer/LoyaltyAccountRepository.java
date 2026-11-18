package com.travixa.repository.customer;

import com.travixa.entity.customer_engagement.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
}
