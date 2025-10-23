package com.murat.tradewave.repository;

import com.murat.tradewave.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByEmail(String email);
    boolean existsByEmail(String email);
}
