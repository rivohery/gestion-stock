package com.alibou.stockmanage.products.web.dtos;

import com.alibou.stockmanage.products.models.Category;
import com.alibou.stockmanage.suppliers.models.Supplier;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private Long id;
    private String code;
    private String name;
    private BigDecimal salesPrice;// Prix de vente
    private BigDecimal costPrice;// Prix d'achat
    private int alertStock;
    private int qtyStock;
    private String unity;
    private Boolean isActive;
    private byte[] photos;
    private Long categoryId;
    private String categoryName;
    private Long supplierId;
    private String supplierName;

    public ProductResponse(Long id, String name, BigDecimal costPrice, int qtyStock){
       this.id = id;
       this.name = name;
       this.costPrice = costPrice;
       this.qtyStock = qtyStock;
    }

    public ProductResponse(Long id, String name, int qtyStock, BigDecimal salesPrice){
        this.id = id;
        this.name = name;
        this.qtyStock = qtyStock;
        this.salesPrice = salesPrice;
    }

}
