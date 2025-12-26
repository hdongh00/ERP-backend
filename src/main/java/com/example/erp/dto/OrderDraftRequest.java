package com.example.erp.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;

@JsonClassDescription("발주를 위한 결재 기안을 요청하는 데이터(즉시 발주 아님)")
public record OrderDraftRequest(
        String productName,
        int quantity
) {}
