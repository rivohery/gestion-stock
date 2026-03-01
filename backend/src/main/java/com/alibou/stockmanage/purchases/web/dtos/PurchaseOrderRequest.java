package com.alibou.stockmanage.purchases.web.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Validated
public record PurchaseOrderRequest(
        @NotBlank(message = "Invoice Number is required")
        String invoiceNo,
        @NotNull
        Long supplierId,
        @NotNull(message = "Receive date is not null")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate receiveDate,
        @NotNull(message = "Total amount is not null")
        @Positive(message = "Total amount must be positive")
        BigDecimal totalAmounts,
        @NotNull(message = "Items is not null")
        @NotEmpty(message= "Items is not empty")
        @Valid
        Set<PurchaseOrderItemRequest>items
) {
}
