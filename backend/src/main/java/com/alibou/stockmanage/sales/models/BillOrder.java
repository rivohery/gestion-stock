package com.alibou.stockmanage.sales.models;

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
@Table(name="bill_orders")
@EntityListeners(AuditingEntityListener.class)
public class BillOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String invoiceNo;
    @Column(nullable = false)
    private String customer;
    private String phoneNu;
    private String email;
    @Column(nullable = false)
    private String paymentMethod;
    @Column(nullable = false)
    private BigDecimal total;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @OneToMany(mappedBy = "billOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<BillOrderItem>items = new HashSet<>();
    
    
}
