package com.murat.tradewave.repository;

import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

}
