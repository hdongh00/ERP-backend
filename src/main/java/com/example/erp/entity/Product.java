package com.example.erp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products") //테이블 명은 복수형으로 명시
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 막아서 안전하게
@ToString(of = {"id", "name", "price"}) //로그 찍을 때 무한루프 방지
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity;
    private Integer safetyStock;

    @Column(columnDefinition = "TEXT") //postgre에서 긴 글은 TEXT 타입 사용
    private String description;

    //낙관적 락을 위한 버전 관리
    @Version
    private Long version;

    //생성자 대신 빌더 패턴 사용(객체 생성 시 명확성 확보)
    @Builder
    public Product(String name, Integer price, Integer stockQuantity, Integer safetyStock, String description) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.safetyStock = safetyStock;
        this.description = description;
    }

    //비즈니스 로직(재고 증가)
    public void addStock(int amount){
        this.stockQuantity += amount;
    }

    //비즈니스 로직(재고 감소 - 예외 처리 포함)
    public void removeStock(int amount){
        int restStock = this.stockQuantity - amount;
        if(restStock < 0){
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stockQuantity -= restStock;
    }
}
