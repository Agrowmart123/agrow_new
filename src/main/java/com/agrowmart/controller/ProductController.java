//package com.agrowmart.controller;
//
//import jakarta.validation.Valid;
//import org.springframework.data.domain.Page;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import com.agrowmart.dto.auth.product.*;
//import com.agrowmart.entity.User;
//import com.agrowmart.service.ProductService;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/products")
//public class ProductController {
//
//    private final ProductService productService;
//
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }
//
//    /** ✅ Create Product (multipart form-data) */
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('VENDOR')")
//    public ProductResponseDTO create(
//            @Valid @ModelAttribute ProductCreateDTO dto,
//            @AuthenticationPrincipal User user) throws IOException {
//        return productService.create(dto, user.getId());
//    }
//
//    /** ✅ Update Product (multipart form-data) */
//    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('VENDOR')")
//    public ProductResponseDTO update(
//            @PathVariable Long id,
//            @Valid @ModelAttribute ProductUpdateDTO dto,
//            @AuthenticationPrincipal User user) throws IOException {
//        return productService.update(id, dto, user.getId());
//    }
//
//    /** 🗑️ Delete Product */
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('VENDOR')")
//    public ResponseEntity<Void> delete(
//            @PathVariable Long id,
//            @AuthenticationPrincipal User user) {
//        productService.delete(id, user.getId());
//        return ResponseEntity.noContent().build();
//    }
//
//    /** 🔍 Search Products */
//    @PostMapping("/search")
//    public Page<ProductResponseDTO> search(@RequestBody ProductSearchDTO filter) {
//        return productService.search(filter);
//    }
//
//    /** 🏪 Vendor’s Products */
//    @GetMapping("/vendor")
//    @PreAuthorize("hasAuthority('VENDOR')")
//    public List<ProductResponseDTO> getVendorProducts(@AuthenticationPrincipal User user) {
//        return productService.getVendorProducts(user.getId());
//    }
//}



//



//----------------

package com.agrowmart.controller;


import com.agrowmart.dto.auth.product.ProductCreateDTO;
import com.agrowmart.dto.auth.product.ProductResponseDTO;
import com.agrowmart.dto.auth.product.ProductSearchDTO;
import com.agrowmart.dto.auth.product.ProductStatusUpdateRequest;
import com.agrowmart.dto.auth.product.ProductUpdateDTO;
import com.agrowmart.dto.auth.product.VendorProductPaginatedResponse;
import com.agrowmart.entity.User;
import com.agrowmart.service.ProductService;

import jakarta.validation.Valid;

import org.apache.http.*;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")

public class ProductController {

//    // This is auto-injected by Lombok — DO NOT write constructor manually!
    private final ProductService productService;

    
  public ProductController(ProductService productService) {
      this.productService = productService;
  }

    
    
    // CREATE PRODUCT
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('VENDOR')")
    public ProductResponseDTO create(
            @ModelAttribute ProductCreateDTO dto,
            @AuthenticationPrincipal User user) throws Exception {
        return productService.create(dto, user.getId());
    }

    // UPDATE PRODUCT
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('VENDOR')")
    public ProductResponseDTO update(
            @PathVariable Long id,
            @ModelAttribute ProductUpdateDTO dto,
            @AuthenticationPrincipal User user) throws Exception {
        return productService.update(id, dto, user.getId());
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDOR')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        productService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // SEARCH PRODUCTS (Public)
    @PostMapping("/search")
    public Page<com.agrowmart.dto.auth.product.ProductResponseDTO> search(@RequestBody ProductSearchDTO filter) {
        return productService.search(filter);
    }

// // GET VENDOR'S OWN PRODUCTS
//    @GetMapping("/vendor")
//    @PreAuthorize("hasAuthority('VENDOR')")
//    public VendorProductPaginatedResponse getVendorProducts(
//            @AuthenticationPrincipal User user,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "2147483647") int size) {
//
//        return productService.getVendorProductsPaginated(user.getId(), page, size);
//    }
//   
    // GET VENDOR'S OWN PRODUCTS
 // ProductController.java
    @GetMapping("/vendor")
    @PreAuthorize("hasAuthority('VENDOR')")
    public VendorProductPaginatedResponse getVendorProducts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2147483647") int size,
            @RequestParam(required = false) String status  // Optional filter
    ) {
        return productService.getVendorProductsPaginated(user.getId(), page, size, status);
    }
    
//------------
 // In ProductController.java (add this new endpoint)
 // 2. Updated controller method (recommended)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('VENDOR')")
    public ProductResponseDTO updateProductStatus(
        @PathVariable Long id,
        @RequestBody @Valid ProductStatusUpdateRequest request,  // ← use DTO + validation
        @AuthenticationPrincipal User user
    ) throws Exception {
        return productService.updateStatus(
            id,
            request.status().toUpperCase(),
            user.getId()
        );
    }
    
 //---------
 // ===================== SHOP PRODUCTS (PUBLIC) =====================
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByShop(
            @PathVariable Long shopId
    ) {
        return ResponseEntity.ok(
                productService.getProductsByShop(shopId)
        );
    }
    
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) throws Exception {
        ProductResponseDTO product = productService.getPublicProductById(id);
        return ResponseEntity.ok(product);
    }


}