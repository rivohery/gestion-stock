package com.alibou.stockmanage.purchases.web.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseOrderItemResponse {
    private String itemName;
    private BigDecimal costPrice;
    private int quantity;
    private BigDecimal totalItems;

}
