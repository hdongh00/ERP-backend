package com.example.erp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 100, message = "가격은 최소 100원 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "재고 수량은 필수입니다.")
    @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
    private Integer stockQuantity;

    @NotNull(message = "안전 재고는 필수입니다.")
    @Min(value = 0, message = "안전 재고는 0개 이상이어야 합니다.")
    private Integer safetyStock;

    private String description;
}
