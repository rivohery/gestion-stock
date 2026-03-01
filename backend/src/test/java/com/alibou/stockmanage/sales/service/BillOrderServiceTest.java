package com.alibou.stockmanage.sales.service;

import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.products.models.Category;
import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.repositories.CategoryRepository;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.sales.exceptions.InsufficientQtyStockException;
import com.alibou.stockmanage.sales.repositories.BillOrderItemRepository;
import com.alibou.stockmanage.sales.repositories.BillOrderRepository;
import com.alibou.stockmanage.sales.web.dto.BillOrderItemRequest;
import com.alibou.stockmanage.sales.web.dto.BillOrderRequest;
import com.alibou.stockmanage.sales.web.dto.BillOrderResponse;
import com.alibou.stockmanage.stocks.models.TypeMovement;
import com.alibou.stockmanage.stocks.repositories.StockMovementRepository;
import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class BillOrderServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BillOrderRepository billOrderRepository;
    @Autowired
    private BillOrderItemRepository billOrderItemRepository;
    @Autowired
    private BillOrderService billOrderService;
    @Autowired
    private StockMovementRepository stockMovementRepository;



    @MockBean
    private AuditorAware auditorAware;
    @MockBean
    private UserRepository userRepository;

    Product product1;
    Product product2;
    Product product3;

    @BeforeEach
    void setUp(){
        billOrderItemRepository.deleteAll();
        billOrderRepository.deleteAll();
        stockMovementRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        supplierRepository.deleteAll();

        Category category = categoryRepository.save(Category.builder().name("electronics").reference("electronics").build());
        Supplier supplier = supplierRepository.save(
                Supplier.builder()
                        .name("Mohamed")
                        .phoneNu("1234")
                        .email("mohamed@gmail.com")
                        .address("test")
                        .createdAt(LocalDate.now())
                        .build()
        );
        product1 = Product.builder()
                .alertStock(5)
                .category(category)
                .code("23455")
                .costPrice(BigDecimal.valueOf(200))
                .name("Lenovo 123")
                .unity("none")
                .photoPath("")
                .qtyStock(20)
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
                .qtyStock(20)
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
                .qtyStock(20)
                .isActive(true)
                .salesPrice(BigDecimal.valueOf(300))
                .supplier(supplier)
                .build();
        productRepository.saveAll(Set.of(product1,product2,product3));

        Mockito.when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(1L));
        Mockito.when(userRepository.getEmployeeDetail(1l)).thenReturn(Optional.of(UserDetails.builder().fullName("BillDoe").build()));
    }
    @Test
    void createBillOrder_shouldHandleInsufficientQtyException(){
        //Given
        BillOrderRequest request = new BillOrderRequest(
                "12345",
                "Bill",
                "12345",
                "bill@gmail.com",
                "cash",
                BigDecimal.valueOf(6000),
                Set.of(
                        new BillOrderItemRequest(product1.getId(), product1.getName(), 50, BigDecimal.valueOf(1000)),
                        new BillOrderItemRequest(product2.getId(), product2.getName(), 10, BigDecimal.valueOf(3500)),
                        new BillOrderItemRequest(product3.getId(), product3.getName(), 5, BigDecimal.valueOf(1500))
                )
        );
        assertThatThrownBy(()-> billOrderService.createBillOrder(request))
                .isInstanceOf(InsufficientQtyStockException.class)
                .hasMessageContaining(String.format("Quantity stock of %s insufficient", product1.getName()));

        assertThat(stockMovementRepository.findAll().size()).isEqualTo(0);
        assertThat(productRepository.findAll()).extracting(product -> product.getQtyStock()).containsOnly(20);
    }

    @Test
    void createBillOrder_shouldCreateOrderWithSuccess(){
        //Given
        BillOrderRequest request = new BillOrderRequest(
             "12345",
             "Bill",
             "12345",
             "bill@gmail.com",
             "cash",
             BigDecimal.valueOf(6000),
                Set.of(
                   new BillOrderItemRequest(product1.getId(), product1.getName(), 4, BigDecimal.valueOf(1000)),
                   new BillOrderItemRequest(product2.getId(), product2.getName(), 10, BigDecimal.valueOf(3500)),
                   new BillOrderItemRequest(product3.getId(), product3.getName(), 5, BigDecimal.valueOf(1500))
                )
        );

        //when
        BillOrderResponse response = billOrderService.createBillOrder(request);

        //Then
        assertThat(billOrderItemRepository.findAll().size()).isEqualTo(3);
        assertThat(billOrderRepository.findAll().size()).isEqualTo(1);
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().comparingOnlyFields(
            "invoiceNo","customer","phoneNu","email","paymentMethod"
        ).isEqualTo(request);
        assertThat(response.getItems().size()).isEqualTo(3);
        assertThat(response.getItems()).extracting(item -> item.getItemName())
                .containsExactlyInAnyOrder("Lenovo 123","Smartphone 123", "Hp 123");
        assertThat(response.getItems()).extracting(item -> item.getQuantity())
                .containsExactlyInAnyOrder(4,10, 5);
        assertThat(stockMovementRepository.findAll().size()).isEqualTo(3);
        assertThat(stockMovementRepository.findAll()).extracting(stockMvt -> stockMvt.getType()).containsExactly(
                TypeMovement.OUT,TypeMovement.OUT,TypeMovement.OUT );
        assertThat(productRepository.findAll()).extracting(product -> product.getQtyStock()).contains(
            16, 10, 15
        );
    }
}
