package com.alibou.stockmanage.auths.initializer;

import com.alibou.stockmanage.auths.models.Role;
import com.alibou.stockmanage.auths.models.RoleEnum;
import com.alibou.stockmanage.auths.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Profile({"dev"})
@Order(1)
@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        Arrays.asList(
                RoleEnum.ADMIN,
                RoleEnum.SALES_MANAGER,
                RoleEnum.STOCK_MANAGER,
                RoleEnum.VIEWER
        ).forEach(roleName -> {
            if(!roleRepository.existsByName(roleName)){
                roleRepository.save(new Role(roleName));
            }
        });
    }
}
