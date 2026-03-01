package com.alibou.stockmanage.purchases.mappers;

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
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderItemResponse;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderRequest;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderResponse;
import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class PurchaseOrderMapperTest {
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    Supplier supplier;
    Product product1;
    Product product2;
    Product product3;
    User user;

    @BeforeEach
    void setUp() {
        supplierRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        purchaseOrderItemRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        userRepository.deleteAll();
        userDetailsRepository.deleteAll();

        user = User.builder()
                .enabled(true)
                .roles(new HashSet<>())
                .email("alibou@gmail.com")
                .password("1234")
                .build();
        var details = UserDetails
                .builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNu("987654")
                .user(user)
                .build();
        user.setUserDetails(details);
        user = userRepository.save(user);

        supplier = Supplier.builder()
                .address("testaddress")
                .createdAt(LocalDate.now())
                .email("testemail@gmail.com")
                .name("test")
                .phoneNu("098765")
                .build();
        supplier = supplierRepository.save(supplier);

        Category category = Category.builder().name("electric").reference("electric").build();
        category = categoryRepository.save(category);

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

    }

    @DisplayName("Doit retourner l'objet PurchaseOrder correspondant")
    @Test
    void mapToEntity_shouldReturnTheMappedPurchaseOrder() {
        //GIVEN
        PurchaseOrderRequest request = new PurchaseOrderRequest(
                "123456",
                1L,
                LocalDate.of(2026, 1, 14),
                BigDecimal.valueOf(200.0),
                Set.of(
                        new PurchaseOrderItemRequest(5, 1L, product1.getName(), BigDecimal.valueOf(120)),
                        new PurchaseOrderItemRequest(5, 2L, product2.getName(), BigDecimal.valueOf(20)),
                        new PurchaseOrderItemRequest(5, 3L, product3.getName(), BigDecimal.valueOf(40))
                )
        );
        //WHEN
        var purchaseOrder = purchaseOrderMapper.mapToEntity(request);
        //THEN
        assertThat(purchaseOrder).usingRecursiveComparison()
                .comparingOnlyFields("invoiceNo", "billDate", "receiveDate", "totalAmounts")
                .isEqualTo(request);
        assertThat(purchaseOrder.getSupplier())
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "address", "createdAt", "email", "name", "phoneNu")
                .isEqualTo(supplier);

    }

    @Test
    void mapToResponse_shouldReturnPurchaseOrderResponse() {
        //GIVEN
        PurchaseOrder purchaseOrder = PurchaseOrder
                .builder()
                .receiveDate(LocalDate.of(2026, 1, 16))
                .createdBy(user.getId())
                .invoiceNo("123456")
                .status(PurchaseOrderStatus.PENDING)
                .supplier(supplier)
                .totalAmounts(BigDecimal.valueOf(5500))
                .build();
        var items1 = PurchaseOrderItem.builder().product(product1).purchaseOrder(purchaseOrder).quantity(10).totalItems(BigDecimal.valueOf(2000)).build();
        var items2 = PurchaseOrderItem.builder().product(product2).purchaseOrder(purchaseOrder).quantity(10).totalItems(BigDecimal.valueOf(2500)).build();
        var items3 = PurchaseOrderItem.builder().product(product3).purchaseOrder(purchaseOrder).quantity(5).totalItems(BigDecimal.valueOf(1000)).build();
        purchaseOrder.setPurchaseOrderItems(Set.of(items1, items2, items3));

        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        assertThat(purchaseOrderRepository.findAll().size()).isEqualTo(1);
        assertThat(purchaseOrderItemRepository.findAll().size()).isEqualTo(3);
        assertThat(userDetailsRepository.findAll().size()).isEqualTo(1);


        //WHEN
        PurchaseOrderResponse response = purchaseOrderMapper.mapToResponse(purchaseOrder);

        //THEN
        assertThat(response).usingRecursiveComparison()
                .comparingOnlyFields("billDate", "receiveDate", "invoiceNo", "status", "totalAmounts")
                .isEqualTo(purchaseOrder);
        assertThat(response.getSupplierName()).isEqualTo("test");
        assertThat(response.getItems().size()).isEqualTo(3);
        assertThat(response.getItems())
                .extracting(item -> item.getItemName())
                .containsExactlyInAnyOrder("Lenovo 123", "Smartphone 123", "Hp 123");
        assertThat(response.getItems()).extracting("totalItems").contains(
                BigDecimal.valueOf(2500),
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(1000));

    }

    @Test
    void mapToPurchaseOrderItemResponse_shouldReturnPurchaseOrderItemResponse(){
        //GIVEN
        PurchaseOrder purchaseOrder = PurchaseOrder
                .builder()
                .receiveDate(LocalDate.of(2026, 1, 16))
                .createdBy(user.getId())
                .invoiceNo("123456")
                .status(PurchaseOrderStatus.PENDING)
                .supplier(supplier)
                .totalAmounts(BigDecimal.valueOf(5500))
                .build();
        var items = PurchaseOrderItem.builder().product(product1).purchaseOrder(purchaseOrder).quantity(10).totalItems(BigDecimal.valueOf(2000)).build();
        purchaseOrder.setPurchaseOrderItems(Set.of(items));
        purchaseOrderRepository.save(purchaseOrder);
        assertThat(purchaseOrderItemRepository.findAll().size()).isEqualTo(1);

        //When
        PurchaseOrderItem registred = purchaseOrderItemRepository.findAll().stream().findFirst().get();
        PurchaseOrderItemResponse response = purchaseOrderMapper.mapToPurchaseOrderItemResponse(registred);

        //Then
        assertThat(response.getItemName()).isNotNull();
        assertThat(response.getItemName()).isEqualTo("Lenovo 123");
        assertThat(response.getCostPrice().compareTo(BigDecimal.valueOf(200))).isEqualTo(0);
        assertThat(response.getQuantity()).isEqualTo(10);
    }
}



