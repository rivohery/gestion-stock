package com.alibou.stockmanage.products.services;

import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.web.dtos.*;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.suppliers.web.dto.SupplierDto;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);

    ProductResponse findById(Long productId);

    boolean deleteById(Long productId);

    ProductResponse update(Long productId, ProductRequest request);

    PageResponse<ProductResponse>findAllProductBySearch(String search, Pageable pageable);

    Product uploadPhotosProduct(@NonNull MultipartFile file,@NonNull Long productId);

    Product updateStatusProduct(UpdateProductStatusRequest request);

    PageResponse<StockResponse> findAllStock(String search, Pageable pageable);

    List<CategoryDto> getCategoryList();

    List<SupplierDto> getSupplierList();

}
