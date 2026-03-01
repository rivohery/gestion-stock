package com.alibou.stockmanage.purchases.models;

import com.alibou.stockmanage.suppliers.models.Supplier;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="purchase_orders")
@EntityListeners(AuditingEntityListener.class)
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String invoiceNo;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;
    @Column(nullable = false)
    private LocalDate receiveDate;
    @Column(nullable = false)
    private BigDecimal totalAmounts;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @OneToMany(mappedBy = "purchaseOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<PurchaseOrderItem>purchaseOrderItems = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy;

}
