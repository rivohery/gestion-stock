package com.alibou.stockmanage.auths.repositories;

import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("""
        Select ud.user from UserDetails ud 
        where lower(ud.lastName)  like lower(concat('%', :search, '%')) 
        or lower(ud.firstName) like lower(concat('%', :search, '%'))
    """)
    Page<User>getAllUser(@Param("search") String search, Pageable pageable);

    @Query("select ud from UserDetails ud where ud.user.id = :userId")
    Optional<UserDetails> getEmployeeDetail(@Param("userId") Long userId);
}


