package com.murat.tradewave.repository;

import com.murat.tradewave.model.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartRepository extends CrudRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndCheckedOutFalse(Long userId);

}
