////
////package com.agrowmart.service;
////
////import com.agrowmart.dto.auth.category.CategoryCreateDTO;
////import com.agrowmart.dto.auth.category.CategoryResponseDTO;
////import com.agrowmart.entity.Category;
////import com.agrowmart.entity.User;
////import com.agrowmart.exception.ForbiddenException;
////import com.agrowmart.exception.ResourceNotFoundException;
////import com.agrowmart.repository.CategoryRepository;
////import lombok.RequiredArgsConstructor;
////import org.springframework.stereotype.Service;
////import org.springframework.transaction.annotation.Transactional;
////
////import java.util.List;
////
////@Service
////@RequiredArgsConstructor
////@Transactional
////public class CategoryService {
////
////    private final CategoryRepository categoryRepo;
////    public CategoryService(CategoryRepository categoryRepo
////          
////         ) {
//// this.categoryRepo = categoryRepo;
////
////
//// }
////
////    // CREATE - With Role Restriction
////    public CategoryResponseDTO create(CategoryCreateDTO dto, User currentUser) {
////        String role = currentUser.getRole().getName();
////
////        // Define allowed root for restricted vendors
////        String allowedRootSlug = switch (role) {
////            case "VEGETABLE"   -> "vegetable-root";
////            case "SEAFOODMEAT" -> "seafoodmeat-root";
////            case "DAIRY"       -> "dairy-root";
////            default            -> null; // WOMEN, DOCTOR → no restriction
////        };
////
////        Category category = new Category();
////        category.setName(dto.name());
////        category.setSlug(dto.slug());
////
////        // If parentId is provided → check if it's allowed
////        if (dto.parentId() != null) {
////            Category parent = categoryRepo.findById(dto.parentId())
////                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
////
////            // Apply restriction only for restricted roles
////            if (allowedRootSlug != null) {
////                if (!isUnderRoot(parent, allowedRootSlug)) {
////                    throw new ForbiddenException(
////                        "You can only create sub-categories in your own section!"
////                    );
////                }
////            }
////            category.setParent(parent);
////        } else {
////            // If no parent → it's a root category → only WOMEN/DOCTOR can create root
////            if (allowedRootSlug != null) {
////                throw new ForbiddenException("You cannot create root categories. Only allowed in your section.");
////            }
////        }
////
////        category = categoryRepo.save(category);
////        return toDto(category);
////    }
////
////    public List<CategoryResponseDTO> listAll() {
////        return categoryRepo.findAll().stream()
////                .map(this::toDto)
////                .toList();
////    }
////
////    public CategoryResponseDTO getById(Long id) {
////        Category cat = categoryRepo.findById(id)
////                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
////        return toDto(cat);
////    }
////
////    public CategoryResponseDTO update(Long id, CategoryCreateDTO dto, User user) {
////        Category cat = categoryRepo.findById(id)
////                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
////
////        // Optional: Allow only if category belongs to user's section
////        String role = user.getRole().getName();
////        String allowedRoot = switch (role) {
////            case "VEGETABLE"   -> "vegetable-root";
////            case "SEAFOODMEAT" -> "seafoodmeat-root";
////            case "DAIRY"       -> "dairy-root";
////            default            -> null;
////        };
////        
////        
////
////        if (allowedRoot != null && !isUnderRoot(cat, allowedRoot)) {
////            throw new ForbiddenException("You can only update categories in your section");
////        }
////
////        cat.setName(dto.name());
////        cat.setSlug(dto.slug());
////
////        if (dto.parentId() != null) {
////            Category parent = categoryRepo.findById(dto.parentId())
////                    .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
////            cat.setParent(parent);
////        } else {
////            cat.setParent(null);
////        }
////
////        cat = categoryRepo.save(cat);
////        return toDto(cat);
////    }
////
////    public void delete(Long id, User user) {
////        Category cat = categoryRepo.findById(id)
////                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
////
////        String role = user.getRole().getName();
////        String allowedRoot = switch (role) {
////            case "VEGETABLE"   -> "vegetable-root";
////            case "SEAFOODMEAT" -> "seafoodmeat-root";
////            case "DAIRY"       -> "dairy-root";
////            default            -> null;
////        };
////
////        if (allowedRoot != null && !isUnderRoot(cat, allowedRoot)) {
////            throw new ForbiddenException("You can only delete categories from your section");
////        }
////
////        categoryRepo.delete(cat);
////    }
////
////    // Helper: Check if category is under a root
////    private boolean isUnderRoot(Category category, String rootSlug) {
////        Category current = category;
////        while (current != null) {
////            if (rootSlug.equals(current.getSlug())) {
////                return true;
////            }
////            current = current.getParent();
////        }
////        return false;
////    }
////
////    private CategoryResponseDTO toDto(Category c) {
////        Long parentId = c.getParent() != null ? c.getParent().getId() : null;
////        return new CategoryResponseDTO(c.getId(), c.getName(), c.getSlug(), parentId);
////    }
////}
//
//
////------------------------
package com.agrowmart.service;

