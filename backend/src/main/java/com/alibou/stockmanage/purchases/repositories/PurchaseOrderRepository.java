package com.alibou.stockmanage.purchases.repositories;

import com.alibou.stockmanage.purchases.models.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    boolean existsByInvoiceNo(String invoiceNo);
}
