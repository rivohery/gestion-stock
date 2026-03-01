package com.alibou.stockmanage.stocks.models;

import com.alibou.stockmanage.products.models.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name="stock_movements")
@EntityListeners(AuditingEntityListener.class)
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeMovement type;
    @Column(nullable = false)
    private String reference;//N° de bon de livraison, de vente, etc.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy;


}
