package com.alibou.stockmanage.sales.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillOrderItemResponse {
    private String itemName;
    private int quantity;
    private BigDecimal salesPrice;
    private BigDecimal totalItems;
}
