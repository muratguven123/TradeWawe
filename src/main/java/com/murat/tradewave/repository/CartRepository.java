package com.murat.tradewave.repository;

import com.murat.tradewave.dto.Cartİtem.ViewCartItems;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user.id = :userId AND c.checkedout = false")
    List<Cart> findActiveCartsByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.id = :cartId")
    Optional<Cart> findByIdWithItems(@Param("cartId") Long cartId);

    ViewCartItems findCartsByItems(List<CartItem> items);
    ViewCartItems findCartItemsById(Long id);
}
