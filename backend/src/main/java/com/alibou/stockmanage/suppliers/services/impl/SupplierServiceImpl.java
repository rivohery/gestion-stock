package com.alibou.stockmanage.suppliers.services.impl;

import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import com.alibou.stockmanage.suppliers.mapper.SupplierMapper;
import com.alibou.stockmanage.suppliers.models.Supplier;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import com.alibou.stockmanage.suppliers.services.SupplierService;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierDto create(SupplierDto dto) {
        boolean existsByName = supplierRepository.existsByName(dto.getName());
        if(existsByName){
            throw new OperationNotPermittedException("Name of supplier is already exist");
        }
        boolean existsByEmail = supplierRepository.existsByEmail(dto.getEmail());
        if(existsByEmail){
            throw new OperationNotPermittedException("Email of supplier is already exist");
        }

        var supplier = SupplierMapper.mapToEntity(dto);
        supplier.setCreatedAt(LocalDate.now());

        return SupplierMapper.mapToDto(supplierRepository.save(supplier));
    }

    @Override
    public PageResponse<SupplierDto> findAll(String search, Pageable pageable) {
        String searchLike = String.format("%%%s%%", search);
        Page<Supplier>pages = supplierRepository.findByNameLikeIgnoreCase(searchLike, pageable);

        List<SupplierDto>suppliers = pages
                .stream()
                .map(SupplierMapper::mapToDto)
                .toList();

        return new PageResponse<>(
                suppliers,
                pages.getNumber(),
                pages.getSize(),
                pages.getTotalElements(),
                pages.getTotalPages(),
                pages.isFirst(),
                pages.isLast()
        );
    }

    @Override
    public SupplierDto findById(Long supplierId) {
        return SupplierMapper.mapToDto(supplierRepository.findById(supplierId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Supplier of ID %s is not found", supplierId))
        ));
    }

    @Override
    public boolean deleteById(Long supplierId) {
        var supplier = supplierRepository.findById(supplierId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Supplier of ID %s is not found", supplierId))
        );
        supplierRepository.delete(supplier);
        return true;
    }

    @Override
    public SupplierDto update(Long supplierId, SupplierDto dto) {
        var supplier = supplierRepository.findById(supplierId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Supplier of ID %s is not found", supplierId))
        );
        supplier = SupplierMapper.mapToEntity(dto);
        try{
            supplier = supplierRepository.save(supplier);
            return SupplierMapper.mapToDto(supplier);
        } catch ( Exception ex){
            ex.printStackTrace();
            throw new PersistenceException(ex.getMessage());
        }
    }
}
