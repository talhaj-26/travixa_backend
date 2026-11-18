package com.travixa.repository.booking;

import com.travixa.entity.booking.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser_UserName(String userName);

    List<Cart> findAllByUser_UserName(String userName);

    Optional<Cart> findByIdAndUser_UserName(Long id, String userName);

    boolean existsByUser_UserName(String userName);
}