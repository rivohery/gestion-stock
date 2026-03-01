package com.alibou.stockmanage.products.models;

import com.alibou.stockmanage.suppliers.models.Supplier;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal salesPrice;// Prix de vente
    @Column(nullable = false)
    private BigDecimal costPrice;// Prix d'achat
    @Column(nullable = false)
    private int alertStock;
    @Column(nullable = false)
    private int qtyStock;
    @Column(nullable = false)
    private String unity;
    private Boolean isActive;
    private String photoPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="categoryId")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="supplierId")
    private Supplier supplier;

}
