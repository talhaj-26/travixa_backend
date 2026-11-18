
package com.travixa.repository.booking;

import com.travixa.entity.booking.CartItem;
import com.travixa.enums.BookingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByCart_Id(Long cartId);

    List<CartItem> findAllByCart_User_UserName(String userName);

    Optional<CartItem> findByIdAndCart_User_UserName(
            Long id,
            String userName
    );

    Optional<CartItem> findByCart_IdAndResourceTypeAndResourceId(
            Long cartId,
            BookingType resourceType,
            Long resourceId
    );

    void deleteAllByCart_Id(Long cartId);

    boolean existsByCart_Id(Long cartId);
}
