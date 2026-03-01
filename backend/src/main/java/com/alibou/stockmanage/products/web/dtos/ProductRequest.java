package com.alibou.stockmanage.products.web.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public record ProductRequest(
        @Nullable
        Long id,
        @NotBlank(message="Product code is mandatory")
        String code,
        @NotBlank(message="Product name is mandatory")
        String name,
        @NotNull(message="Sales price is mandatory")
        BigDecimal salesPrice,
        @NotNull(message="Cost price is mandatory")
        BigDecimal costPrice,
        @NotNull(message="Alert stock is mandatory")
        int alertStock,
        @NotBlank(message="Product unity is mandatory")
        String unity,
        @NotNull(message="Category of product is mandatory")
        Long categoryId,
        @NotNull(message="Category of supplier is mandatory")
        Long supplierId
) {
}
