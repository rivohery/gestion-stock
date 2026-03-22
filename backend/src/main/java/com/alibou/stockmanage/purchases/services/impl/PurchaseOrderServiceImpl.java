package com.alibou.stockmanage.purchases.services.impl;

import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import com.alibou.stockmanage.purchases.mappers.PurchaseOrderMapper;
import com.alibou.stockmanage.purchases.models.PurchaseOrder;
import com.alibou.stockmanage.purchases.models.PurchaseOrderItem;
import com.alibou.stockmanage.purchases.models.PurchaseOrderProjection;
import com.alibou.stockmanage.purchases.models.PurchaseOrderStatus;
import com.alibou.stockmanage.purchases.repositories.PurchaseOrderItemRepository;
import com.alibou.stockmanage.purchases.repositories.PurchaseOrderRepository;
import com.alibou.stockmanage.purchases.services.PurchaseOrderService;
import com.alibou.stockmanage.purchases.web.dtos.*;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import com.alibou.stockmanage.shared.utils.InvoiceNoGenerator;
import com.alibou.stockmanage.stocks.models.StockMovement;
import com.alibou.stockmanage.stocks.models.TypeMovement;
import com.alibou.stockmanage.stocks.repositories.StockMovementRepository;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final int INVOICE_NO_LENGTH = 6;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public String generateUniqueInvoiceNo() {
        boolean isUniqueInvoiceNo = true;
        String invoiceNo = null;
        while (isUniqueInvoiceNo) {
            invoiceNo = InvoiceNoGenerator.generateInvoiceNo(INVOICE_NO_LENGTH);
            isUniqueInvoiceNo = purchaseOrderRepository.existsByInvoiceNo(invoiceNo);
        }
        return invoiceNo;
    }

    @Override
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        if (request.items() == null || request.items().size() == 0) {
            throw new OperationNotPermittedException("Your purchase order items is empty or null");
        }
        PurchaseOrder purchaseOrder = purchaseOrderMapper.mapToEntity(request);
        purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        for (PurchaseOrderItemRequest item : request.items()) {
            var product = productRepository.findById(item.productId()).orElseThrow(
                    () -> new EntityNotFoundException(String.format("Product of ID %s is not found", item.productId()))
            );
            var purchaseOrderItem = purchaseOrderMapper.mapToPurchaseOrderItem(item, purchaseOrder, product);
            purchaseOrderItem = purchaseOrderItemRepository.save(purchaseOrderItem);
            purchaseOrder.getPurchaseOrderItems().add(purchaseOrderItem);
        }
        return purchaseOrderMapper.mapToResponse(purchaseOrder);
    }

    @Override
    public PurchaseOrder updateStatusOfPurchaseOrder(UpdateStatusPurchaseOrderRequest request) {
        var purchaseOrder = purchaseOrderRepository.findById(request.purchaseOrderId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("No Purchase order found with ID: %s", request.purchaseOrderId()))
        );
        switch (request.status()) {
            case PENDING:
                break;
            case CANCELLED:
                if (purchaseOrder.getStatus() != PurchaseOrderStatus.PENDING) {
                    throw new OperationNotPermittedException("You can cancel the order only if it is PENDING");
                }
                purchaseOrder.setStatus(PurchaseOrderStatus.CANCELLED);
                purchaseOrderRepository.save(purchaseOrder);
                break;
            case CONFIRMED:
                if (purchaseOrder.getStatus() != PurchaseOrderStatus.PENDING) {
                    throw new OperationNotPermittedException("Status of order is already PAYED | DELIVERED | CANCELED");
                }
                purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
                purchaseOrderRepository.save(purchaseOrder);

                for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
                    //Update qty stock only PurchaseOrderStatus === PurchaseOrderStatus.CONFIRMED
                    var product = productRepository.findById(item.getProduct().getId()).orElseThrow(
                            () -> new EntityNotFoundException(String.format("Product of ID %s is not found", item.getProduct().getId()))
                    );
                    product.setQtyStock(product.getQtyStock() + item.getQuantity());
                    product = productRepository.save(product);

                    //Save stock movement only PurchaseOrderStatus === PurchaseOrderStatus.CONFIRMED
                    saveMovementStock(product, item.getQuantity(), purchaseOrder.getInvoiceNo());
                }
                break;
            default://DELIVERED
                if (purchaseOrder.getStatus() == PurchaseOrderStatus.PENDING
                        || purchaseOrder.getStatus() == PurchaseOrderStatus.CANCELLED
                ) {
                    throw new OperationNotPermittedException("The status of order is PENDING or CANCELED");
                }
                purchaseOrder.setStatus(request.status());
                purchaseOrderRepository.save(purchaseOrder);
                break;
        }
        return purchaseOrder;
    }

    private void saveMovementStock(Product product, int quantity, String invoiceNo) {
        var stockMovement = StockMovement.builder()
                .product(product)
                .quantity(quantity)
                .reference(invoiceNo)
                .type(TypeMovement.IN)
                .build();
        stockMovementRepository.save(stockMovement);
    }

    @Override
    public PurchaseOrderResponse findPurchaseOrderById(Long purchaseOrderId) {
        var purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No Purchase order found with ID: %s", purchaseOrderId))
        );
        return purchaseOrderMapper.mapToResponse(purchaseOrder);
    }

    @Override
    public boolean deletePurchaseOrderById(Long purchaseOrderId) {
        var purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No Purchase order found with ID: %s", purchaseOrderId))
        );
        purchaseOrderRepository.delete(purchaseOrder);
        return true;
    }

    @Override
    public PageResponse<PurchaseOrderMinResponse> findAll(Pageable pageable) {
        Page<PurchaseOrderProjection> pages = purchaseOrderRepository.fetchAllPurchaseOrder(pageable);
        List<PurchaseOrderMinResponse> content = pages
                .stream()
                .map(purchaseOrderMapper::mapToPurchaseOrderMinResponse)
                .toList();
        return new PageResponse(
                content,
                pages.getNumber(),
                pages.getSize(),
                pages.getTotalElements(),
                pages.getTotalPages(),
                pages.isFirst(),
                pages.isLast()
        );
    }

    @Override
    public List<SupplierDto> getSupplierList() {
        return supplierRepository.getSupplierList();
    }

    @Override
    public List<ProductResponse> fetchProductsBySupplierId(Long supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }
}
