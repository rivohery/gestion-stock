package com.alibou.stockmanage.stocks.mappers;

import com.alibou.stockmanage.stocks.models.StockMovementProjection;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import org.springframework.stereotype.Service;

@Service
public class StockMovementMapper {

    public StockMovementDto mapToDto(StockMovementProjection stockMovementProjection){
        return StockMovementDto.builder()
                .createdDate(stockMovementProjection.getStockMovement().getCreatedDate())
                .employeId(stockMovementProjection.getStockMovement().getCreatedBy())
                .employeName(stockMovementProjection.getUserDetails().getFullName())
                .productId(stockMovementProjection.getStockMovement().getProduct().getId())
                .productName(stockMovementProjection.getStockMovement().getProduct().getName())
                .id(stockMovementProjection.getStockMovement().getId())
                .quantity(stockMovementProjection.getStockMovement().getQuantity())
                .reference(stockMovementProjection.getStockMovement().getReference())
                .type(stockMovementProjection.getStockMovement().getType())
                .build();
    }
}
