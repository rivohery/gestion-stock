package com.alibou.stockmanage.products.web.dtos;

import com.alibou.stockmanage.products.web.dtos.AlertEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockResponse {
    private Long id;//productId
    private String name;
    private AlertEnum alert;
    private int qtyStock;
    private Boolean isActive;
    private byte[] photos;
}
