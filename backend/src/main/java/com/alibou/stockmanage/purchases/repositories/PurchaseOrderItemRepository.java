package com.alibou.stockmanage.purchases.repositories;

import com.alibou.stockmanage.purchases.models.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
}
