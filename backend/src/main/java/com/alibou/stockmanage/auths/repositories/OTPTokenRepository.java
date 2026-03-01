package com.alibou.stockmanage.auths.repositories;

import com.alibou.stockmanage.auths.models.OTPToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPTokenRepository extends JpaRepository<OTPToken, Long> {
    Optional<OTPToken> findByToken(String token);
}
