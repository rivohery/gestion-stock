package com.alibou.stockmanage.purchases.mappers;

import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.purchases.models.PurchaseOrder;
import com.alibou.stockmanage.purchases.models.PurchaseOrderItem;
import com.alibou.stockmanage.purchases.web.dtos.*;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderMapper {
    private final SupplierRepository supplierRepository;

    private final UserRepository userRepository;

    public PurchaseOrderItem mapToPurchaseOrderItem(
          @NonNull PurchaseOrderItemRequest request,
          @NonNull  PurchaseOrder purchaseOrder,
          @NonNull  Product product
    ){
        return PurchaseOrderItem.builder()
                .product(product)
                .purchaseOrder(purchaseOrder)
                .quantity(request.quantity())
                .totalItems(request.totalItems())
                .build();
    }

    public PurchaseOrder mapToEntity(PurchaseOrderRequest request){
        var purchaseOrder = new PurchaseOrder();
        BeanUtils.copyProperties(request, purchaseOrder);
        purchaseOrder.setSupplier(
                supplierRepository.findById(request.supplierId()).orElseThrow(
                        () -> new EntityNotFoundException(String.format("Supplier of ID %s is not found", request.supplierId()))
                )
        );
        return purchaseOrder;
    }

    public PurchaseOrderResponse mapToResponse(PurchaseOrder purchaseOrder){
        Optional<UserDetails> optional = userRepository.getEmployeeDetail(purchaseOrder.getCreatedBy());
        if(optional.isEmpty()){
            throw new OperationNotPermittedException("UserDetails object is null");
        }
        var purchaseOrderResponse = new PurchaseOrderResponse();
        BeanUtils.copyProperties(purchaseOrder, purchaseOrderResponse);
        purchaseOrderResponse.setSupplierId(purchaseOrder.getSupplier().getId());
        purchaseOrderResponse.setSupplierName(purchaseOrder.getSupplier().getName());
        purchaseOrderResponse.setEmployee(
                optional.get().getFullName()
        );
        purchaseOrderResponse.setItems(
                purchaseOrder.getPurchaseOrderItems()
                        .stream()
                        .map(this::mapToPurchaseOrderItemResponse)
                        .collect(Collectors.toSet())
        );
        return purchaseOrderResponse;
    }

    public PurchaseOrderMinResponse mapToPurchaseOrderMinResponse(PurchaseOrder purchaseOrder){
        var purchaseOrderMinResponse = new PurchaseOrderMinResponse();
        BeanUtils.copyProperties(purchaseOrder, purchaseOrderMinResponse);
        purchaseOrderMinResponse.setEmployee(
                userRepository.getEmployeeDetail(purchaseOrder.getCreatedBy()).get().getFullName()
        );
        purchaseOrderMinResponse.setSupplierName(
                purchaseOrder.getSupplier().getName()
        );
        return purchaseOrderMinResponse;
    }

    public PurchaseOrderItemResponse mapToPurchaseOrderItemResponse(PurchaseOrderItem purchaseOrderItem){
        return PurchaseOrderItemResponse.builder()
                .costPrice(purchaseOrderItem.getProduct().getCostPrice())
                .itemName(purchaseOrderItem.getProduct().getName())
                .quantity(purchaseOrderItem.getQuantity())
                .totalItems(purchaseOrderItem.getTotalItems())
                .build();
    }
}
