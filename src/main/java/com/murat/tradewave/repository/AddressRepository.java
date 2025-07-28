package com.murat.tradewave.repository;

import com.murat.tradewave.model.Address;
import com.murat.tradewave.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Query("SELECT a FROM Address a Where a.name= :name")
    List<Address> findAllByUserAdressName(@Param("name") String name);
}
