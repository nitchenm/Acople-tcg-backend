package com.acople.tcg_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.acople.tcg_backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    Optional<Product> findBySlug(String slug);

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();

}
