package com.alibou.stockmanage.products.web.controllers;

import com.alibou.stockmanage.products.services.ProductService;
import com.alibou.stockmanage.products.web.dtos.*;
import com.alibou.stockmanage.shared.dtos.GlobalResponse;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name="product-endpoints")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>create(
         @Valid @RequestBody ProductRequest request
    ){
        var productResponse = productService.create(request);
        if(Objects.nonNull(productResponse)){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("New product of ID %s was created successfully", productResponse.getId()))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/products")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<ProductResponse>>findAllProductBySearch(
         @RequestParam(name="search", defaultValue = "") String search,
         @RequestParam(name="page", defaultValue = "0")int page,
         @RequestParam(name="size", defaultValue = "6")int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(
                productService.findAllProductBySearch(search, pageable)
        );
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponse>findById(@PathVariable("productId") Long productId){
        var productResponse = productService.findById(productId);
        if(Objects.nonNull(productResponse)){
            return ResponseEntity.ok(productResponse);
        }
        return ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>deleteById(@PathVariable("productId") Long productId){
        boolean deleted = productService.deleteById(productId);
        if(deleted){
            return ResponseEntity.ok(
                        GlobalResponse.builder()
                            .message(String.format("Product of ID: %s was deleted successfully", productId))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @PutMapping("/products/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>update(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody ProductRequest request
    ){
        ProductResponse updated = productService.update(productId, request);
        if(Objects.nonNull(updated)){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Product of ID: %s was updated successfully", productId))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping(value = "/products/upload-photo", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>uploadFile(
            @RequestParam(name = "productId", required = true) Long productId,
            @Parameter
            @RequestPart(name="file", required = true) MultipartFile file
    ){
        var product = productService.uploadPhotosProduct(file, productId);
        if(Objects.nonNull(product)){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message("Image uploaded successfully")
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @PatchMapping("/products/update-status")
    @PreAuthorize("hasAnyAuthority('ADMIN','STOCK_MANAGER')")
    public ResponseEntity<GlobalResponse>updateStatus(
          @Valid @RequestBody  UpdateProductStatusRequest request
    ){
        var product = productService.updateStatusProduct(request);
        if(Objects.nonNull(product)){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Status of Product ID: %s was updated successfully", request.productId()))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/products/find-all-stock")
    @PreAuthorize("hasAnyAuthority('ADMIN','STOCK_MANAGER')")
    public ResponseEntity<PageResponse<StockResponse>>findAllStock(
            @RequestParam(name="search", defaultValue = "") String search,
            @RequestParam(name="page", defaultValue = "0")int page,
            @RequestParam(name="size", defaultValue = "6")int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.findAllStock(search, pageable));
    }

    @GetMapping("/products/find-all-category")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CategoryDto>> getCategoryList(){
        return ResponseEntity.ok(productService.getCategoryList());
    }

    @GetMapping("/products/find-all-supplier")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<SupplierDto>> getSupplierList(){
        return ResponseEntity.ok(productService.getSupplierList());
    }
}
