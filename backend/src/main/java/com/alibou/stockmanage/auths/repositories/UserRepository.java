package com.alibou.stockmanage.auths.repositories;

import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"userDetails"})
    @Query("""
        Select u from User u 
        where lower(u.userDetails.lastName)  like lower(concat('%', :search, '%')) 
        or lower(u.userDetails.firstName) like lower(concat('%', :search, '%'))
    """)
    Page<User>fetchAllUserBySearch(String search, Pageable pageable);
}


