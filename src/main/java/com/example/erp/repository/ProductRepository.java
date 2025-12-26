package com.example.erp.repository;

import com.example.erp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    //기본 기능(save, findAll, findById 등)은 JpaRepository가 이미 다 생성
}
