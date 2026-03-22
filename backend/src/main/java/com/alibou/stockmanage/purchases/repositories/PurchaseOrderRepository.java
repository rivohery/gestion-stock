package com.alibou.stockmanage.purchases.repositories;

import com.alibou.stockmanage.purchases.models.PurchaseOrder;
import com.alibou.stockmanage.purchases.models.PurchaseOrderProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    boolean existsByInvoiceNo(String invoiceNo);

    @EntityGraph(attributePaths = {"supplier"})
    @Query("""
        Select po.id as id, 
               po.invoiceNo as invoiceNo, 
               po.status as status, 
               po.createdDate as createdDate,
               po.receiveDate as receiveDate,
               po.totalAmounts as totalAmounts,
               po.supplier.name as supplierName,
               ud.firstName as firstName,
               ud.lastName as lastName
        from PurchaseOrder po 
        JOIN UserDetails ud
        ON po.createdBy = ud.user.id       
    """)
    Page<PurchaseOrderProjection>fetchAllPurchaseOrder(Pageable pageable);
}

