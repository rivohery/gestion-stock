package com.alibou.stockmanage.suppliers.web.controller;

import com.alibou.stockmanage.shared.dtos.GlobalResponse;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.suppliers.services.SupplierService;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name="supplier-endpoint")
public class SupplierController {
    private final SupplierService service;

    @PostMapping("/suppliers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse> create(
        @RequestBody @Valid SupplierDto dto
    ) {
        var supplierDto = service.create(dto);
        if(Objects.nonNull(supplierDto)){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Supplier was created successfully,ID: %s", supplierDto.getId()))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/suppliers")
    public ResponseEntity<PageResponse<SupplierDto>> findAll(
           @RequestParam(name = "search", defaultValue = "") String search,
           @RequestParam(name = "page", defaultValue = "0") int page,
           @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.findAll(search, pageable));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<SupplierDto> findById(@PathVariable("supplierId") Long supplierId) {
        return ResponseEntity.ok(service.findById(supplierId));
    }

    @DeleteMapping("/suppliers/{supplierId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse> deleteById(@PathVariable("supplierId") Long supplierId) {
        boolean deleted = service.deleteById(supplierId);
        if(deleted){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Supplier of ID %s was deleted successfully", supplierId))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @PutMapping("/suppliers/{supplierId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse> update(
            @PathVariable("supplierId") Long supplierId,
            @Valid @RequestBody SupplierDto dto
    ) {
        SupplierDto updated = service.update(supplierId, dto);
        if(Objects.nonNull(updated)){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Supplier of ID %s was updated successfully", updated.getId()))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

}
