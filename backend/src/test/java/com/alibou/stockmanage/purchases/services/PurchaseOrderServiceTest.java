package com.alibou.stockmanage.purchases.services;

import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.UserDetailsRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.products.models.Category;
import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.repositories.CategoryRepository;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.purchases.models.PurchaseOrder;
import com.alibou.stockmanage.purchases.models.PurchaseOrderItem;
import com.alibou.stockmanage.purchases.models.PurchaseOrderStatus;
import com.alibou.stockmanage.purchases.repositories.PurchaseOrderItemRepository;
import com.alibou.stockmanage.purchases.repositories.PurchaseOrderRepository;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderItemRequest;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderRequest;
import com.alibou.stockmanage.purchases.web.dtos.UpdateStatusPurchaseOrderRequest;
import com.alibou.stockmanage.stocks.models.TypeMovement;
import com.alibou.stockmanage.stocks.repositories.StockMovementRepository;
import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class PurchaseOrderServiceTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private StockMovementRepository stockMovementRepository;

    @MockBean
    private AuditorAware auditorAware;

    Supplier supplier;
    Product product1;
    Product product2;
    Product product3;
    User user;
    UserDetails details;

    PurchaseOrderRequest request;

    PurchaseOrder purchaseOrder;


    @BeforeEach
    void setUp(){
        productRepository.deleteAll();
        userRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        supplierRepository.deleteAll();

        user = User.builder()
                .enabled(true)
                .roles(new HashSet<>())
                .email("alibou@gmail.com")
                .password("1234")
                .build();
        details =  UserDetails
                .builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNu("987654")
                .user(user)
                .build();
        user.setUserDetails(details);
        user = userRepository.save(user);

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(user.getId()));

        supplier = Supplier.builder()
                .address("testaddress")
                .createdAt(LocalDate.now())
                .email("testemail@gmail.com")
                .name("test")
                .phoneNu("098765")
                .build();
        supplier = supplierRepository.save(supplier);

        Category category = categoryRepository.save(Category.builder().name("electronic").reference("electronic").build());
        product1 = Product.builder()
                .alertStock(5)
                .category(category)
                .code("23455")
                .costPrice(BigDecimal.valueOf(200))
                .name("Lenovo 123")
                .unity("none")
                .photoPath("")
                .qtyStock(0)
                .isActive(true)
                .salesPrice(BigDecimal.valueOf(250))
                .supplier(supplier)
                .build();
        product2 = Product.builder()
                .alertStock(10)
                .category(category)
                .code("23456")
                .costPrice(BigDecimal.valueOf(250))
                .name("Smartphone 123")
                .photoPath("")
                .unity("none")
                .qtyStock(0)
                .isActive(true)
                .salesPrice(BigDecimal.valueOf(350))
                .supplier(supplier)
                .build();
        product3 = Product.builder()
                .alertStock(20)
                .category(category)
                .code("23457")
                .costPrice(BigDecimal.valueOf(200))
                .name("Hp 123")
                .photoPath("")
                .unity("none")
                .qtyStock(0)
                .isActive(true)
                .salesPrice(BigDecimal.valueOf(300))
                .supplier(supplier)
                .build();
        productRepository.saveAll(List.of(product1, product2, product3));

        purchaseOrder = PurchaseOrder.builder()
                .receiveDate(LocalDate.of(2026, 1, 16))
                .invoiceNo("123456")
                .status(PurchaseOrderStatus.PENDING)
                .supplier(supplier)
                .totalAmounts(BigDecimal.valueOf(50000))
                .build();
        PurchaseOrderItem item1 = PurchaseOrderItem.builder()
                .product(product1)
                .purchaseOrder(purchaseOrder)
                .quantity(5)
                .totalItems(BigDecimal.valueOf(5000))
                .build();
        PurchaseOrderItem item2 = PurchaseOrderItem.builder()
                .product(product2)
                .purchaseOrder(purchaseOrder)
                .quantity(10)
                .totalItems(BigDecimal.valueOf(15000))
                .build();
        PurchaseOrderItem item3 = PurchaseOrderItem.builder()
                .product(product3)
                .purchaseOrder(purchaseOrder)
                .quantity(15)
                .totalItems(BigDecimal.valueOf(25000))
                .build();
        purchaseOrder.setPurchaseOrderItems(Set.of(item1, item2, item3));
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
    }

    @Test
    void before(){
        assertThat(userDetailsRepository.findAll()).isNotNull();
        assertThat(userDetailsRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void generateUniqueInvoiceNo_shouldReturnTheUniqueInvoiceNo(){
        //When
        String invoiceNo = purchaseOrderService.generateUniqueInvoiceNo();
        //Then
        assertThat(invoiceNo).isNotNull();
        assertThat(invoiceNo.length()).isEqualTo(6);
        assertThat(purchaseOrderRepository.existsByInvoiceNo(invoiceNo)).isFalse();
    }

    @DisplayName("Doit retourner l'objet PurchaseOrderResponse crée")
    @Test
    void createPurchaseOrder_shouldReturnPurchaseOrderResponse(){
        //GIVEN
        request = new PurchaseOrderRequest(
                "123456",
                supplier.getId(),
                LocalDate.of(2026, 1, 14),
                BigDecimal.valueOf(200.0),
                Set.of(
                        new PurchaseOrderItemRequest(5, 1L, product1.getName(), BigDecimal.valueOf(120)),
                        new PurchaseOrderItemRequest(5, 2L, product2.getName(), BigDecimal.valueOf(20)),
                        new PurchaseOrderItemRequest(5, 3L, product3.getName(), BigDecimal.valueOf(40))
                )
        );
        //WHEN
        var purchaseOrderResponse = purchaseOrderService.createPurchaseOrder(request);
        //THEN
        assertThat(purchaseOrderResponse).isNotNull();
        assertThat(purchaseOrderResponse).usingRecursiveComparison().comparingOnlyFields(
                "invoiceNo","billDate","receiveDate","totalAmounts"
        ).isEqualTo(request);
        assertThat(purchaseOrderResponse.getSupplierName()).isEqualTo(supplier.getName());
        assertThat(purchaseOrderResponse.getItems().size()).isEqualTo(3);
        assertThat(purchaseOrderResponse
                .getItems())
                .extracting(item -> item.getItemName())
                .containsExactlyInAnyOrder("Lenovo 123","Smartphone 123", "Hp 123");
        assertThat(purchaseOrderResponse.getItems()).extracting("totalItems").contains(
                BigDecimal.valueOf(120),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(40)
        );

    }

    @DisplayName("Doit modifier le status de la commande fournisseur avec modification de la quantité stocké et création des mouvements de stock pour les produits confirmer")
    @Test
    void updateStatusOfPurchaseOrder_shouldReturnPurchaseOrderUpdatedWhenConfirmedStatus(){
        assertThat(purchaseOrderRepository.findAll().size()).isEqualTo(1);
        assertThat(purchaseOrderItemRepository.findAll().size()).isEqualTo(3);

        //WHEN
        var updateStatusOrderRequest = new UpdateStatusPurchaseOrderRequest(
                purchaseOrder.getId(),
                PurchaseOrderStatus.CONFIRMED
        );
        PurchaseOrder purchaseOrder = purchaseOrderService.updateStatusOfPurchaseOrder(updateStatusOrderRequest);

        //THEN
        assertThat(purchaseOrder.getStatus()).isEqualTo(PurchaseOrderStatus.CONFIRMED);
        assertThat(productRepository.findAll()).extracting(product -> product.getQtyStock()).containsExactlyInAnyOrder(5,10,15);
        assertThat(stockMovementRepository.findAll().size()).isEqualTo(3);
        assertThat(stockMovementRepository.findAll()).extracting(movement -> movement.getReference()).containsOnly("123456");
        assertThat(stockMovementRepository.findAll()).extracting(movement -> movement.getType()).containsOnly(TypeMovement.IN);
        assertThat(stockMovementRepository.findAll()).extracting(movement -> movement.getQuantity()).containsExactlyInAnyOrder(5,10,15);
    }

    @Test
    void deletePurchaseOrderById_shouldDeletePurchaseOrderAndPurchaseOrderItemSuccessfully(){
        assertThat(purchaseOrderRepository.findAll().size()).isEqualTo(1);
        assertThat(purchaseOrderItemRepository.findAll().size()).isEqualTo(3);

        //WHEN
        var deleted = purchaseOrderService.deletePurchaseOrderById(purchaseOrder.getId());

        //THEN
        assertThat(deleted).isTrue();
        assertThat(purchaseOrderRepository.findAll().size()).isEqualTo(0);
        assertThat(purchaseOrderItemRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void deletePurchaseOrderById_shouldThrowEntityNotFoundException(){
        assertThat(purchaseOrderRepository.findAll().size()).isEqualTo(1);
        assertThat(purchaseOrderItemRepository.findAll().size()).isEqualTo(3);

        //WHEN-THEN
        assertThatThrownBy(() -> purchaseOrderService.deletePurchaseOrderById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No Purchase order found with ID: 99");
    }


}



