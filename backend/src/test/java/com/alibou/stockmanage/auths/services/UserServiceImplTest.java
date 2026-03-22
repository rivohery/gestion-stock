package com.alibou.stockmanage.auths.services;

import com.alibou.stockmanage.auths.models.Role;
import com.alibou.stockmanage.auths.models.RoleEnum;
import com.alibou.stockmanage.auths.repositories.RoleRepository;
import com.alibou.stockmanage.auths.repositories.UserDetailsRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.auths.web.dtos.CreateUserRequest;
import com.alibou.stockmanage.auths.web.dtos.UserDetailsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional// pour rollback après chaque test
public class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp(){
        // Nettoyage avant chaque test
        roleRepository.deleteAll();
        userDetailsRepository.deleteAll();
        userRepository.deleteAll();

        Role adminManager = roleRepository.save(new Role(RoleEnum.ADMIN));
        Role stockManager = roleRepository.save(new Role(RoleEnum.STOCK_MANAGER));
        Role salesManager = roleRepository.save(new Role(RoleEnum.SALES_MANAGER));


    }

    @Test
    void createUser_shouldReturnUserDetailsResponse(){
        //Given
        CreateUserRequest request = new CreateUserRequest(
                "John",
                "Doe",
                "04567894",
                "john@gmail.com",
                Set.of(RoleEnum.STOCK_MANAGER, RoleEnum.ADMIN)
        );
        //When
        UserDetailsResponse response = userService.createUser(request);
        //Then
        assertThat(userDetailsRepository.findAll().size()).isEqualTo(1);
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        assertThat(response.getEmail()).isEqualTo("john@gmail.com");
        assertThat(response.getFullName()).isEqualTo("John Doe");
        assertThat(response.getRole()).contains("STOCK_MANAGER","ADMIN");

    }

    @Test
    void shouldDeleteUserById(){
        //Given
        CreateUserRequest user1 = new CreateUserRequest(
                "James",
                "Brown",
                "04567894",
                "user1@gmail.com",
                Set.of(RoleEnum.STOCK_MANAGER, RoleEnum.ADMIN)
        );
        CreateUserRequest user2 = new CreateUserRequest(
                "Steve",
                "Jobs",
                "04567894",
                "user2@gmail.com",
                Set.of(RoleEnum.STOCK_MANAGER)
        );
        CreateUserRequest user3 = new CreateUserRequest(
                "Charlie",
                "Houston",
                "04567894",
                "user3@gmail.com",
                Set.of(RoleEnum.SALES_MANAGER)
        );
        Set.of(user1,user2,user3).forEach(user -> {
            userService.createUser(user);
        });
        Long userId = userRepository.findByEmail("user1@gmail.com").get().getId();
        assertThat(userRepository.findAll().size()).isEqualTo(3);

        var result = userService.deleteUserById(userId);

        assertThat(result).isTrue();
        assertThat(userRepository.findAll().size()).isEqualTo(2);
        assertThat(roleRepository.findAll().size()).isEqualTo(3);
    }
}
