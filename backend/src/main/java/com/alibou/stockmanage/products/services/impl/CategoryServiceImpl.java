package com.alibou.stockmanage.products.services.impl;

import com.alibou.stockmanage.products.mapper.CategoryMapper;
import com.alibou.stockmanage.products.models.Category;
import com.alibou.stockmanage.products.repositories.CategoryRepository;
import com.alibou.stockmanage.products.services.CategoryService;
import com.alibou.stockmanage.products.web.dtos.CategoryDto;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::mapToDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long categoryId) {
        var category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category of ID: %s is not found", categoryId))
        );
        return CategoryMapper.mapToDto(category);
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        var isCategoryNameExist = categoryRepository.existsByName(dto.getName());
        if(isCategoryNameExist){
            throw new OperationNotPermittedException("Category name is already exist");
        }
        var isCategoryReferenceExist = categoryRepository.existsByReference(dto.getReference());
        if(isCategoryReferenceExist){
            throw new OperationNotPermittedException("Category reference is already exist");
        }
        Category category = categoryRepository.save(CategoryMapper.mapToEntity(dto));
        return CategoryMapper.mapToDto(category);
    }

    @Override
    public CategoryDto update(Long categoryId, CategoryDto dto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category of ID: %s not found", dto.getId()))
        );
        category.setName(dto.getName());
        category.setReference(dto.getReference());
        return CategoryMapper.mapToDto(categoryRepository.save(category));
    }

    @Override
    public boolean deleteById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category of ID: %s not found", id))
        );
        categoryRepository.delete(category);
        return true;
    }
}
