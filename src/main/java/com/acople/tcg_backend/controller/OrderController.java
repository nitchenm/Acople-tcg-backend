package com.acople.tcg_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acople.tcg_backend.model.Order;
import com.acople.tcg_backend.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    
    // Create order from cart
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Order order = orderService.createOrderFromCart(userId);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            // e.g., cart is empty or stock is insufficient
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Get user's own orders
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(@RequestParam Long userId) {
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }

    // Get all orders (for admin)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update order status (for admin)
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return orderService.updateOrderStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
