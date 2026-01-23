//////package com.agrowmart.controller;
//////
//////import com.agrowmart.dto.auth.product.ProductResponseDTO;
//////import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
//////import com.agrowmart.dto.auth.category.CategoryResponseDTO;
//////import com.agrowmart.service.*;
//////import org.springframework.http.ResponseEntity;
//////import org.springframework.web.bind.annotation.*;
//////
//////import java.util.HashMap;
//////import java.util.List;
//////import java.util.Map;
//////import java.util.stream.Collectors;
//////
//////
//////
//////
//////
//////
//////@RestController
//////@RequestMapping("/api/public")
//////
//////public class PublicProductController {
//////
//////    private final ProductService productService;
//////    private final WomenProductService womenProductService;
//////    private final CategoryService categoryService;
//////    
//////    
//////
//////    
//////    public PublicProductController(ProductService productService,WomenProductService womenProductService,CategoryService categoryService) {
//////        this.productService = productService;
//////        this.womenProductService=womenProductService;
//////        this.categoryService=categoryService;
////////        this.doctorService=doctorService;
//////        
//////    }
//////
//////    // HOME PAGE — Sab products dikhao
//////    @GetMapping({"/", "/home", "/products"})
//////    public ResponseEntity<Map<String, Object>> getAllProducts(
//////            @RequestParam(required = false) String search) {
//////
//////        List<ProductResponseDTO> vendorProducts = productService.getAllActiveProducts();
//////        List<WomenProductResponseDTO> womenProducts = womenProductService.getAllWomenProducts();
//////
//////        // Search functionality
//////        if (search != null && !search.trim().isEmpty()) {
//////            String q = search.trim().toLowerCase();
//////            vendorProducts = vendorProducts.stream()
//////                    .filter(p -> p.productName() != null && p.productName().toLowerCase().contains(q))
//////                    .toList();
//////
//////            womenProducts = womenProducts.stream()
//////                    .filter(w -> w.name() != null && w.name().toLowerCase().contains(q))
//////                    .toList();
//////        }
//////
//////        Map<String, Object> data = Map.of(
//////                "vendorProducts", vendorProducts,
//////                "womenProducts",  womenProducts,
//////                "categories",     categoryService.listAll(),
//////                "total",          vendorProducts.size() + womenProducts.size(),
//////                "search",         search != null ? search : ""
//////        );
//////
//////        Map<String, Object> response = new HashMap<>();
//////        response.put("success", true);
//////        response.put("message", "Products loaded successfully");
//////        response.put("data", data);
//////        response.put("timestamp", java.time.Instant.now().toString());
//////
//////        return ResponseEntity.ok(response);
//////    }
//////
//////    // Categories
//////    @GetMapping("/categories")
//////    public List<CategoryResponseDTO> getCategories() {
//////        return categoryService.listAll();
//////    }
//////}
////
////
////
////// src/main/java/com/agrowmart/controller/PublicProductController.java
////
////package com.agrowmart.controller;
////
////import com.agrowmart.dto.auth.product.ProductFilterDTO;
////import com.agrowmart.dto.auth.product.ProductResponseDTO;
////import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
////import com.agrowmart.dto.auth.category.CategoryResponseDTO;
////import com.agrowmart.service.*;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////import java.util.stream.Collectors;
////
////@RestController
////@RequestMapping("/api/public")
////public class PublicProductController {
////
////    private final ProductService productService;
////    private final WomenProductService womenProductService;
////    private final CategoryService categoryService;
////
////    public PublicProductController(ProductService productService,
////                                   WomenProductService womenProductService,
////                                   CategoryService categoryService) {
////        this.productService = productService;
////        this.womenProductService = womenProductService;
////        this.categoryService = categoryService;
////    }
////
////    // Existing home endpoint
////    @GetMapping({"/", "/home", "/products"})
////    public ResponseEntity<Map<String, Object>> getAllProducts(
////            @RequestParam(required = false) String search) {
////        List<ProductResponseDTO> vendorProducts = productService.getAllActiveProducts();
////        List<WomenProductResponseDTO> womenProducts = womenProductService.getAllWomenProducts();
////        // Search functionality
////        if (search != null && !search.trim().isEmpty()) {
////            String q = search.trim().toLowerCase();
////            vendorProducts = vendorProducts.stream()
////                    .filter(p -> p.productName() != null && p.productName().toLowerCase().contains(q))
////                    .toList();
////            womenProducts = womenProducts.stream()
////                    .filter(w -> w.name() != null && w.name().toLowerCase().contains(q))
////                    .toList();
////        }
////        Map<String, Object> data = Map.of(
////                "vendorProducts", vendorProducts,
////                "womenProducts", womenProducts,
////                "categories", categoryService.listAll(),
////                "total", vendorProducts.size() + womenProducts.size(),
////                "search", search != null ? search : ""
////        );
////        Map<String, Object> response = new HashMap<>();
////        response.put("success", true);
////        response.put("message", "Products loaded successfully");
////        response.put("data", data);
////        response.put("timestamp", java.time.Instant.now().toString());
////        return ResponseEntity.ok(response);
////    }
////
////    // NEW FILTER API - Matches Image Exactly
////    @GetMapping("/filtered-products")
////    public ResponseEntity<List<ProductResponseDTO>> getFilteredProducts(
////            @RequestParam(required = false) String sortBy,          // price_low_high, price_high_low, rating_high_low, rating_low_high
////            @RequestParam(required = false) List<String> categories, // vegetable_fruits, seafood_meat, local_trial, handcrafts
////            @RequestParam(required = false) Boolean inStock,       // true/false
////            @RequestParam(required = false) Double lat,            // User latitude for distance
////            @RequestParam(required = false) Double lon,            // User longitude for distance
////            @RequestParam(required = false) String distanceFilter  // nearest, around_5kms
////    ) {
////        ProductFilterDTO filter = new ProductFilterDTO(sortBy, categories, inStock, lat, lon, distanceFilter);
////        List<ProductResponseDTO> products = productService.getFilteredProducts(filter);
////        return ResponseEntity.ok(products);
////    }
////
////    @GetMapping("/categories")
////    public List<CategoryResponseDTO> getCategories() {
////        return categoryService.listAll();
////    }
////}
//
//
//
//// src/main/java/com/agrowmart/controller/PublicProductController.java
//
//package com.agrowmart.controller;
//
//import com.agrowmart.dto.auth.product.ProductFilterDTO;
//import com.agrowmart.dto.auth.product.ProductResponseDTO;
//import com.agrowmart.dto.auth.shop.ShopResponse;
//import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
//import com.agrowmart.dto.auth.category.CategoryResponseDTO;
//import com.agrowmart.service.ProductService;
//import com.agrowmart.service.ShopService;
//import com.agrowmart.service.WomenProductService;
//import com.agrowmart.service.CategoryService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/public")
//public class PublicProductController {
//
//    private final ProductService productService;
//    private final WomenProductService womenProductService;
//    private final CategoryService categoryService;
//    private final ShopService shopService; // inject in constructor
//
//
//    public PublicProductController(ProductService productService,
//                                   WomenProductService womenProductService,
//                                   CategoryService categoryService,
//                                   ShopService shopService ) {
//        this.productService = productService;
//        this.womenProductService = womenProductService;
//        this.categoryService = categoryService;
//        this.shopService=shopService;
//    }
//
//    // Home Page - All Products + Search
//    @GetMapping({"/", "/home", "/products"})
//    public ResponseEntity<Map<String, Object>> getAllProducts(
//            @RequestParam(required = false) String search) {
//        List<ProductResponseDTO> vendorProducts = productService.getAllActiveProducts();
//        List<WomenProductResponseDTO> womenProducts = womenProductService.getAllWomenProducts();
//
//        if (search != null && !search.trim().isEmpty()) {
//            String q = search.trim().toLowerCase();
//            vendorProducts = vendorProducts.stream()
//                    .filter(p -> p.productName() != null && p.productName().toLowerCase().contains(q))
//                    .toList();
//            womenProducts = womenProducts.stream()
//                    .filter(w -> w.name() != null && w.name().toLowerCase().contains(q))
//                    .toList();
//        }
//
//        Map<String, Object> data = Map.of(
//                "vendorProducts", vendorProducts,
//                "womenProducts", womenProducts,
//                "categories", categoryService.listAll(),
//                "total", vendorProducts.size() + womenProducts.size(),
//                "search", search != null ? search : ""
//        );
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Products loaded successfully");
//        response.put("data", data);
//        response.put("timestamp", java.time.Instant.now().toString());
//
//        return ResponseEntity.ok(response);
//    }
//
//    // FULL FILTER API - All filters working (NO Distance filter)
//    @GetMapping("/filtered-products")
//    public ResponseEntity<List<Map<String, Object>>> getFilteredProducts(
//            @RequestParam(required = false) String sortBy,           // price_low_high, price_high_low, rating_high_low, rating_low_high
//            @RequestParam(required = false) List<String> categories, // e.g., Vegetables & Fruits, Seafood & Meat
//            @RequestParam(required = false) Boolean inStock         // true = show only in stock
//    ) {
//        // Create filter without lat/lon/distanceFilter
//        ProductFilterDTO filter = new ProductFilterDTO(sortBy, categories, inStock, null, null, null);
//
//        // Get filtered regular products
//        List<ProductResponseDTO> regularProducts = productService.getFilteredProducts(filter);
//
//        // Get filtered women products
//        List<WomenProductResponseDTO> womenProducts = womenProductService.getFilteredProducts(filter);
//
//        // Combine into unified response
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        regularProducts.forEach(p -> {
//            Map<String, Object> item = new HashMap<>();
//            item.put("type", "regular");
//            item.put("data", p);
//            result.add(item);
//        });
//
//        womenProducts.forEach(w -> {
//            Map<String, Object> item = new HashMap<>();
//            item.put("type", "women");
//            item.put("data", w);
//            result.add(item);
//        });
//
//        return ResponseEntity.ok(result);
//    }
//
//    @GetMapping("/categories")
//    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
//        return ResponseEntity.ok(categoryService.listAll());
//    }
//    
//    
// 
//    @GetMapping("/popular-shops")
//    public ResponseEntity<Map<String, Object>> getPopularShops() {
//        List<ShopResponse> shops = shopService.getPopularShops();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Popular shops loaded");
//        response.put("data", shops);
//        response.put("total", shops.size());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/top10-popular-shops")
//    public ResponseEntity<List<ShopResponse>> getTop10PopularShops() {
//        return ResponseEntity.ok(shopService.getTop10PopularShops());
//    }
//}


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
    	
    	List<ProductResponseDTO> regular = productService.getPublicProducts();
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