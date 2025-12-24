package com.example.erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Approval extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requesterName; //기안자

    @ManyToOne(fetch = FetchType.LAZY) //결제 건 하나는 상품 하나와 연결
    @JoinColumn(name = "product_id") //DB에는 product_id로 저장
    private Product product;
    private Integer quantity; //요청 수량

    @Enumerated(EnumType.STRING) //DB에 숫자가 아니라 글자로 저장
    private ApprovalStatus status;

    @Builder
    public Approval(String requesterName, Product product, Integer quantity) {
        this.requesterName = requesterName;
        this.product = product;
        this.quantity = quantity;
        this.status = ApprovalStatus.WAIT; //처음에는 무조건 대기
    }

    //비즈니스 로직: 승인 처리
    public void approve(){
        this.status = ApprovalStatus.APPROVED;
    }

    //반려 처리
    public void reject(){
        this.status = ApprovalStatus.REJECTED;
    }
}
