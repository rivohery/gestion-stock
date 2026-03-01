package com.alibou.stockmanage.auths.repositories;

import com.alibou.stockmanage.auths.models.Role;
import com.alibou.stockmanage.auths.models.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role>findByName(RoleEnum name);
    boolean existsByName(RoleEnum name);
}
