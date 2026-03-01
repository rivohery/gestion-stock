package com.alibou.stockmanage.purchases.web.dtos;

import com.alibou.stockmanage.purchases.models.PurchaseOrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusPurchaseOrderRequest(
        @NotNull
        Long purchaseOrderId,
        @NotNull
        PurchaseOrderStatus status
) {
}
