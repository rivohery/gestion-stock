package com.alibou.stockmanage.stocks.web.dtos;

import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.stocks.models.TypeMovement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockMovementDto {
    private Long id;;

    private int quantity;

    private TypeMovement type;

    private String reference;//N° de bon de livraison, de vente, etc.

    private Long productId;

    private String productName;

    private LocalDate createdDate;

    private Long employeId;

    private String employeName;
}
