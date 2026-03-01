package com.alibou.stockmanage.sales.web.controller;

import com.alibou.stockmanage.products.web.dtos.CategoryDto;
import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import com.alibou.stockmanage.sales.service.BillOrderService;
import com.alibou.stockmanage.sales.web.dto.BillOrderMinResponse;
import com.alibou.stockmanage.sales.web.dto.BillOrderRequest;
import com.alibou.stockmanage.sales.web.dto.BillOrderResponse;
import com.alibou.stockmanage.shared.dtos.GlobalResponse;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/sales/bill-order")
@Tag(name="billOrder-endpoints")
@RequiredArgsConstructor
public class BillOrderController {

    private final BillOrderService billOrderService;

    @GetMapping("/check-unique-invoiceNo")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<Map<String,String>>checkUniqueInvoiceNo(){
        String invoiceNo = billOrderService.checkUniqueInvoiceNo();
        if(StringUtils.isNotBlank(invoiceNo)){
            return ResponseEntity.ok(Map.of("invoiceNo", invoiceNo));
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/get-category-list")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<List<CategoryDto>> findAllCategory() {
        return ResponseEntity.ok(billOrderService.findAllCategory());
    }

    @GetMapping("/get-all-product-by-category")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<List<ProductResponse>> findAllProductByCategoryId(
            @RequestParam(name = "categoryId", required = true) Long categoryId
    ) {
        return ResponseEntity.ok(billOrderService.findAllProductByCategoryId(categoryId));
    }

    @GetMapping("/{billOrderId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<BillOrderResponse> findBillOrderById(@PathVariable("billOrderId") Long billOrderId) {
        var billOrderResponse =  billOrderService.findBillOrderById(billOrderId);
        if(Objects.nonNull(billOrderResponse)){
            return ResponseEntity.ok(billOrderResponse);
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<PageResponse<BillOrderMinResponse>> findAllOrder(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return ResponseEntity.ok(billOrderService.findAllOrder(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<BillOrderResponse> createBillOrder(
           @Valid @RequestBody BillOrderRequest request)
    {
        var billOrderResponse = billOrderService.createBillOrder(request);
        if(Objects.nonNull(billOrderResponse)){
            return ResponseEntity.status(HttpStatus.CREATED).body(billOrderResponse);
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/get-invoice-pdf/{invoiceNu}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable("invoiceNu") String invoiceNu) {
        byte[]pdf = billOrderService.getInvoicePdf(invoiceNu);
        if(Objects.nonNull(pdf)){
            return ResponseEntity.ok(pdf);
        }
        return ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/{billOrderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse> deleteBillOrderById(@PathVariable("billOrderId") Long billOrderId){
        boolean deleted = billOrderService.deleteBillOrderById(billOrderId);
        if(deleted){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Order of ID %s was deleted successfully", billOrderId))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

}
