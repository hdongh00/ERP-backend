package com.example.erp.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;

@JsonClassDescription("발주 취소 요청 데이터")
public record OrderCancelRequest(
        String productName,
        int quantity
) {}
