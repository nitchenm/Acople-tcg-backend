package com.acople.tcg_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acople.tcg_backend.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

}
