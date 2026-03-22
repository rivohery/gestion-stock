package com.alibou.stockmanage.auths.repositories;

import com.alibou.stockmanage.auths.models.Role;
import com.alibou.stockmanage.auths.models.RoleEnum;
import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private RoleRepository roleRepository;
    UserDetails details1;

    @BeforeEach
    void setUp(){
        // Nettoyage avant chaque test
        roleRepository.deleteAll();
        userDetailsRepository.deleteAll();
        userRepository.deleteAll();

        Role adminManager = roleRepository.save(new Role(RoleEnum.ADMIN));
        Role stockManager = roleRepository.save(new Role(RoleEnum.STOCK_MANAGER));
        Role salesManager = roleRepository.save(new Role(RoleEnum.SALES_MANAGER));

        User admin = User.builder()
                .roles(Set.of(adminManager, stockManager,salesManager))
                .email("admin@gmail.com")
                .password("1234")
                .enabled(true)
                .build();

        UserDetails adminDetails = UserDetails.builder()
                .firstName("Alice")
                .lastName("Doe")
                .phoneNu("0347366212")
                .user(admin)
                .profileImage(null)
                .profilImageUrl("/users/download/1")
                .build();
        admin.setUserDetails(adminDetails);
        userRepository.save(admin);

        User stockmanager = User.builder()
                .roles(Set.of(stockManager))
                .email("stockmanager@gmail.com")
                .password("1234")
                .enabled(true)
                .build();

        UserDetails stockmanagerDetails = UserDetails.builder()
                .firstName("James")
                .lastName("Dupont")
                .phoneNu("0347366212")
                .user(stockmanager)
                .profileImage(null)
                .profilImageUrl("/users/download/2")
                .build();
        stockmanager.setUserDetails(stockmanagerDetails);
        userRepository.save(stockmanager);

        User salesmanager = User.builder()
                .roles(Set.of(salesManager))
                .email("salesmanager@gmail.com")
                .password("1234")
                .enabled(true)
                .build();

        UserDetails salesmanagerDetails = UserDetails.builder()
                .firstName("Bill")
                .lastName("Durand")
                .phoneNu("0347366212")
                .user(salesmanager)
                .profileImage(null)
                .profilImageUrl("/users/download/3")
                .build();
        salesmanager.setUserDetails(salesmanagerDetails);
        userRepository.save(salesmanager);

    }

    @Test
    void first(){
        List<User>users = userRepository.findAll();
        List<UserDetails>details = userDetailsRepository.findAll();

        assertThat(users.size()).isEqualTo(2);
        assertThat(details.size()).isEqualTo(2);
    }


    @DisplayName("Doit retourner le details d'un utilisateur qui correspond à la recherche")
    @Test
    void getEmployeeDetail_shouldReturnTheResultSuccessfully(){
        //Given
        Long userId = 1L;
        //When
        Optional<UserDetails>optional = userDetailsRepository.getEmployeeDetail(userId);
        //Then
        assertThat(optional.get()).isNotNull();
        assertThat(optional.get()).usingRecursiveComparison().comparingOnlyFields(
                "firstName","lastName","phoneNu"
        ).isEqualTo(details1);
        assertThat(optional.get().getFullName()).isEqualTo("Jean Dupont");
    }

    @Test
    void fetchAllUserBySearchTest() {
        //Given:  sensible à la casse
        String search = "d";
        Pageable pageable = PageRequest.of(0,10);

        //When
        Page<User>result = userRepository.fetchAllUserBySearch(search, pageable);
        //Then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getContent()).extracting(u -> u.getUserDetails().getLastName()).containsExactly(
                "Doe","Dupont","Durand"
        );
        assertThat(result.getContent()).extracting(u -> u.getUserDetails().getFirstName()).containsExactly(
                "Alice","James","Bill"
        );
        List<Integer>sizeOfUserRoles = result.getContent().stream().map(item -> item.getRoles().size()).toList();
        assertThat(sizeOfUserRoles).containsExactlyInAnyOrder(3, 1, 1);

        //*Other test*//*
        //Given:  insensible à la casse
        search = "du";
        //When
        Page<User>otherResult = userRepository.fetchAllUserBySearch(search, pageable);
        //Then
        assertThat(otherResult.getTotalElements()).isEqualTo(2);
        assertThat(otherResult.getTotalPages()).isEqualTo(1);
        assertThat(otherResult.getNumber()).isEqualTo(0);
        assertThat(otherResult.getContent().size()).isEqualTo(2);
        assertThat(otherResult.getContent()).extracting(u -> u.getUserDetails().getLastName()).containsExactly(
                "Dupont","Durand"
        );
        assertThat(otherResult.getContent()).extracting(u -> u.getUserDetails().getFirstName()).containsExactly(
                "James","Bill"
        );

        assertThat(otherResult.getContent().stream().map(item -> item.getRoles().size()).toList()).containsExactlyInAnyOrder(1, 1);
    }
}
