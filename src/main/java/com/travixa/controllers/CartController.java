
package com.travixa.controllers;

import com.travixa.dto.booking.CartItemRequest;
import com.travixa.dto.booking.CartItemResponse;
import com.travixa.dto.booking.CartItemUpdateRequest;
import com.travixa.dto.booking.CartResponse;
import com.travixa.entity.booking.Cart;
import com.travixa.entity.booking.CartItem;
import com.travixa.services.booking.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<CartResponse> findAll(Authentication authentication) {
        if (hasAuthority(authentication,
                "ADMIN", "SUPER_ADMIN", "FINANCE_ACCOUNTANT")) {
            return cartService.findAll()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return cartService.findAllByUserName(authentication.getName())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public CartResponse findById(
            @PathVariable Long id,
            Authentication authentication) {

        Cart cart;

        if (hasAuthority(authentication,
                "ADMIN", "SUPER_ADMIN", "FINANCE_ACCOUNTANT")) {
            cart = cartService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Cart not found"));
        } else {
            cart = cartService.findByIdAndUserName(
                            id, authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Cart not found"));
        }

        return toResponse(cart);
    }

    @GetMapping("/{id}/items")
    @Transactional(readOnly = true)
    public List<CartItemResponse> getItems(
            @PathVariable Long id,
            Authentication authentication) {

        return cartService.findItemsByCartIdAndUsername(
                        id, authentication.getName())
                .stream()
                .map(this::toItemResponse)
                .toList();
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse addItem(
            @Valid @RequestBody CartItemRequest request,
            Authentication authentication) {

        return toItemResponse(
                cartService.addItem(request, authentication.getName()));
    }

    @PutMapping("/items/{itemId}")
    public CartItemResponse updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemUpdateRequest request,
            Authentication authentication) {

        return toItemResponse(
                cartService.updateItem(
                        itemId, request, authentication.getName()));
    }

    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            @PathVariable Long itemId,
            Authentication authentication) {

        cartService.removeItem(itemId, authentication.getName());
    }

    private boolean hasAuthority(
            Authentication authentication,
            String... authorities) {

        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(granted -> Arrays.asList(authorities)
                        .contains(granted.getAuthority()));
    }

    private CartResponse toResponse(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getUser().getUserName(),
                cart.getStatus(),
                cart.getExpiresAt()
        );
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getCart().getId(),
                item.getResourceType(),
                item.getResourceId(),
                item.getQuantity(),
                item.getOptionsJson()
        );
    }
}
