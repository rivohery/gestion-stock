package com.alibou.stockmanage.purchases.web.controllers;

import com.alibou.stockmanage.auths.models.Role;
import com.alibou.stockmanage.auths.models.RoleEnum;
import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.RoleRepository;
import com.alibou.stockmanage.auths.repositories.UserDetailsRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.auths.web.dtos.LoginRequest;
import com.alibou.stockmanage.auths.web.dtos.LoginResponse;
import com.alibou.stockmanage.products.models.Category;
import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.repositories.CategoryRepository;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.purchases.models.PurchaseOrderStatus;
import com.alibou.stockmanage.purchases.repositories.PurchaseOrderItemRepository;
import com.alibou.stockmanage.purchases.repositories.PurchaseOrderRepository;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderItemRequest;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderRequest;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderResponse;
import com.alibou.stockmanage.shared.handlers.HttpErrorResponse;
import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PurchaseOrderControllerIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*@MockBean
    private AuditorAware auditorAware;*/

    private User admin;
    private User viewer;
    private String accessToken;

    Product product1;
    Product product2;
    Product product3;
    Supplier supplier;


    private String getBasePurchaseOrderUrl() {
        return "http://localhost:" + port + "/api/v1/stock/purchase-order";
    }

    @BeforeEach
    void setUp(){
        purchaseOrderItemRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        supplierRepository.deleteAll();
        userDetailsRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Arrays.asList(RoleEnum.ADMIN, RoleEnum.STOCK_MANAGER, RoleEnum.VIEWER).forEach(roleName -> {
            if(!roleRepository.existsByName(roleName)){
                roleRepository.save(Role.builder().name(roleName).build());
            }
        });
        admin = User.builder()
                .email("admin@gmail.com")
                .enabled(true)
                .password(passwordEncoder.encode("1234"))
                .roles(
                        Set.of(
                                roleRepository.findByName(RoleEnum.ADMIN).get(),
                                roleRepository.findByName(RoleEnum.STOCK_MANAGER).get()
                        ))
                .build();
        UserDetails adminDetails = UserDetails.builder()
                .user(admin)
                .phoneNu("1234567")
                .lastName("Doe")
                .firstName("John")
                .build();
        admin.setUserDetails(adminDetails);

        viewer  = User.builder()
                .email("viewer@gmail.com")
                .enabled(true)
                .password(passwordEncoder.encode("1234"))
                .roles(
                        Set.of(
                                roleRepository.findByName(RoleEnum.VIEWER).get()
                        ))
                .build();
        userRepository.saveAll(List.of(admin, viewer));

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
        product1 = productRepository.save(product1);

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
        product2 = productRepository.save(product2);

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
        product3 = productRepository.save(product3);

        assertThat(roleRepository.findAll().size()).isEqualTo(3);
        assertThat(userRepository.findAll().size()).isEqualTo(2);
        assertThat(userDetailsRepository.findAll().size()).isEqualTo(1);
        assertThat(supplierRepository.findAll().size()).isEqualTo(1);
        assertThat(categoryRepository.findAll().size()).isEqualTo(1);
        assertThat(productRepository.findAll().size()).isEqualTo(3);

        /* For login*/
        var loginRequest = new LoginRequest("admin@gmail.com", "1234");
        ResponseEntity<LoginResponse>response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/auth//login-by-password",
                loginRequest,
                LoginResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().token()).isNotNull();
        accessToken = response.getBody().token();

    }

    @AfterEach
    void tearDown() {
        // Nettoyer après chaque test
        purchaseOrderItemRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        supplierRepository.deleteAll();
        userDetailsRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void generateUniqueInvoiceNo_shouldReturnInvoiceNoGenerated(){
        //when
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String>entity = new HttpEntity<>(headers);

        ResponseEntity<Map>response = restTemplate.exchange(
            getBasePurchaseOrderUrl() + "/check-invoice-no",
                HttpMethod.GET,
                entity,
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("invoiceNo")).isNotNull();
        assertThat(((String)response.getBody().get("invoiceNo")).length()).isEqualTo(6);
    }

    @Test
    void createPurchaseOrder_shouldCreatePurchaseOrderResponseWithSuccess(){
        //Given
        PurchaseOrderRequest purchaseOrderRequest = new PurchaseOrderRequest(
                "123456",
                supplier.getId(),
                LocalDate.of(2026, 1, 16),
                BigDecimal.valueOf(200.0),
                Set.of(
                        new PurchaseOrderItemRequest(5, product1.getId(), product1.getName(), BigDecimal.valueOf(120)),
                        new PurchaseOrderItemRequest(5, product2.getId(), product2.getName(), BigDecimal.valueOf(20)),
                        new PurchaseOrderItemRequest(5, product3.getId(), product3.getName(), BigDecimal.valueOf(40))
                )
        );

        //When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<PurchaseOrderRequest>entity = new HttpEntity<>(purchaseOrderRequest, headers);
        ResponseEntity<PurchaseOrderResponse>response = restTemplate.exchange(
                getBasePurchaseOrderUrl() + "/create",
                HttpMethod.POST,
                entity,
                PurchaseOrderResponse.class
        );

        //Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSupplierName()).isEqualTo("test");
        assertThat(response.getBody().getEmployee()).isEqualTo("John Doe");
        assertThat(response.getBody().getStatus().name()).isEqualTo(PurchaseOrderStatus.PENDING.name());
        assertThat(response.getBody().getItems().size()).isEqualTo(3);

        assertThat(purchaseOrderItemRepository.findAll().size()).isEqualTo(3);
        assertThat(purchaseOrderItemRepository.findAll()).extracting(item -> item.getProduct().getName())
                .contains("Smartphone 123", "Hp 123", "Lenovo 123");

    }

    @Test
    void createPurchaseOrder_shouldForbiddenTheResource(){
        //Given
        LoginRequest request = new LoginRequest("viewer@gmail.com", "1234");
        ResponseEntity<LoginResponse>loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/auth//login-by-password",
                request,
                LoginResponse.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody().token()).isNotNull();
        String token = loginResponse.getBody().token();

        PurchaseOrderRequest purchaseOrderRequest = new PurchaseOrderRequest(
                "123456",
                supplier.getId(),
                LocalDate.of(2026, 1, 16),
                BigDecimal.valueOf(200.0),
                Set.of(
                        new PurchaseOrderItemRequest(5, product1.getId(), product1.getName(), BigDecimal.valueOf(120)),
                        new PurchaseOrderItemRequest(5, product2.getId(), product2.getName(), BigDecimal.valueOf(20)),
                        new PurchaseOrderItemRequest(5, product3.getId(), product3.getName(), BigDecimal.valueOf(40))
                )
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<PurchaseOrderRequest>entity = new HttpEntity<>(purchaseOrderRequest, headers);

        //When
        ResponseEntity<HttpErrorResponse>response = restTemplate.exchange(
                getBasePurchaseOrderUrl() + "/create",
                HttpMethod.POST,
                entity,
                HttpErrorResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getMessage()).isEqualTo("Access Denied: some role is required to access this resource");

    }

    @Test
    void createPurchaseOrder_shouldThrowsBadRequestException() {
        //Given
        PurchaseOrderRequest purchaseOrderRequest = new PurchaseOrderRequest(
                "",
                supplier.getId(),
                LocalDate.of(2026, 1, 14),
                null,
                Set.of()
        );

        //When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<PurchaseOrderRequest>entity = new HttpEntity<>(purchaseOrderRequest, headers);
        ResponseEntity<HttpErrorResponse>response = restTemplate.exchange(
                getBasePurchaseOrderUrl() + "/create",
                HttpMethod.POST,
                entity,
                HttpErrorResponse.class
        );

        //Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Invoice Number is required","Items is not empty", "Total amount is not null");
        assertThat(response.getBody().getMessage()).contains("Items is not empty");
    }

    @Test
    void createPurchaseOrder_shouldThrowsBadRequestExceptionIfAnyItemsInvalid() {
        //Given
        PurchaseOrderRequest purchaseOrderRequest = new PurchaseOrderRequest(
                "123456",
                supplier.getId(),
                LocalDate.of(2026, 1, 14),
                BigDecimal.valueOf(200.0),
                Set.of(
                        new PurchaseOrderItemRequest(5, null,null, BigDecimal.valueOf(120)),
                        new PurchaseOrderItemRequest(0, product2.getId(), product2.getName(), BigDecimal.valueOf(20)),
                        new PurchaseOrderItemRequest(5, product3.getId(), product3.getName(), BigDecimal.ZERO)
                )
        );

        //When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<PurchaseOrderRequest>entity = new HttpEntity<>(purchaseOrderRequest, headers);
        ResponseEntity<HttpErrorResponse>response = restTemplate.exchange(
                getBasePurchaseOrderUrl() + "/create",
                HttpMethod.POST,
                entity,
                HttpErrorResponse.class
        );

        //Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage())
                .contains("Product of Items is not null","Quantity of Items must positive","Total of Items must positive");
    }


}
