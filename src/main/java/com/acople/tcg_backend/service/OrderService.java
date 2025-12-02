package com.acople.tcg_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acople.tcg_backend.model.CartItem;
import com.acople.tcg_backend.model.Order;
import com.acople.tcg_backend.model.OrderItem;
import com.acople.tcg_backend.model.Product;
import com.acople.tcg_backend.model.User;
import com.acople.tcg_backend.repository.CartItemRepository;
import com.acople.tcg_backend.repository.OrderRepository;
import com.acople.tcg_backend.repository.ProductRepository;
import com.acople.tcg_backend.repository.UserRepository;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;

    @Transactional
    public Order createOrderFromCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create an order from an empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found in cart processing."));

            // Check for stock before proceeding
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }
            // Decrease stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice()); // Capture price at time of purchase
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        
        double totalAmount = orderItems.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        
        // Clear the user's cart after the order is successfully created
        cartItemRepository.deleteAll(cartItems);
        
        return savedOrder;
    }

    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    // For Admin
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    // For Admin
    public Optional<Order> updateOrderStatus(Long orderId, String status) {
        return orderRepository.findById(orderId).map(order -> {
            order.setStatus(status);
            return orderRepository.save(order);
        });
    }
}
