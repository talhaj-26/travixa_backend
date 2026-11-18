//package com.travixa.controllers;
//
//import com.travixa.dto.booking.CartItemRequest;
//import com.travixa.dto.booking.CartItemResponse;
//import com.travixa.entity.booking.Cart;
//import com.travixa.entity.booking.CartItem;
//import com.travixa.entity.identity_access.User;
//import com.travixa.enums.CartStatus;
//import com.travixa.repository.booking.CartItemRepository;
//import com.travixa.repository.booking.CartRepository;
//import com.travixa.repository.identity.UserRepository;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/carts/items")
//public class CartItemController {
//
//    private final CartRepository cartRepository;
//    private final CartItemRepository cartItemRepository;
//    private final UserRepository userRepository;
//
//    public CartItemController(
//            CartRepository cartRepository,
//            CartItemRepository cartItemRepository,
//            UserRepository userRepository) {
//        this.cartRepository = cartRepository;
//        this.cartItemRepository = cartItemRepository;
//        this.userRepository = userRepository;
//    }
//
//    @GetMapping
//    @Transactional(readOnly = true)
//    public List<CartItemResponse> getMyItems(Authentication authentication) {
//        return cartItemRepository
//                .findAllByCart_User_UserName(authentication.getName())
//                .stream()
//                .map(this::toResponse)
//                .toList();
//    }
//
//    @PostMapping
//    @Transactional
//    @ResponseStatus(HttpStatus.CREATED)
//    public CartItemResponse addItem(
//            @Valid @RequestBody CartItemRequest request,
//            Authentication authentication) {
//
//        Cart cart = getOrCreateActiveCart(authentication.getName());
//
//        var existing = cartItemRepository
//                .findByCart_IdAndResourceTypeAndResourceId(
//                        cart.getId(),
//                        request.resourceType(),
//                        request.resourceId());
//
//        CartItem item;
//
//        if (existing.isPresent()) {
//            item = existing.get();
//
//            int newQuantity = item.getQuantity() + request.quantity();
//
//            if (newQuantity > 100) {
//                throw new ResponseStatusException(
//                        HttpStatus.BAD_REQUEST,
//                        "Cart item quantity cannot exceed 100");
//            }
//
//            item.setQuantity(newQuantity);
//            item.setOptionsJson(request.optionsJson());
//        } else {
//            if (request.quantity() > 100) {
//                throw new ResponseStatusException(
//                        HttpStatus.BAD_REQUEST,
//                        "Cart item quantity cannot exceed 100");
//            }
//
//            item = new CartItem();
//            item.setCart(cart);
//            item.setResourceType(request.resourceType());
//            item.setResourceId(request.resourceId());
//            item.setQuantity(request.quantity());
//            item.setOptionsJson(request.optionsJson());
//        }
//
//        return toResponse(cartItemRepository.save(item));
//    }
//
//    @PutMapping("/{itemId}")
//    @Transactional
//    public CartItemResponse updateItem(
//            @PathVariable Long itemId,
//            @Valid @RequestBody CartItemRequest request,
//            Authentication authentication) {
//
//        CartItem item = cartItemRepository
//                .findByIdAndCart_User_UserName(
//                        itemId, authentication.getName())
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Cart item not found"));
//
//        if (item.getCart().getStatus() != CartStatus.ACTIVE) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "Cart is not active");
//        }
//
//        if (!item.getResourceType().equals(request.resourceType())
//                || !item.getResourceId().equals(request.resourceId())) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Resource type and resource ID cannot be changed");
//        }
//
//        if (request.quantity() > 100) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Cart item quantity cannot exceed 100");
//        }
//
//        item.setQuantity(request.quantity());
//        item.setOptionsJson(request.optionsJson());
//
//        return toResponse(cartItemRepository.save(item));
//    }
//
//    @DeleteMapping("/{itemId}")
//    @Transactional
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void removeItem(
//            @PathVariable Long itemId,
//            Authentication authentication) {
//
//        CartItem item = cartItemRepository
//                .findByIdAndCart_User_UserName(
//                        itemId, authentication.getName())
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Cart item not found"));
//
//        if (item.getCart().getStatus() != CartStatus.ACTIVE) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "Cart is not active");
//        }
//
//        cartItemRepository.delete(item);
//    }
//
//    private Cart getOrCreateActiveCart(String userName) {
//        Cart cart = cartRepository.findByUser_UserName(userName)
//                .orElseGet(() -> {
//                    User user = userRepository.findByUserName(userName)
//                            .orElseThrow(() -> new ResponseStatusException(
//                                    HttpStatus.NOT_FOUND, "User not found"));
//
//                    Cart newCart = new Cart();
//                    newCart.setUser(user);
//                    newCart.setStatus(CartStatus.ACTIVE);
//
//                    return cartRepository.save(newCart);
//                });
//
//        if (cart.getStatus() != CartStatus.ACTIVE) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "Cart is not active");
//        }
//
//        return cart;
//    }
//
//    private CartItemResponse toResponse(CartItem item) {
//        return new CartItemResponse(
//                item.getId(),
//                item.getCart().getId(),
//                item.getResourceType(),
//                item.getResourceId(),
//                item.getQuantity(),
//                item.getOptionsJson()
//        );
//    }
//}