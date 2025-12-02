package com.acople.tcg_backend.controller;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long userId;
    private Long productId;
    private int quantity;
}
