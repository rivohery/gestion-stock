package com.alibou.stockmanage.auths.repositories;

import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    Optional<UserDetails>findByUser(User user);

    @Query("select ud from UserDetails ud where ud.user.id = :userId")
    Optional<UserDetails> getEmployeeDetail(@Param("userId") Long userId);

}
