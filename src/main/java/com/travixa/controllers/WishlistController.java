package com.travixa.controllers;

import com.travixa.dto.engagement.WishlistResponse;
import com.travixa.entity.customer_engagement.WishlistItem;
import com.travixa.services.customer.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public List<WishlistResponse> findAll() {
        return wishlistService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public WishlistResponse findById(@PathVariable Long id) {
        return wishlistService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist item not found"));
    }

    private WishlistResponse toResponse(WishlistItem item) {
        return new WishlistResponse(item.getId(), item.getItemType(), item.getTargetId(), item.getNote());
    }
}
