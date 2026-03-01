package com.alibou.stockmanage.suppliers.repositories;

import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    Page<Supplier>findByNameLikeIgnoreCase(String name, Pageable pageable);

    @Query("""
        select new com.alibou.stockmanage.suppliers.web.dto.SupplierDto(s.id, s.name) from Supplier s
    """)
    List<SupplierDto> getSupplierList();
}
