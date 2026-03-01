package com.alibou.stockmanage.sales.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BillOrderItemRequest(
    @NotNull(message="ProductId not null")
    Long productId,
    String productName,
    @NotNull(message = "Quantity is not null")
    @Positive(message= "Quantity must positive")
    int quantity,
    @NotNull(message = "Total items is not null")
    @Positive(message= "Total items must positive")
    BigDecimal totalItems
) {
}
