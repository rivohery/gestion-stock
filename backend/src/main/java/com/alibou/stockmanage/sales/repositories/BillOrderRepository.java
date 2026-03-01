package com.alibou.stockmanage.sales.repositories;

import com.alibou.stockmanage.sales.models.BillOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillOrderRepository extends JpaRepository<BillOrder, Long> {
    Optional<BillOrder>findByInvoiceNo(String invoiceNo);
    boolean existsByInvoiceNo(String invoiceNo);
}
