package com.alibou.stockmanage.products.web.controllers;

import com.alibou.stockmanage.products.services.CategoryService;
import com.alibou.stockmanage.products.web.dtos.CategoryDto;
import com.alibou.stockmanage.shared.dtos.GlobalResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Category-endpoint")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CategoryDto> findById(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(categoryService.findById(categoryId));
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> create(
            @RequestBody @Valid CategoryDto dto
    ) {
        return ResponseEntity.ok(categoryService.create(dto));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryDto> update(
            @PathVariable("categoryId")Long categoryId,
            @Valid @RequestBody CategoryDto dto
    ) {
        CategoryDto response = categoryService.update(categoryId, dto);
        if(Objects.nonNull(response)){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<GlobalResponse> deleteById(@PathVariable("categoryId")Long categoryId) {
        var response = categoryService.deleteById(categoryId);
        if(response){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Category of ID: %s was deleted", categoryId))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

}
