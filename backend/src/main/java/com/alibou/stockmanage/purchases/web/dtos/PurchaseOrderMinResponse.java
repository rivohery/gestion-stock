package com.alibou.stockmanage.purchases.web.dtos;

import com.alibou.stockmanage.purchases.models.PurchaseOrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseOrderMinResponse {
    private Long id;
    private String invoiceNo;
    private PurchaseOrderStatus status;
    private LocalDate createdDate;
    private LocalDate receiveDate;
    private BigDecimal totalAmounts;
    private String supplierName;
    private String employee;
}
