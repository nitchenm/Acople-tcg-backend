package com.acople.tcg_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acople.tcg_backend.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByUserId(Long userId);
}
