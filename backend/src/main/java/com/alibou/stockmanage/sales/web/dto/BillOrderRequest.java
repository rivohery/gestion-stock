package com.alibou.stockmanage.sales.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Set;

public record BillOrderRequest(
        @NotBlank(message="InvoiceNo is required")
        String invoiceNo,
        @NotBlank(message="Customer is required")
        String customer,
        String phoneNu,
        String email,
        @NotBlank(message="Payment method is required")
        String paymentMethod,
        @NotNull(message="Total order is required")
        @Positive
        BigDecimal total,
        @NotNull(message="Items is not null")
        @NotEmpty(message= "Items is not empty")
        @Valid
        Set<BillOrderItemRequest> items
) { }
