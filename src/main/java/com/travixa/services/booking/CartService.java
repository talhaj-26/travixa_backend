
package com.travixa.services.booking;

import com.travixa.dto.booking.CartItemRequest;
import com.travixa.dto.booking.CartItemUpdateRequest;
import com.travixa.entity.booking.Cart;
import com.travixa.entity.booking.CartItem;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.CartStatus;
import com.travixa.repository.booking.CartItemRepository;
import com.travixa.repository.booking.CartRepository;
import com.travixa.repository.identity.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    public Optional<Cart> findById(Long id) {
        return cartRepository.findById(id);
    }

    public Optional<Cart> findByUserName(String username) {
        return cartRepository.findByUser_UserName(username);
    }

    public List<Cart> findAllByUserName(String username) {
        return cartRepository.findAllByUser_UserName(username);
    }

    public Optional<Cart> findByIdAndUserName(
            Long id,
            String username
    ) {
        return cartRepository.findByIdAndUser_UserName(id, username);
    }

    public List<CartItem> findItemsByCartIdAndUsername(
            Long cartId,
            String username
    ) {
        Cart cart = cartRepository.findByIdAndUser_UserName(
                cartId, username
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Cart not found"
        ));

        return cartItemRepository.findAllByCart_Id(cart.getId());
    }

    @Transactional
    public CartItem addItem(
            CartItemRequest request,
            String username
    ) {
        validateQuantity(request.quantity());

        Cart cart = getOrCreateActiveCart(username);

        Optional<CartItem> existing = cartItemRepository
                .findByCart_IdAndResourceTypeAndResourceId(
                        cart.getId(),
                        request.resourceType(),
                        request.resourceId()
                );

        if (existing.isPresent()) {
            CartItem item = existing.get();

            int newQuantity = item.getQuantity() + request.quantity();
            validateQuantity(newQuantity);

            item.setQuantity(newQuantity);
            item.setOptionsJson(request.optionsJson());

            return cartItemRepository.save(item);
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setResourceType(request.resourceType());
        item.setResourceId(request.resourceId());
        item.setQuantity(request.quantity());
        item.setOptionsJson(request.optionsJson());

        return cartItemRepository.save(item);
    }

    @Transactional
    public CartItem updateItem(
            Long itemId,
            CartItemUpdateRequest request,
            String username
    ) {
        validateQuantity(request.quantity());

        CartItem item = cartItemRepository
                .findByIdAndCart_User_UserName(itemId, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cart item not found"
                ));

        ensureActive(item.getCart());

        item.setQuantity(request.quantity());
        item.setOptionsJson(request.optionsJson());

        return cartItemRepository.save(item);
    }

    @Transactional
    public void removeItem(Long itemId, String username) {
        CartItem item = cartItemRepository
                .findByIdAndCart_User_UserName(itemId, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cart item not found"
                ));

        ensureActive(item.getCart());
        cartItemRepository.delete(item);
    }

    @Transactional
    public Cart getOrCreateActiveCart(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));

        Optional<Cart> existing =
                cartRepository.findByUser_UserName(username);

        if (existing.isEmpty()) {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setStatus(CartStatus.ACTIVE);

            return cartRepository.save(cart);
        }

        Cart cart = existing.get();

        if (cart.getStatus() != CartStatus.ACTIVE) {
            // The database permits only one cart per user.
            // Reuse that cart for the next shopping session.
            cartItemRepository.deleteAllByCart_Id(cart.getId());
            cart.setStatus(CartStatus.ACTIVE);
            cart.setExpiresAt(null);
            cart = cartRepository.save(cart);
        }

        return cart;
    }

    @Transactional
    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    @Transactional
    public void deleteById(Long id) {
        cartRepository.deleteById(id);
    }

    private void ensureActive(Cart cart) {
        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cart is not active"
            );
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1 || quantity > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be between 1 and 100"
            );
        }
    }
}
