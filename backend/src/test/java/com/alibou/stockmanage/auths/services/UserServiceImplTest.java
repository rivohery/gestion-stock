package com.alibou.stockmanage.auths.services;

import com.alibou.stockmanage.auths.models.Role;
import com.alibou.stockmanage.auths.models.RoleEnum;
import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.repositories.RoleRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    private UserService userService;

    @BeforeEach
    void setUp(){
        Set<Role>roles = Arrays.asList("STOCK_MANAGER","VIEWER","PURCHASE_MANAGER").stream()
                .map(r -> roleRepository.findByName(RoleEnum.valueOf(r)).get())
                .collect(Collectors.toSet());
        User user = User.builder()
                .email("user@gmail.com")
                .password("1234")
                .enabled(true)
                .roles(new HashSet<>())
                .build();
        user = userRepository.save(user);
        for(Role role: roles){
            user.getRoles().add(role);
        }
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Test
    void shouldDeleteUserById(){
        Long userId = userRepository.findByEmail("user@gmail.com").get().getId();

        var result = userService.deleteUserById(userId);

        assertThat(result).isTrue();
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        assertThat(roleRepository.findAll().size()).isEqualTo(6);
    }
}
