package com.example.erp.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;

@JsonClassDescription("재고 및 상태를 조회하고 싶은 품목의 이름")
public record ProductSearchRequest(
        String productName
) {}
