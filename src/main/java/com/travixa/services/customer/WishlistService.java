package com.travixa.services.customer;

import com.travixa.entity.customer_engagement.WishlistItem;
import com.travixa.repository.customer.WishlistItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;

    public WishlistService(WishlistItemRepository wishlistItemRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
    }

    public List<WishlistItem> findAll() {
        return wishlistItemRepository.findAll();
    }

    public Optional<WishlistItem> findById(Long id) {
        return wishlistItemRepository.findById(id);
    }

    @Transactional
    public WishlistItem save(WishlistItem wishlistItem) {
        return wishlistItemRepository.save(wishlistItem);
    }
}
