package com.agrowmart.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrowmart.dto.auth.product.ProductResponseDTO;
import com.agrowmart.entity.User;
import com.agrowmart.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/vendor")
@PreAuthorize("hasAuthority('VENDOR')")
public class VendorDashboardController {

    private final ProductService productService;

    public VendorDashboardController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/my-products")
    public List<ProductResponseDTO> myProducts(@AuthenticationPrincipal User user) {
        return productService.getVendorProducts(user.getId());
    }
}