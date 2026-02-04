package com.agrowmart.admin_product_management;



import com.agrowmart.dto.auth.AgriProduct.AgriProductResponseDTO;
import com.agrowmart.entity.AgriProduct.BaseAgriProduct;
import com.agrowmart.entity.AgriProduct.BaseAgriProduct.ApprovalStatus;
import com.agrowmart.repository.AgriProductRepository;
import com.agrowmart.service.AgriProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminAgriProductService {

    @Autowired
    private AgriProductRepository agriProductRepository;

    @Autowired
    private AgriProductService agriProductService;

    // Get all pending products
    public List<BaseAgriProduct> getPendingProducts() {
        return agriProductRepository.findAllPending();
    }

    // Get all rejected products
    public List<BaseAgriProduct> getRejectedProducts() {
        return agriProductRepository.findAllRejected();
    }

    // Get all approved products
    public List<BaseAgriProduct> getApprovedProducts() {
        return agriProductRepository.findAllApproved();
    }

    // Search pending products
    public List<BaseAgriProduct> searchPendingProducts(String keyword) {
        // You might want to add a specific query method in repository
        return agriProductRepository.findByApprovalStatus(ApprovalStatus.PENDING).stream()
                .filter(p -> p.getAgriproductName().toLowerCase().contains(keyword.toLowerCase()) ||
                        (p.getAgridescription() != null && p.getAgridescription().toLowerCase().contains(keyword.toLowerCase())))
                .toList();
    }

    @Transactional
    public AgriProductResponseDTO approveProduct(Long id) {
        BaseAgriProduct product = agriProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not in pending status");
        }

        // Set approval status
        product.setApprovalStatus(ApprovalStatus.APPROVED);
        product.setApprovedBy(getCurrentAdminId()); // You might want to get from authentication
        product.setApprovedAt(LocalDate.now());
        product.setVisibleToCustomers(true); // Make visible to customers
        product.setVerified(true); // Mark as verified

        BaseAgriProduct saved = agriProductRepository.save(product);
        return agriProductService.entityToDto(saved);
    }

    @Transactional
    public AgriProductResponseDTO rejectProduct(Long id, String reason) {
        BaseAgriProduct product = agriProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not in pending status");
        }

        // Set rejection status
        product.setApprovalStatus(ApprovalStatus.REJECTED);
        product.setRejectionReason(reason);
        product.setRejectedBy(getCurrentAdminId()); // You might want to get from authentication
        product.setRejectedAt(LocalDate.now());
        product.setVisibleToCustomers(false); // Hide from customers

        BaseAgriProduct saved = agriProductRepository.save(product);
        return agriProductService.entityToDto(saved);
    }

    @Transactional
    public AgriProductResponseDTO restoreProduct(Long id) {
        BaseAgriProduct product = agriProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getApprovalStatus() != ApprovalStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not in rejected status");
        }

        // Restore to pending status
        product.setApprovalStatus(ApprovalStatus.PENDING);
        product.setRejectionReason(null);
        product.setRejectedBy(null);
        product.setRejectedAt(null);
        product.setVisibleToCustomers(false); // Keep hidden until re-approved

        BaseAgriProduct saved = agriProductRepository.save(product);
        return agriProductService.entityToDto(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        BaseAgriProduct product = agriProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // Permanent delete
        agriProductRepository.delete(product);
    }

    // Helper method - you might want to get admin ID from authentication
    private Long getCurrentAdminId() {
        // In real implementation, get from SecurityContextHolder
        return 1L; // Default admin ID - replace with actual implementation
    }
}