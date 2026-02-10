

package com.agrowmart.controller;

import com.agrowmart.dto.auth.product.ProductFilterDTO;
import com.agrowmart.dto.auth.product.ProductResponseDTO;
import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
import com.agrowmart.dto.auth.category.CategoryResponseDTO;
import com.agrowmart.dto.auth.shop.ShopResponse;
import com.agrowmart.service.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/public")
public class PublicProductController {

    private final ProductService productService;
    private final WomenProductService womenProductService;
    private final CategoryService categoryService;
    private final ShopService shopService;

    public PublicProductController(ProductService productService,
                                   WomenProductService womenProductService,
                                   CategoryService categoryService,
                                   ShopService shopService) {
        this.productService = productService;
        this.womenProductService = womenProductService;
        this.categoryService = categoryService;
        this.shopService = shopService;
    }

    // HOME PAGE
    @GetMapping({"/", "/home", "/products"})
    public ResponseEntity<Map<String, Object>> getHomeData(
            @RequestParam(required = false) String search) {

       // List<ProductResponseDTO> regular = productService.getAllActiveProducts();
    	
    // 21 Jan
    	
    	List<ProductResponseDTO> regular = productService.getAllActiveProducts();
// 
     // FIXED: Use active-only method
        List<WomenProductResponseDTO> women = womenProductService.getAllActiveWomenProducts();
        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            regular = regular.stream()
                    .filter(p -> p.productName().toLowerCase().contains(q))
                    .toList();
            women = women.stream()
                    .filter(w -> w.name().toLowerCase().contains(q))
                    .toList();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("regularProducts", regular);
        data.put("womenProducts", women);
        data.put("categories", categoryService.listAll());
        data.put("total", regular.size() + women.size());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Home data loaded");
        response.put("data", data);
        response.put("timestamp", new Date());

        return ResponseEntity.ok(response);
    }

    // MOST POPULAR SHOPS
    @GetMapping("/popular-shops")
    public ResponseEntity<Map<String, Object>> getPopularShops() {
        List<ShopResponse> shops = shopService.getPopularShops();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", shops);
        response.put("total", shops.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top10-popular-shops")
    public ResponseEntity<List<ShopResponse>> getTop10PopularShops() {
        return ResponseEntity.ok(shopService.getTop10PopularShops());
    }

    // RECENTLY ADDED PRODUCTS
    @GetMapping("/recently-added")
    public ResponseEntity<Map<String, Object>> getRecentlyAdded(
            @RequestParam(defaultValue = "20") int limit) {

        //List<ProductResponseDTO> recentRegular = productService.getRecentlyAddedProducts(limit / 2 + 5);
    	
    	// 21 Jan 
        List<ProductResponseDTO> recentRegular = productService.getRecentlyAddedPublicProducts(limit / 2 + 5);
     // This method already filters active
     // This method already filters active
        List<WomenProductResponseDTO> recentWomen = womenProductService.getRecentlyAddedWomenProducts(limit / 2 + 5);      List<Map<String, Object>> combined = new ArrayList<>();

        recentRegular.forEach(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "regular");
            item.put("data", p);
            combined.add(item);
        });

        recentWomen.forEach(w -> {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "women");
            item.put("data", w);
            combined.add(item);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Recently added products");
        response.put("data", combined);
        response.put("total", combined.size());

        return ResponseEntity.ok(response);
    }

    // FILTERED PRODUCTS
    @GetMapping("/filtered-products")
    public ResponseEntity<List<Map<String, Object>>> getFilteredProducts(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Boolean inStock) {

        ProductFilterDTO filter = new ProductFilterDTO(sortBy, categories, inStock, null, null, null);

        List<ProductResponseDTO> regular = productService.getFilteredProducts(filter);
        List<WomenProductResponseDTO> women = womenProductService.getFilteredProducts(filter);

        List<Map<String, Object>> result = new ArrayList<>();

        regular.forEach(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "regular");
            item.put("data", p);
            result.add(item);
        });

        women.forEach(w -> {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "women");
            item.put("data", w);
            result.add(item);
        });

        return ResponseEntity.ok(result);
    }

    // CATEGORIES
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.listAll());
    }
    
    
    // 21 Jan 
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable Long id) throws Exception {
        ProductResponseDTO product = productService.getPublicProductById(id);
        return ResponseEntity.ok(product);
    }
}