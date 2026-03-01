package com.alibou.stockmanage.products.mapper;

import com.alibou.stockmanage.products.models.Category;
import com.alibou.stockmanage.products.web.dtos.CategoryDto;
import org.springframework.beans.BeanUtils;

public class CategoryMapper {

    public static Category mapToEntity(CategoryDto dto){
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        return category;
    }

    public static CategoryDto mapToDto(Category category){
        CategoryDto categoryDto = new CategoryDto();
        BeanUtils.copyProperties(category, categoryDto);
        return categoryDto;
    }
}
