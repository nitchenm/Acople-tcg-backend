package com.acople.tcg_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acople.tcg_backend.model.CartItem;
import com.acople.tcg_backend.model.Product;
import com.acople.tcg_backend.model.User;
import com.acople.tcg_backend.repository.CartItemRepository;
import com.acople.tcg_backend.repository.ProductRepository;
import com.acople.tcg_backend.repository.UserRepository;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public CartItem addToCart(Long userId, Long productId, int quantity) {
        // Check if item already exists in cart
        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (existingItemOpt.isPresent()) {
            // If it exists, update the quantity
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemRepository.save(existingItem);
        } else {
            // If not, create a new cart item
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
            
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            return cartItemRepository.save(newItem);
        }
    }

    public Optional<CartItem> updateCartItemQuantity(Long userId, Long productId, int quantity) {
        return cartItemRepository.findByUserIdAndProductId(userId, productId)
                .map(cartItem -> {
                    cartItem.setQuantity(quantity);
                    return cartItemRepository.save(cartItem);
                });
    }

    @Transactional
    public boolean removeItemFromCart(Long userId, Long productId) {
        return cartItemRepository.findByUserIdAndProductId(userId, productId).map(cartItem -> {
            cartItemRepository.delete(cartItem);
            return true;
        }).orElse(false);
    }

    @Transactional
    public void clearUserCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
