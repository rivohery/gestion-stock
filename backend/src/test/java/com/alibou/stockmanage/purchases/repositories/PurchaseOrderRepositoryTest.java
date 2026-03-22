package com.alibou.stockmanage.purchases.repositories;


import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.UserDetailsRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.purchases.models.PurchaseOrder;
import com.alibou.stockmanage.purchases.models.PurchaseOrderProjection;
import com.alibou.stockmanage.purchases.models.PurchaseOrderStatus;
import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@ActiveProfiles("test")
public class PurchaseOrderRepositoryTest {
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    @BeforeEach
    void setUp(){
        userDetailsRepository.deleteAll();
        userRepository.deleteAll();
        supplierRepository.deleteAll();
        purchaseOrderRepository.deleteAll();

        User user = User.builder()
                .email("user@gmail.comm")
                .password("1234")
                .enabled(true)
                .roles(new HashSet<>())
                .build();

        UserDetails userDetails = UserDetails
                .builder()
                .phoneNu("12345")
                .lastName("John")
                .firstName("Doe")
                .user(user)
                .profileImage(null)
                .profilImageUrl(null)
                .build();
        user.setUserDetails(userDetails);
        user = userRepository.save(user);

        Supplier tom = Supplier.builder()
                .name("Tom S")
                .phoneNu("1234567")
                .email("tomm@gmail.com")
                .createdAt(LocalDate.now())
                .address("Tana")
                .build();
        tom = supplierRepository.save(tom);

        Supplier betty = Supplier.builder()
                .name("Betty S")
                .phoneNu("1234567")
                .email("betty@gmail.com")
                .createdAt(LocalDate.now())
                .address("Tana")
                .build();
        betty = supplierRepository.save(betty);

        PurchaseOrder purchaseOrder1 = PurchaseOrder
                .builder()
                .invoiceNo("123456")
                .receiveDate(LocalDate.now())
                .status(PurchaseOrderStatus.PENDING)
                .supplier(tom)
                .totalAmounts(BigDecimal.valueOf(100.00))
                .createdBy(user.getId())
                .createdDate(LocalDate.now())
                .purchaseOrderItems(new HashSet<>())
                .build();

        PurchaseOrder purchaseOrder2 = PurchaseOrder
                .builder()
                .invoiceNo("432567")
                .receiveDate(LocalDate.now())
                .status(PurchaseOrderStatus.CONFIRMED)
                .supplier(betty)
                .totalAmounts(BigDecimal.valueOf(400.00))
                .createdBy(user.getId())
                .createdDate(LocalDate.now())
                .purchaseOrderItems(new HashSet<>())
                .build();
        purchaseOrderRepository.saveAll(Arrays.asList(purchaseOrder1, purchaseOrder2));
    }

    @Test
    void first(){
        assertThat(purchaseOrderRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    void fetchAllPurchaseOrder_shouldPurchaseOrderProjection(){
        //Given
        Pageable pageable = PageRequest.of(0,10);

        //When
        Page<PurchaseOrderProjection>result = purchaseOrderRepository.fetchAllPurchaseOrder(pageable);

        //Then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getContent()).extracting(pu -> pu.getInvoiceNo()).containsExactly("123456", "432567");
        assertThat(result.getContent()).extracting(pu -> pu.getSupplierName()).containsExactly("Tom S", "Betty S");
        assertThat(result.getContent()).extracting(pu -> pu.getFirstName()).containsOnly("Doe");
        assertThat(result.getContent()).extracting(pu -> pu.getLastName()).containsOnly("John");
        assertThat(result.getContent()).extracting(pu -> pu.getTotalAmounts().doubleValue()).contains(100.0, 400.0);
    }
}
