package com.acople.tcg_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acople.tcg_backend.model.CartItem;
import com.acople.tcg_backend.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    // GET /api/cart?userId=1
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestParam Long userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    // POST /api/cart/items
    @PostMapping("/items")
    public ResponseEntity<CartItem> addToCart(@RequestBody CartItemRequest request) {
        try {
            CartItem cartItem = cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/cart/items/{productId}?userId=1
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long productId, @RequestParam Long userId, @RequestBody UpdateQuantityRequest request) {
        return cartService.updateCartItemQuantity(userId, productId, request.getQuantity())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/cart/items/{productId}?userId=1
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long productId, @RequestParam Long userId) {
        boolean removed = cartService.removeItemFromCart(userId, productId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // DELETE /api/cart?userId=1
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        cartService.clearUserCart(userId);
        return ResponseEntity.noContent().build();
    }
}
