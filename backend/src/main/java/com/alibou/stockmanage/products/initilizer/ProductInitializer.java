package com.alibou.stockmanage.products.initilizer;

import com.alibou.stockmanage.products.models.Category;
import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.repositories.CategoryRepository;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@Order(4)
@Profile({"dev"})
@RequiredArgsConstructor
public class ProductInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private final SupplierRepository supplierRepository;

    @Override
    public void run(String... args) throws Exception {

        List<Supplier>suppliers = supplierRepository.findAll();
        Arrays.asList("phones","electronics","computers").forEach(cat -> {
            if(!categoryRepository.existsByName(cat)){
                var category = Category.builder()
                        .name(cat)
                        .reference(cat)
                        .build();
                categoryRepository.save(category);
            }
        });
        var phones = categoryRepository.findByReference("phones");
        var electronics = categoryRepository.findByReference("electronics");
        var computers = categoryRepository.findByReference("computers");

        Arrays.asList("Lenovo123","HP345","Acer345","Del456").forEach(name -> {
            if(!productRepository.existsByName(name)){
                Product product = Product.builder()
                        .unity("none")
                        .name(name)
                        .isActive(true)
                        .salesPrice(BigDecimal.valueOf(300))
                        .costPrice(BigDecimal.valueOf(250))
                        .code(name.toLowerCase())
                        .category(computers)
                        .supplier(suppliers.get(1))
                        .alertStock(10)
                        .qtyStock(10)
                        .build();
                productRepository.save(product);
            }
        });
        Arrays.asList("Motorola","Legend","Nokia","Smart phone").forEach(name -> {
            if(!productRepository.existsByName(name)){
                Product product = Product.builder()
                        .unity("none")
                        .name(name)
                        .isActive(true)
                        .salesPrice(BigDecimal.valueOf(200))
                        .costPrice(BigDecimal.valueOf(150))
                        .code(name.toLowerCase())
                        .category(phones)
                        .supplier(suppliers.get(2))
                        .alertStock(10)
                        .qtyStock(10)
                        .build();
                productRepository.save(product);
            }
        });
        Arrays.asList("TVPlat12","Sony Sono34","Modem 147","Souris 54").forEach(name -> {
            if(!productRepository.existsByName(name)){
                Product product = Product.builder()
                        .unity("none")
                        .name(name)
                        .isActive(true)
                        .salesPrice(BigDecimal.valueOf(100))
                        .costPrice(BigDecimal.valueOf(70))
                        .code(name.toLowerCase())
                        .category(electronics)
                        .supplier(suppliers.get(3))
                        .alertStock(10)
                        .qtyStock(10)
                        .build();
                productRepository.save(product);
            }
        });

    }
}
