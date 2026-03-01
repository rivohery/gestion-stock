package com.alibou.stockmanage.sales.mappers;

import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.sales.models.BillOrder;
import com.alibou.stockmanage.sales.models.BillOrderItem;
import com.alibou.stockmanage.sales.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BillOrderMapper {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    public BillOrderResponse mapToBillOrderResponse(BillOrder billOrder){
        UserDetails userDetails = userRepository.getEmployeeDetail(billOrder.getCreatedBy()).orElseThrow(
                () -> new EntityNotFoundException(String.format("No entity UserDetails found with user of Id: %s", billOrder.getCreatedBy()))
        );
        BillOrderResponse billOrderResponse = new BillOrderResponse();
        BeanUtils.copyProperties(billOrder, billOrderResponse);
        billOrderResponse.setEmployee(userDetails.getFullName());
        billOrderResponse.setItems(
                billOrder.getItems()
                        .stream()
                        .map(this::mapToBillOrderItemResponse)
                        .collect(Collectors.toSet())
        );
        return billOrderResponse;
    }

    public BillOrderItemResponse mapToBillOrderItemResponse(BillOrderItem item){
        return BillOrderItemResponse.builder()
                .itemName(item.getProduct().getName())
                .salesPrice(item.getProduct().getSalesPrice())
                .quantity(item.getQuantity())
                .totalItems(item.getTotalItems())
                .build();
    }

    public BillOrderMinResponse mapToBillOrderMinResponse(BillOrder billOrder){
        var billOrderMinResponse = new BillOrderMinResponse();
        BeanUtils.copyProperties(billOrder, billOrderMinResponse);
        return billOrderMinResponse;
    }

    public BillOrderItem mapToBillOrderItem(BillOrderItemRequest request, BillOrder billOrder){
        var product = productRepository.findById(request.productId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("No entity Product found with ID: %s", request.productId()))
        );
        return BillOrderItem.builder()
                .billOrder(billOrder)
                .product(product)
                .quantity(request.quantity())
                .totalItems(request.totalItems())
                .build();
    }

    public BillOrder mapToBillOrder(BillOrderRequest billOrderRequest){
        var billOrder = new BillOrder();
        BeanUtils.copyProperties(billOrderRequest, billOrder);
        return billOrder;
    }
}
