package com.alibou.stockmanage.home.impl;

import com.alibou.stockmanage.home.HomeService;
import com.alibou.stockmanage.products.mapper.ProductMapper;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> checkListProduct() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::mapToProductResponse)
                .toList();
    }
}
