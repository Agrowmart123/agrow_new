////package com.agrowmart.controller;
////
////import jakarta.validation.Valid;
////import org.springframework.security.access.prepost.PreAuthorize;
////import org.springframework.web.bind.annotation.*;
////
////import com.agrowmart.dto.auth.category.CategoryCreateDTO;
////import com.agrowmart.dto.auth.category.CategoryResponseDTO;
////import com.agrowmart.service.CategoryService;
////
////import java.util.List;
////
////@RestController
////@RequestMapping("/api/categories")
////public class CategoryController {
////
////    private final CategoryService categoryService;
////
////    public CategoryController(CategoryService categoryService) {
////        this.categoryService = categoryService;
////    }
////
////    @PostMapping
////    @PreAuthorize("hasAuthority('VENDOR')")
////    public CategoryResponseDTO create(@Valid @RequestBody CategoryCreateDTO dto) {
////        return categoryService.create(dto);
////    }
////
////    @GetMapping
////    public List<CategoryResponseDTO> list() {
////        return categoryService.listAll();
////    }
////
////    @GetMapping("/{id}")
////    public CategoryResponseDTO get(@PathVariable Long id) {
////        return categoryService.getById(id);
////    }
////
////    @PutMapping("/{id}")
////    @PreAuthorize("hasAuthority('VENDOR')")
////    public CategoryResponseDTO update(@PathVariable Long id, @Valid @RequestBody CategoryCreateDTO dto) {
////        return categoryService.update(id, dto);
////    }
////
////    @DeleteMapping("/{id}")
////    @PreAuthorize("hasAuthority('VENDOR')")
////    public void delete(@PathVariable Long id) {
////        categoryService.delete(id);
////    }
////}
//
//
//
//
package com.agrowmart.controller;

import com.agrowmart.dto.auth.category.CategoryCreateDTO;
import com.agrowmart.dto.auth.category.CategoryResponseDTO;
import com.agrowmart.entity.User;
import com.agrowmart.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE - Now with current user
    @PostMapping
    @PreAuthorize("hasAuthority('VENDOR')")
    public CategoryResponseDTO create(
            @Valid @RequestBody CategoryCreateDTO dto,
            @AuthenticationPrincipal User user) {
        return categoryService.create(dto, user);  // ← User pass kiya
    }

    @GetMapping
    public List<CategoryResponseDTO> list() {
        return categoryService.listAll();
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO get(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDOR')")
    public CategoryResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryCreateDTO dto,
            @AuthenticationPrincipal User user) {
        return categoryService.update(id, dto, user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDOR')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        categoryService.delete(id, user);
    }
    
 // In CategoryController.java
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('VEGETABLE','DAIRY','SEAFOODMEAT')")
    public List<CategoryResponseDTO> getMyCategories(
            @AuthenticationPrincipal(expression = "#this") User user) {
        
        if (user == null) {
            throw new RuntimeException("User not found in token");
        }
        
        return categoryService.getAllowedCategoriesForVendor(user);
    }
}


