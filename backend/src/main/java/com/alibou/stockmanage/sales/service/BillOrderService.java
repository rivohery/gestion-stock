package com.alibou.stockmanage.sales.service;

import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.web.dtos.CategoryDto;
import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import com.alibou.stockmanage.sales.web.dto.BillOrderMinResponse;
import com.alibou.stockmanage.sales.web.dto.BillOrderRequest;
import com.alibou.stockmanage.sales.web.dto.BillOrderResponse;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillOrderService {
    List<CategoryDto>findAllCategory();
    String checkUniqueInvoiceNo();
    List<ProductResponse>findAllProductByCategoryId(Long categoryId);

    BillOrderResponse findBillOrderById(Long id);
    PageResponse<BillOrderMinResponse> findAllOrder(Pageable pageable);
    BillOrderResponse createBillOrder(BillOrderRequest request);
    byte[] getInvoicePdf(String invoiceNu);

    boolean deleteBillOrderById(Long billOrderId);
}