import com.agrowmart.dto.auth.category.CategoryCreateDTO;
import com.agrowmart.dto.auth.category.CategoryResponseDTO;
import com.agrowmart.entity.Category;
import com.agrowmart.entity.User;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.CategoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    // Helper to determine the required root slug based on role
    private String determineRequiredRootSlug(String role) {
        return switch (role) {
            case "VEGETABLE"   -> "vegetable-root";
            case "SEAFOODMEAT" -> "seafoodmeat-root";
            case "DAIRY"       -> "dairy-root";
            default            -> null; // For WOMEN, FARMER, etc.
        };
    }

    // CREATE - With Role Restriction and Automatic Parent Assignment
//    public CategoryResponseDTO create(CategoryCreateDTO dto, User currentUser) {
//        String role = currentUser.getRole().getName();
//        String requiredRootSlug = determineRequiredRootSlug(role);
//        Long finalParentId = dto.parentId();
//
//        // === AUTO-ASSIGNMENT LOGIC for Restricted Vendors ===
//        if (finalParentId == null && requiredRootSlug != null) {
//            // User is a restricted vendor AND no parentId was provided.
//            Category rootCategory = categoryRepo.findBySlug(requiredRootSlug)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                    "System error: Required root category '" + requiredRootSlug + "' not found. Run seeder."
//                ));
//            finalParentId = rootCategory.getId(); // Automatically set the Parent ID to the root ID
//            System.out.println("AUTO-ASSIGN: User '" + role + "' assigned parent ID: " + finalParentId);
//        }
//        // === END AUTO-ASSIGNMENT LOGIC ===
//
//
//        Category category = new Category();
//        category.setName(dto.name());
//        category.setSlug(dto.slug());
//        
//        // --- Parent Assignment and Validation ---
//        if (finalParentId != null) {
//            Category parent = categoryRepo.findById(finalParentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
//
//            // Apply restriction check using the determined or default slug
//            if (requiredRootSlug != null) {
//                if (!isUnderRoot(parent, requiredRootSlug)) {
//                    throw new ForbiddenException(
//                        "You can only create categories under the '" + requiredRootSlug + "' section."
//                    );
//                }
//            }
//            category.setParent(parent);
//        } else {
//            // Case: finalParentId is null AND requiredRootSlug is null (WOMEN/FARMER creating a new root category)
//            if (requiredRootSlug != null) {
//                 // Should be unreachable due to auto-assignment, but kept for logical completeness.
//                throw new ForbiddenException("You cannot create root categories directly.");
//            }
//            category.setParent(null);
//        }
//
//        category = categoryRepo.save(category);
//        return toDto(category);
//    }

    
    
    public CategoryResponseDTO create(CategoryCreateDTO dto, User currentUser) {
        String role = currentUser.getRole().getName();
        String requiredRootSlug = determineRequiredRootSlug(role); // vegetable-root, etc. or null

        Long parentIdToUse = dto.parentId();

        // === AUTO-ASSIGN PARENT FOR RESTRICTED VENDORS (VEGETABLE, SEAFOODMEAT, DAIRY) ===
        if (requiredRootSlug != null && parentIdToUse == null) {
            // Find the root category for this vendor type
            Category rootCategory = categoryRepo.findBySlug(requiredRootSlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "System error: Required root category '" + requiredRootSlug + "' not found. Please run seeder."
                ));
            parentIdToUse = rootCategory.getId();
            System.out.println("AUTO-ASSIGNED parent for " + role + ": " + parentIdToUse);
        }

        // === STRICT RULE: parentId MUST be provided now (either by user or auto-assigned) ===
        if (parentIdToUse == null) {
            throw new ForbiddenException("You must specify a parent category. Creating root categories is not allowed.");
        }

        // Load the parent
        Category parent = categoryRepo.findById(parentIdToUse)
            .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));

        // === RESTRICTED VENDORS: Ensure parent is under their allowed root ===
        if (requiredRootSlug != null) {
            if (!isUnderRoot(parent, requiredRootSlug)) {
                throw new ForbiddenException(
                    "You can only create categories under your section ('" + requiredRootSlug + "')."
                );
            }
        }
        // Note: Unrestricted roles (WOMEN, FARMER, etc.) can choose any existing parent → allowed

        // Create the new category
        Category category = new Category();
        category.setName(dto.name());
        category.setSlug(dto.slug());
        category.setParent(parent);

        category = categoryRepo.save(category);
        return toDto(category);
    }
    
    public List<CategoryResponseDTO> listAll() {
        return categoryRepo.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    public CategoryResponseDTO getById(Long id) {
        Category cat = categoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return toDto(cat);
    }

    public CategoryResponseDTO update(Long id, CategoryCreateDTO dto, User user) {
        Category cat = categoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Determine allowed root for modification validation
        String allowedRoot = determineRequiredRootSlug(user.getRole().getName());

        if (allowedRoot != null && !isUnderRoot(cat, allowedRoot)) {
            throw new ForbiddenException("You can only update categories in your section");
        }

        cat.setName(dto.name());
        cat.setSlug(dto.slug());

        if (dto.parentId() != null) {
            Category parent = categoryRepo.findById(dto.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
            
            // Re-validate parent hierarchy during update
             if (allowedRoot != null && !isUnderRoot(parent, allowedRoot)) {
                 throw new ForbiddenException("Cannot move category outside of your designated section.");
             }
            cat.setParent(parent);
        } else {
             // Only allow setting parent to null if the user is unrestricted, or if it's already a root that the user created.
             if (allowedRoot != null && cat.getParent() != null) {
                 throw new ForbiddenException("Restricted vendors cannot detach a subcategory to make it a root category.");
             }
            cat.setParent(null);
        }

        cat = categoryRepo.save(cat);
        return toDto(cat);
    }

    public void delete(Long id, User user) {
        Category cat = categoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Determine allowed root for deletion validation
        String allowedRoot = determineRequiredRootSlug(user.getRole().getName());

        if (allowedRoot != null && !isUnderRoot(cat, allowedRoot)) {
            throw new ForbiddenException("You can only delete categories from your section");
        }

        categoryRepo.delete(cat);
    }

    // Helper: Check if category is under a root
    private boolean isUnderRoot(Category category, String rootSlug) {
        Category current = category;
        while (current != null) {
            if (rootSlug.equals(current.getSlug())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private CategoryResponseDTO toDto(Category c) {
        Long parentId = c.getParent() != null ? c.getParent().getId() : null;
        return new CategoryResponseDTO(c.getId(), c.getName(), c.getSlug(), parentId);
    }
    
    
    
 // In CategoryService.java

    public List<CategoryResponseDTO> getAllowedCategoriesForVendor(User currentUser) {
        String role = currentUser.getRole().getName();
        String requiredRootSlug = determineRequiredRootSlug(role);

        // Unrestricted roles (e.g., WOMEN, FARMER, ADMIN) can see ALL categories
        if (requiredRootSlug == null) {
            return listAll(); // They can use any category
        }

        // Restricted vendors (VEGETABLE, DAIRY, SEAFOODMEAT) → only under their root
        Category root = categoryRepo.findBySlug(requiredRootSlug)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Root category not found for your role. Contact admin."
            ));

        List<Category> allowed = new ArrayList<>();
        collectSubcategories(root, allowed);

        return allowed.stream()
            .map(this::toDto)
            .toList();
    }

    private void collectSubcategories(Category category, List<Category> result) {
        result.add(category); // Include the root itself
        for (Category child : category.getChildren()) {
            collectSubcategories(child, result);
        }
    }
}

