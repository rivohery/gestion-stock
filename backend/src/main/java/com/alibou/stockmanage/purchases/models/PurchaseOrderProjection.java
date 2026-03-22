package com.alibou.stockmanage.purchases.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PurchaseOrderProjection {
    Long getId();
    String getInvoiceNo();
    PurchaseOrderStatus getStatus();
    LocalDate getCreatedDate();
    LocalDate getReceiveDate();
    BigDecimal getTotalAmounts();
    String getSupplierName();
    String getFirstName();
    String getLastName();
}
