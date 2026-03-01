package com.alibou.stockmanage.sales.repositories;

import com.alibou.stockmanage.sales.models.BillOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillOrderItemRepository extends JpaRepository<BillOrderItem, Long> {
}
