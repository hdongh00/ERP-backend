package com.example.erp.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("전체 상품 목록을 조회하는 요청 (조건 없음)")
public record ProductListRequest(
        @JsonPropertyDescription("요청 이유: (예: 목록 조회)")
        String reason
) {}
