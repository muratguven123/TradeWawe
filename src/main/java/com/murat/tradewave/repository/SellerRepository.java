package com.murat.tradewave.repository;

import com.murat.tradewave.model.Seller;
import com.murat.tradewave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
