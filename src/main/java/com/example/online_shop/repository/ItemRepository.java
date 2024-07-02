package com.example.online_shop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.online_shop.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
    @Query("SELECT i FROM Item i WHERE i.isAvailable = true AND i.itemId = :itemId")
    Optional<Item> findByItemId(@Param("itemId") Long itemId);
}
