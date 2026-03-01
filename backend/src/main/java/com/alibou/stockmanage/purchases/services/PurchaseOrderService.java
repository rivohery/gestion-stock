package com.alibou.stockmanage.purchases.services;

import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import com.alibou.stockmanage.purchases.models.PurchaseOrder;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderMinResponse;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderRequest;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderResponse;
import com.alibou.stockmanage.purchases.web.dtos.UpdateStatusPurchaseOrderRequest;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PurchaseOrderService {
    String generateUniqueInvoiceNo();

    PageResponse<PurchaseOrderMinResponse>findAll(Pageable pageable);
    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request);
    PurchaseOrder updateStatusOfPurchaseOrder(UpdateStatusPurchaseOrderRequest request);
    PurchaseOrderResponse findPurchaseOrderById(Long purchaseOrderId);
    boolean deletePurchaseOrderById(Long purchaseOrderId);
    List<SupplierDto>getSupplierList();
    List<ProductResponse>fetchProductsBySupplierId(Long supplierId);
}
