package com.alibou.stockmanage.products.web.dtos;

import jakarta.validation.constraints.NotNull;

public record UpdateProductStatusRequest(
        @NotNull(message="Product ID is not null")
        Long productId,
        @NotNull
        boolean status
) {
}
