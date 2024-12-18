package com.bekrenovr.ecommerce.catalog.category;

import com.bekrenovr.ecommerce.catalog.category.subcategory.SubcategoryResponse;
import com.bekrenovr.ecommerce.catalog.item.filters.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/images")
    public ResponseEntity<Map<UUID, byte[]>> getAllCategoryImages(@RequestParam Gender gender){
        return ResponseEntity.ok(categoryService.getAllCategoryImages(gender));
    }

    @GetMapping("/{categoryId}/subcategories")
    public ResponseEntity<List<SubcategoryResponse>> getAllSubcategoriesInCategory(@PathVariable UUID categoryId){
        return ResponseEntity.ok(categoryService.getAllSubcategoriesInCategory(categoryId));
    }
}
