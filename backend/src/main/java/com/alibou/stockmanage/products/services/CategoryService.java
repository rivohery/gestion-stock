package com.alibou.stockmanage.products.services;

import com.alibou.stockmanage.products.web.dtos.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto>findAll();
    CategoryDto findById(Long categoryId);
    CategoryDto create(CategoryDto dto);
    CategoryDto update(Long categoryId, CategoryDto dto);
    boolean deleteById(Long id);
}
