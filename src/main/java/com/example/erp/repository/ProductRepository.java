package com.example.erp.repository;

import com.example.erp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //기본 기능(save, findAll, findById 등)은 JpaRepository가 이미 다 생성
}
