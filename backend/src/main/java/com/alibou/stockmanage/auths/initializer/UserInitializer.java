package com.alibou.stockmanage.auths.initializer;

import com.alibou.stockmanage.auths.models.Role;
import com.alibou.stockmanage.auths.models.RoleEnum;
import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.RoleRepository;
import com.alibou.stockmanage.auths.repositories.UserDetailsRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Order(2)
@Profile({"dev"})
@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Set<Role>adminRoles = new HashSet<>(roleRepository.findAll());

        var viewer = User.builder()
                .email("viewer@gmail.com")
                .password(bCryptPasswordEncoder.encode("1234"))
                .enabled(true)
                .roles(Set.of(
                        roleRepository.findByName(RoleEnum.VIEWER).get()
                ))
                .build();
        var viewerDetails = UserDetails.builder()
                .firstName("Mary")
                .lastName("Jane")
                .phoneNu("12345678")
                .user(viewer)
                .build();
        viewer.setUserDetails(viewerDetails);

        var stockManager = User.builder()
                .email("stockmanager@gmail.com")
                .password(bCryptPasswordEncoder.encode("1234"))
                .enabled(true)
                .roles(Set.of(
                        roleRepository.findByName(RoleEnum.STOCK_MANAGER).get(),
                        roleRepository.findByName(RoleEnum.VIEWER).get()
                ))
                .build();
        var stockManagerDetails = UserDetails.builder()
                .firstName("Peter")
                .lastName("Smith")
                .phoneNu("12345678")
                .user(stockManager)
                .build();
        stockManager.setUserDetails(stockManagerDetails);

        var saleManager = User.builder()
                .email("salemanager@gmail.com")
                .password(bCryptPasswordEncoder.encode("1234"))
                .enabled(true)
                .roles(Set.of(
                        roleRepository.findByName(RoleEnum.SALES_MANAGER).get(),
                        roleRepository.findByName(RoleEnum.VIEWER).get()
                ))
                .build();
        var salesManagerDetails = UserDetails.builder()
                .firstName("Bruce")
                .lastName("Lee")
                .phoneNu("12345678")
                .user(saleManager)
                .build();
        saleManager.setUserDetails(salesManagerDetails);

        var admin = User.builder()
                .email("admin@gmail.com")
                .password(bCryptPasswordEncoder.encode("1234"))
                .enabled(true)
                .roles(new HashSet<>(roleRepository.findAll()))
                .build();
        var adminDetails = UserDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNu("03333")
                .user(admin)
                .build();
        admin.setUserDetails(adminDetails);
        Arrays.asList(admin, saleManager, stockManager, viewer).forEach(user -> {
           if(!userRepository.existsByEmail(user.getEmail())){
               userRepository.save(user);
           }
        });


    }
}
