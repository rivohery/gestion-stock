package com.alibou.stockmanage.suppliers.initializer;

import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Order(3)
@Profile({"dev"})
@Component
@RequiredArgsConstructor
public class SupplierInitializer implements CommandLineRunner {
    private final SupplierRepository supplierRepository;

    @Override
    public void run(String... args) throws Exception {
        Arrays.asList("Mohamed", "Moussala", "Lionel", "Christiano").forEach(name -> {
            if(!supplierRepository.existsByName(name)){
                var supplier = Supplier.builder()
                        .address("Mexico")
                        .createdAt(LocalDate.now())
                        .email(name.toLowerCase() +"@gmail.com")
                        .phoneNu("12345678")
                        .name(name)
                        .build();
                supplierRepository.save(supplier);
            }
        });

    }
}
