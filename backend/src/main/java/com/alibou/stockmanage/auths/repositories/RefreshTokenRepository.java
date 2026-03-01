package com.alibou.stockmanage.auths.repositories;

import com.alibou.stockmanage.auths.models.RefreshToken;
import com.alibou.stockmanage.auths.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken>findByToken(String token);

    @Query("""
        select t from RefreshToken t 
        where t.user.id = :userId and t.revoked = false
    """)
    List<RefreshToken>findAllNoRevokedTokensByUser(@Param("userId") Long userId);
}
