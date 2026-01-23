package com.agrowmart.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.agrowmart.entity.Category;
import com.agrowmart.repository.CategoryRepository;

@Component
public class CategorySeeder implements CommandLineRunner {

    // Correct field name → lowercase 'c'
    private final CategoryRepository categoryRepo;
    public CategorySeeder(CategoryRepository categoryRepo) {  // ERROR 2
        this.categoryRepo = categoryRepo;                // Same galat name
    }

    @Override
    public void run(String... args) {
        createRootIfNotExists("vegetable-root", "Vegetables");
        createRootIfNotExists("seafoodmeat-root", "Seafood & Meat");
        createRootIfNotExists("dairy-root", "Dairy Products");
    }

    private void createRootIfNotExists(String slug, String name) {
        if (categoryRepo.findBySlug(slug).isEmpty()) {  // ab sahi variable
            Category root = new Category();
            root.setName(name);
            root.setSlug(slug);
            root.setParent(null);
            categoryRepo.save(root);                   // ab sahi variable
            System.out.println("Root category created: " + name + " (" + slug + ")");
        }
    }
}