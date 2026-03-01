package com.alibou.stockmanage.products.services.impl;

import com.alibou.stockmanage.file.FileStorageService;
import com.alibou.stockmanage.products.mapper.ProductMapper;
import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.repositories.CategoryRepository;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.products.services.ProductService;
import com.alibou.stockmanage.products.web.dtos.*;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductRequest request) {
        boolean existsByName = productRepository.existsByName(request.name());
        if(existsByName){
            throw new OperationNotPermittedException("Name of product is already exists");
        }
        boolean existsByCode = productRepository.existsByCode(request.code());
        if(existsByCode){
            throw new OperationNotPermittedException("Code of product is already exists");
        }
        var product = productMapper.mapToProduct(request, null);
        product.setIsActive(false);
        product.setQtyStock(0);
        product = productRepository.save(product);
        return productMapper.mapToProductResponse(product);
    }

    @Override
    public ProductResponse findById(Long productId) {
        var product = productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No product found with ID: %s", productId))
        );

        return productMapper.mapToProductResponse(product);
    }

    @Override
    public boolean deleteById(Long productId) {
        var product = productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No product found with ID: %s", productId))
        );
        productRepository.delete(product);
        return true;
    }

    @Override
    public ProductResponse update(Long productId, ProductRequest request) {
        var product = productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No product found with ID: %s", productId))
        );
        product = productMapper.mapToProduct(request, product);
        try{
            product = productRepository.save(product);
            return productMapper.mapToProductResponse(product);
        }catch(Exception ex){
            ex.printStackTrace();
            throw new PersistenceException(ex.getMessage());
        }
    }

    @Override
    public PageResponse<ProductResponse> findAllProductBySearch(String search, Pageable pageable) {
        Page<Product>pages = productRepository.searchByNameLike(search, pageable);
        List<ProductResponse>productResponseList = pages
                .stream()
                .map(productMapper::mapToProductResponse)
                .toList();
        return new PageResponse(
                productResponseList,
                pages.getNumber(),
                pages.getSize(),
                pages.getTotalElements(),
                pages.getTotalPages(),
                pages.isFirst(),
                pages.isLast()
        );
    }

    @Override
    public Product uploadPhotosProduct(MultipartFile file, Long productId) {
        var product = productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No product found with ID: %s", productId))
        );
        String photoPath = fileStorageService.saveFile(file, productId, "products");
        if(StringUtils.isNotBlank(photoPath)){
            product.setPhotoPath(photoPath);
            return productRepository.save(product);
        }
        return null;
    }

    @Override
    public Product updateStatusProduct(UpdateProductStatusRequest request) {
        var product = productRepository.findById(request.productId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("No product found with ID: %s", request.productId()))
        );
        product.setIsActive(request.status());
        return productRepository.save(product);
    }

    @Override
    public PageResponse<StockResponse> findAllStock(String search, Pageable pageable) {
        Page<Product>pages = productRepository.searchByNameLike(search, pageable);
        List<StockResponse>stockResponseList = pages
                .stream()
                .map(productMapper::mapToStockResponse)
                .toList();
        return new PageResponse(
                stockResponseList,
                pages.getNumber(),
                pages.getSize(),
                pages.getTotalElements(),
                pages.getTotalPages(),
                pages.isFirst(),
                pages.isLast()
        );
    }

    @Override
    public List<CategoryDto> getCategoryList() {
        return categoryRepository.getCategoryList();
    }

    @Override
    public List<SupplierDto> getSupplierList() {
        return supplierRepository.getSupplierList();
    }
}
