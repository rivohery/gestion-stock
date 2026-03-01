package com.alibou.stockmanage.auths.repositories;

import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.UserDetailsRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  UserDetailsRepository userDetailsRepository;

    User user1;
    User user2;
    UserDetails details1;
    UserDetails details2;

    @BeforeEach
    void setUp(){
        // Nettoyage avant chaque test
        userDetailsRepository.deleteAll();
        userRepository.deleteAll();

        user1 = User.builder()
                .email("jean.dupont@test.com")
                .roles(new HashSet<>())
                .password("pass")
                .enabled(true)
                .build();
        details1 = UserDetails.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .phoneNu("123456")
                .user(user1)
                .build();
        user1.setUserDetails(details1);

        user2 = User.builder()
                .email("alice.durand@test.com")
                .password("pass")
                .roles(new HashSet<>())
                .enabled(true)
                .build();
        UserDetails details2 = UserDetails.builder()
                .firstName("Alice")
                .lastName("Durand")
                .phoneNu("789012")
                .user(user2)
                .build();
        user2.setUserDetails(details2);
        userRepository.saveAll(List.of(user1, user2));

    }

    @Test
    void first(){
        List<User>users = userRepository.findAll();
        List<UserDetails>details = userDetailsRepository.findAll();

        assertThat(users.size()).isEqualTo(2);
        assertThat(details.size()).isEqualTo(2);
    }

    @DisplayName("Doit retourner les utilisateurs dont le nom ou prénom correspond à la recherche")
    @Test
    void getAllUser_shouldReturnUserOfTheSearchCriteria(){
        //Given:  sensible à la casse
        String search = "Ali";
        Pageable pageable = PageRequest.of(0,10);
        //When
        Page<User>pages1 = userRepository.getAllUser(search, pageable);
        //Then
        assertThat(pages1.getContent().size()).isEqualTo(1);
        assertThat(pages1.getContent()).extracting(u -> u.getUserDetails().getFirstName()).containsExactly("Alice");

        /*Other test*/
        //Given:  insensible à la casse
        search = "du";
        //When
        Page<User>pages2 = userRepository.getAllUser(search, pageable);
        //Then
        assertThat(pages2.getContent().size()).isEqualTo(2);
        assertThat(pages2.getContent()).extracting(u -> u.getUserDetails().getLastName()).containsExactlyInAnyOrder("Dupont", "Durand");
    }

    @DisplayName("Doit retourner le details d'un utilisateur qui correspond à la recherche")
    @Test
    void getEmployeeDetail_shouldReturnTheResultSuccessfully(){
        //Given
        Long userId = 1L;
        //When
        Optional<UserDetails>optional = userRepository.getEmployeeDetail(userId);
        //Then
        assertThat(optional.get()).isNotNull();
        assertThat(optional.get()).usingRecursiveComparison().comparingOnlyFields(
                "firstName","lastName","phoneNu"
        ).isEqualTo(details1);
        assertThat(optional.get().getFullName()).isEqualTo("Jean Dupont");
    }
}
