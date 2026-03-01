package com.alibou.stockmanage.purchases.web.controllers;

import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import com.alibou.stockmanage.purchases.services.PurchaseOrderService;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderMinResponse;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderRequest;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderResponse;
import com.alibou.stockmanage.purchases.web.dtos.UpdateStatusPurchaseOrderRequest;
import com.alibou.stockmanage.shared.dtos.GlobalResponse;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/stock/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping("/check-invoice-no")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STOCK_MANAGER')")
    public ResponseEntity<Map<String, String>> generateUniqueInvoiceNo() {
        var invoiceNo = purchaseOrderService.generateUniqueInvoiceNo();
        if(StringUtils.isNotBlank(invoiceNo)){
            return ResponseEntity.ok(Map.of("invoiceNo", invoiceNo));
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STOCK_MANAGER')")
    public ResponseEntity<PageResponse<PurchaseOrderMinResponse>>findAllPurchaseOrder(
          @RequestParam(value = "page", defaultValue = "0")int page,
          @RequestParam(value = "size", defaultValue = "6")int size
    ){
        PageResponse<PurchaseOrderMinResponse> pageResponses =
                purchaseOrderService.findAll(PageRequest.of(page, size , Sort.by("createdDate").descending()));
        if(Objects.nonNull(pageResponses)){
            return ResponseEntity.ok(pageResponses);
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STOCK_MANAGER')")
    public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request
    ) {
        var purchaseOrderResponse = purchaseOrderService.createPurchaseOrder(request);
        if (Objects.nonNull(purchaseOrderResponse)) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(purchaseOrderResponse);
        }
        return ResponseEntity.internalServerError().build();
    }

    @PatchMapping("/update-status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STOCK_MANAGER')")
    public ResponseEntity<GlobalResponse> updateStatusOfPurchaseOrder(
            @Valid @RequestBody UpdateStatusPurchaseOrderRequest request
    ) {
        var purchaseOrder = purchaseOrderService.updateStatusOfPurchaseOrder(request);
        if (Objects.nonNull(purchaseOrder)) {
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Status of purchase order with ID %s was updated to %s", request.purchaseOrderId() ,request.status()))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/{purchaseOrderId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STOCK_MANAGER')")
    public ResponseEntity<PurchaseOrderResponse> findPurchaseOrderById(@PathVariable("purchaseOrderId") Long purchaseOrderId) {
        var purchaseOrdreResponse = purchaseOrderService.findPurchaseOrderById(purchaseOrderId);
        if (Objects.nonNull(purchaseOrdreResponse)) {
            return ResponseEntity.ok(purchaseOrdreResponse);
        }
        return ResponseEntity.internalServerError().build();
    }


    @DeleteMapping("/{purchaseOrderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>deletePurchaseOrderById(
            @PathVariable("purchaseOrderId") Long purchaseOrderId
    ){
        var deleted = purchaseOrderService.deletePurchaseOrderById(purchaseOrderId);
        if(deleted){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Purchase order with ID %s was deleted successfully", purchaseOrderId))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/get-supplier-list")
    public ResponseEntity<List<SupplierDto>>getSupplierList(){
        return ResponseEntity.ok(purchaseOrderService.getSupplierList());
    }

    @GetMapping("/get-product-by-supplierId")
    public ResponseEntity<List<ProductResponse>>findProductBySupplierId(
          @RequestParam(name = "supplierId", required = true)  Long supplierId
    ){
        return ResponseEntity.ok(purchaseOrderService.fetchProductsBySupplierId(supplierId));
    }

}
