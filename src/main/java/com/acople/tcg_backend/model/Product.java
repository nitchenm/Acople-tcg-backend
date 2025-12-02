package com.acople.tcg_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String slug;
    private String description;
    private double price;
    private int stock;
    private String category; // "single", "carpetas", "dados", etc.
    private String game; // "magic", "pokemon", "accesorio", etc.
}
