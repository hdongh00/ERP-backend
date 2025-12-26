package com.example.erp.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;

//AI가 이 설명을 읽고 데이터 채워줌
@JsonClassDescription("발주 요청 데이터")
public record OrderRequest(
        String productName,
        int quantity
) {}
