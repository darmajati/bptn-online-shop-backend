package com.example.online_shop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.online_shop.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    @Query("SELECT c FROM Customer c WHERE c.isActive = true AND c.customerId = :customerId")
    Optional<Customer> findByCustomerId(@Param("customerId") Long customerId);
}
