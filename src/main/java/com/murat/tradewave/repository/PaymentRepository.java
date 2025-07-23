package com.murat.tradewave.repository;

import com.murat.tradewave.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p Where p.order.user.email= :email")
    List<Payment> findAllByUserEmail(@Param("email") String email);

}
