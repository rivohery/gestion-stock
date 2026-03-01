package com.alibou.stockmanage.products.repositories;

import com.alibou.stockmanage.products.models.Category;
import com.alibou.stockmanage.products.web.dtos.CategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByReference(String reference);
    boolean existsByName(String name);

    Category findByReference(String reference);

    @Query("select new com.alibou.stockmanage.products.web.dtos.CategoryDto(c.id, c.name) from Category c")
    List<CategoryDto>getCategoryList();
}
