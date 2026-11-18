package com.travixa.repository.customer;

import com.travixa.entity.customer_engagement.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
}
