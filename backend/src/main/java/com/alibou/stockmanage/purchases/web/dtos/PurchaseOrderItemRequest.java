package com.alibou.stockmanage.purchases.web.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PurchaseOrderItemRequest(
        @NotNull(message = "Quantity of Items is not null")
        @Positive(message = "Quantity of Items must positive")
        int quantity,
        @NotNull(message = "Product of Items is not null")
        Long productId,
        String productName,
        @NotNull(message = "Total of Items is not null")
        @Positive(message = "Total of Items must positive")
        BigDecimal totalItems
) {
}
