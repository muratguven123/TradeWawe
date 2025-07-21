package com.murat.tradewave.repository;

import com.murat.tradewave.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionRepository extends JpaRepository<Product, Long> {
}
