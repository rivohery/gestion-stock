package com.alibou.stockmanage.sales.models;

import com.alibou.stockmanage.products.models.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="bill_order_items")
public class BillOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private BigDecimal totalItems;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="bill_order_id")
    private BillOrder billOrder;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;
}
