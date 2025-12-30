package com.example.erp.repository;

import com.example.erp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity < p.safetyStock")
    long countLowStock();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < p.safetyStock ORDER BY p.id ASC")
    List<Product> findLowStockProducts();

    //기본 기능(save, findAll, findById 등)은 JpaRepository가 이미 다 생성
}
