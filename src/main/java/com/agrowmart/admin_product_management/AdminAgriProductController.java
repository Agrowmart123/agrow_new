package com.agrowmart.admin_product_management;


import com.agrowmart.dto.auth.AgriProduct.AgriProductResponseDTO;
import com.agrowmart.dto.auth.AgriProduct.AgriVendorInfoDTO;
import com.agrowmart.dto.auth.AgriProduct.PendingAgriProductDTO;
import com.agrowmart.entity.AgriProduct.BaseAgriProduct;
import com.agrowmart.entity.AgriProduct.Fertilizer;
import com.agrowmart.entity.AgriProduct.Pesticide;
import com.agrowmart.entity.AgriProduct.Pipe;
import com.agrowmart.entity.AgriProduct.Seeds;
import com.agrowmart.service.AgriProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/agri-products")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminAgriProductController {

 

    @Autowired
    private AgriProductService agriProductService;
    
    @Autowired
    private AdminAgriProductService adminService;
    
 // View product details by ID (Admin)
    @GetMapping("/{id}")
    public ResponseEntity<PendingAgriProductDTO> getProductById(@PathVariable Long id) {
        BaseAgriProduct product = adminService.getProductById(id);
        PendingAgriProductDTO dto = convertToPendingDto(product);
        return ResponseEntity.ok(dto);
    }


    // Get all pending products for approval
    @GetMapping("/pending")
    public ResponseEntity<List<PendingAgriProductDTO>> getPendingProducts() {
        List<BaseAgriProduct> pendingProducts = adminService.getPendingProducts();
        List<PendingAgriProductDTO> dtos = pendingProducts.stream()
                .map(this::convertToPendingDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get all rejected products
    @GetMapping("/rejected")
    public ResponseEntity<List<PendingAgriProductDTO>> getRejectedProducts() {
        List<BaseAgriProduct> rejectedProducts = adminService.getRejectedProducts();
        List<PendingAgriProductDTO> dtos = rejectedProducts.stream()
                .map(this::convertToPendingDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get all approved products
    @GetMapping("/approved")
    public ResponseEntity<List<AgriProductResponseDTO>> getApprovedProducts() {
        List<BaseAgriProduct> approvedProducts = adminService.getApprovedProducts();
        List<AgriProductResponseDTO> dtos = approvedProducts.stream()
                .map(agriProductService::entityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Approve a product
    @PostMapping("/{id}/approve")
    public ResponseEntity<AgriProductResponseDTO> approveProduct(@PathVariable Long id) {
        AgriProductResponseDTO approvedProduct = adminService.approveProduct(id);
        return ResponseEntity.ok(approvedProduct);
    }

    // Reject a product
    @PostMapping("/{id}/reject")
    public ResponseEntity<AgriProductResponseDTO> rejectProduct(
            @PathVariable Long id,
            @RequestBody RejectProductRequest request) {
        AgriProductResponseDTO rejectedProduct = adminService.rejectProduct(id, request.getReason());
        return ResponseEntity.ok(rejectedProduct);
    }

    // Delete a product (permanent delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Restore a rejected product (set back to pending)
    @PostMapping("/{id}/restore")
    public ResponseEntity<AgriProductResponseDTO> restoreProduct(@PathVariable Long id) {
        AgriProductResponseDTO restoredProduct = adminService.restoreProduct(id);
        return ResponseEntity.ok(restoredProduct);
    }

    // Search pending products
    @GetMapping("/pending/search")
    public ResponseEntity<List<PendingAgriProductDTO>> searchPendingProducts(@RequestParam String keyword) {
        List<BaseAgriProduct> searchResults = adminService.searchPendingProducts(keyword);
        List<PendingAgriProductDTO> dtos = searchResults.stream()
                .map(this::convertToPendingDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Convert BaseAgriProduct to PendingAgriProductDTO
    private PendingAgriProductDTO convertToPendingDto(BaseAgriProduct product) {
        String fertilizerType = null, nutrientComposition = null, fcoNumber = null;
        String seedscropType = null, seedsvariety = null, seedClass = null;
        Double seedsgerminationPercentage = null, seedsphysicalPurityPercentage = null;
        String seedslotNumber = null;
        String pesticideType = null, pesticideActiveIngredient = null, pesticideToxicity = null;
        String pesticideCibrcNumber = null, pesticideFormulation = null;
        String pipeType = null, pipeSize = null, pipeBisNumber = null;
        Double pipeLength = null;

        if (product instanceof Fertilizer f) {
            fertilizerType = f.getFertilizerType();
            nutrientComposition = f.getNutrientComposition();
            fcoNumber = f.getFcoNumber();
        } else if (product instanceof Seeds s) {
            seedscropType = s.getSeedscropType();
            seedsvariety = s.getSeedsvariety();
            seedClass = s.getSeedClass();
            seedsgerminationPercentage = s.getSeedsgerminationPercentage();
            seedsphysicalPurityPercentage = s.getSeedsphysicalPurityPercentage();
            seedslotNumber = s.getSeedslotNumber();
        } else if (product instanceof Pesticide pes) {
            pesticideType = pes.getPesticidetype();
            pesticideActiveIngredient = pes.getPesticideactiveIngredient();
            pesticideToxicity = pes.getPesticidetoxicity();
            pesticideCibrcNumber = pes.getPesticidecibrcNumber();
            pesticideFormulation = pes.getPesticideformulation();
        } else if (product instanceof Pipe pipe) {
            pipeType = pipe.getPipetype();
            pipeSize = pipe.getPipesize();
            pipeLength = pipe.getPipelength();
            pipeBisNumber = pipe.getPipebisNumber();
        }

        return new PendingAgriProductDTO(
                product.getId(),
                product.getAgriproductName(),
                product.getAgricategory(),
                product.getAgridescription(),
                product.getAgriprice(),
                product.getAgriunit(),
                product.getAgriquantity(),
                product.getAgribrandName(),
                product.getAgrilicenseNumber(),
                product.getAgrilicenseType(),
                product.getAgribatchNumber(),

                product.getAgriImageUrls(),   // ✅ FIXED
                product.getCreatedAt(),       // ✅ FIXED

                product.getAgrimanufacturerName(),
                product.getAgrimanufacturingDate(),
                product.getAgriexpiryDate(),
                product.getApprovalStatus(),
                product.getRejectionReason(),

                new AgriVendorInfoDTO(
                        product.getVendor().getId(),
                        product.getVendor().getName(),
                        product.getVendor().getPhone(),
                        product.getVendor().getBusinessName(),
                        product.getVendor().getPhotoUrl(),
                        product.getVendor().getCity(),
                        product.getVendor().getState()
                ),

                fertilizerType,
                nutrientComposition,
                fcoNumber,

                seedscropType,
                seedsvariety,
                seedClass,
                seedsgerminationPercentage,
                seedsphysicalPurityPercentage,
                seedslotNumber,

                pesticideType,
                pesticideActiveIngredient,
                pesticideToxicity,
                pesticideCibrcNumber,
                pesticideFormulation,

                pipeType,
                pipeSize,
                pipeLength,
                pipeBisNumber
        );

}

class RejectProductRequest {
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
}