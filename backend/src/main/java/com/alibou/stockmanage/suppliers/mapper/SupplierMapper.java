package com.alibou.stockmanage.suppliers.mapper;

import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import org.springframework.beans.BeanUtils;

public class SupplierMapper {

    public static SupplierDto mapToDto(Supplier supplier){
        SupplierDto dto = new SupplierDto();
        BeanUtils.copyProperties(supplier, dto);
        return dto;
    }

    public static Supplier mapToEntity(SupplierDto dto){
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(dto, supplier);
        return supplier;
    }
}
