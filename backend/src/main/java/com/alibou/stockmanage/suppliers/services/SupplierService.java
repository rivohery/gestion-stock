package com.alibou.stockmanage.suppliers.services;

import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import org.springframework.data.domain.Pageable;

public interface SupplierService {
    SupplierDto create(SupplierDto dto);
    PageResponse<SupplierDto> findAll(String search, Pageable pageable);
    SupplierDto findById(Long supplierId);
    boolean deleteById(Long supplierId);

    SupplierDto update(Long supplierId, SupplierDto dto);

}
