package com.alibou.stockmanage.stocks.repositories;

import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.UserDetailsRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.products.models.Category;
import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.repositories.CategoryRepository;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.stocks.models.StockMovement;
import com.alibou.stockmanage.stocks.models.StockMovementProjection;
import com.alibou.stockmanage.stocks.models.TypeMovement;
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
public class StockMovementRepositoryTest {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @BeforeEach
    void setUp(){
        userDetailsRepository.deleteAll();
        userRepository.deleteAll();
        stockMovementRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        supplierRepository.deleteAll();

        User user = User.builder()
                .roles(new HashSet<>())
                .email("admin@gmail.com")
                .password("1234")
                .enabled(true)
                .build();

        UserDetails adminDetails = UserDetails.builder()
                .firstName("Alice")
                .lastName("Doe")
                .phoneNu("0347366212")
                .user(user)
                .profileImage(null)
                .profilImageUrl("/users/download/1")
                .build();
        user.setUserDetails(adminDetails);
        user = userRepository.save(user);

        Supplier supplier = Supplier.builder()
                .address("address")
                .createdAt(LocalDate.now())
                .email("supplier@gmail.com")
                .phoneNu("234567")
                .name("Bill G")
                .build();
        supplier = supplierRepository.save(supplier);

        Category category = categoryRepository.save(
                Category.builder().reference("electrics").name("electrics").build()
        );

        Product product1 = Product.builder()
                .qtyStock(10)
                .alertStock(5)
                .supplier(supplier)
                .category(category)
                .code("product1")
                .costPrice(BigDecimal.valueOf(100.0))
                .salesPrice(BigDecimal.valueOf(150.0))
                .isActive(true)
                .name("product1")
                .unity("none")
                .photoPath(null)
                .build();
        product1 = productRepository.save(product1);
        Product product2 = Product.builder()
                .qtyStock(10)
                .alertStock(5)
                .supplier(supplier)
                .category(category)
                .code("product2")
                .costPrice(BigDecimal.valueOf(100.0))
                .salesPrice(BigDecimal.valueOf(150.0))
                .isActive(true)
                .name("product2")
                .unity("none")
                .photoPath(null)
                .build();
        product2 = productRepository.save(product2);

        StockMovement stockMovement1 = StockMovement
                .builder()
                .product(product1)
                .quantity(5)
                .reference("123456")
                .type(TypeMovement.IN)
                .createdBy(user.getId())
                .createdDate(LocalDate.now())
                .build();
        StockMovement stockMovement2 = StockMovement
                .builder()
                .product(product2)
                .quantity(6)
                .reference("456789")
                .type(TypeMovement.IN)
                .createdBy(user.getId())
                .createdDate(LocalDate.now())
                .build();
        StockMovement stockMovement3 = StockMovement
                .builder()
                .product(product1)
                .quantity(7)
                .reference("555555")
                .type(TypeMovement.OUT)
                .createdBy(user.getId())
                .createdDate(LocalDate.now())
                .build();
        stockMovementRepository.saveAll(Arrays.asList(stockMovement1, stockMovement2, stockMovement3));
    }

    @Test
    void first(){
        assertThat(stockMovementRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void fetchAllStockByCreatedDateTest(){
        //Given
        Pageable pageable = PageRequest.of(0, 6);

        //When
        Page<StockMovementProjection>result = stockMovementRepository.fetchAllPageOfStockByCreatedDate(LocalDate.now(), pageable);

        //Then
        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getContent()).extracting(item -> item.getUserDetails()).isNotInstanceOf(UserDetails.class);
        assertThat(result.getContent()).extracting(item -> item.getUserDetails().getFullName()).containsOnly("Alice Doe");
        assertThat(result.getContent()).extracting(item -> item.getStockMovement().getProduct().getName()).containsExactly("product1","product2","product1");
        assertThat(result.getContent()).extracting(item -> item.getStockMovement().getQuantity()).containsExactly(5,6,7);
        assertThat(result.getContent()).extracting(item -> item.getStockMovement().getReference()).containsExactly("123456","456789","555555");
    }
}
