package com.alibou.stockmanage.products.mapper;

import com.alibou.stockmanage.file.FileUtils;
import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.repositories.CategoryRepository;
import com.alibou.stockmanage.products.web.dtos.AlertEnum;
import com.alibou.stockmanage.products.web.dtos.ProductRequest;
import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import com.alibou.stockmanage.products.web.dtos.StockResponse;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public Product mapToProduct(
            @NonNull ProductRequest request,
            @Nullable  Product product
    ){
        if(Objects.isNull(product)){
            product = new Product();
        }
        BeanUtils.copyProperties(request, product);
        var category = categoryRepository.findById(request.categoryId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category of ID %s is not found", request.categoryId()))
        );
        product.setCategory(category);
        var supplier = supplierRepository.findById(request.supplierId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Supplier of ID %s is not found", request.supplierId()))
        );
        product.setSupplier(supplier);
        return product;
    }

    public ProductResponse mapToProductResponse(Product product){
        var productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        productResponse.setCategoryId(product.getCategory().getId());
        productResponse.setCategoryName(product.getCategory().getName());
        productResponse.setSupplierId(product.getSupplier().getId());
        productResponse.setSupplierName(product.getSupplier().getName());
        if(StringUtils.isNotBlank(product.getPhotoPath())){
            productResponse.setPhotos(FileUtils.readFileFromLocation(product.getPhotoPath()));
        }
        return productResponse;
    }

    public StockResponse mapToStockResponse(Product product){
        var stockResponse = new StockResponse();
        BeanUtils.copyProperties(product, stockResponse);
        stockResponse.setAlert(product.getQtyStock() <= product.getAlertStock() ? AlertEnum.ALERT : AlertEnum.OK);
        if(StringUtils.isNotBlank(product.getPhotoPath())){
            stockResponse.setPhotos(FileUtils.readFileFromLocation(product.getPhotoPath()));
        }
        return stockResponse;
    }


}
