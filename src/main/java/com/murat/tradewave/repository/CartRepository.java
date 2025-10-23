package com.murat.tradewave.repository;

import com.murat.tradewave.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.checkedout = false")
    Optional<Cart> findCartByUserId(@Param("userId") Long userId);

}
