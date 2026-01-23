//package com.agrowmart.controller;
//
//import com.agrowmart.dto.auth.AgriProduct.AgriProductCreateDTO;
//import com.agrowmart.dto.auth.AgriProduct.AgriProductResponseDTO;
//import com.agrowmart.service.AgriProductService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.http.MediaType;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/agri/products")
//public class AgriProductController {
//
//    @Autowired
//    private AgriProductService service;
//
//    /**
//     * CREATE Agri Product - Now supports image upload via Cloudinary
//     * Content-Type: multipart/form-data
//     */
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<AgriProductResponseDTO> create(
//            @Valid @RequestPart("dto") AgriProductCreateDTO dto,
//            @RequestPart(value = "AgriimageUrl", required = false) List<MultipartFile> imageFiles,
//            @RequestPart(value = "licenseImage", required = false) MultipartFile licenseImage,
//            Authentication auth) {
//
//        return ResponseEntity.ok(service.create(dto, imageFiles, licenseImage, auth));
//    }
//
//    /**
//     * GET All Agri Products - Public access
//     */
//    @GetMapping
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<List<AgriProductResponseDTO>> getAll() {
//        return ResponseEntity.ok(service.getAll());
//    }
//
//    /**
//     * GET Single Product by ID - Public access
//     */
//    @GetMapping("/{id}")
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<AgriProductResponseDTO> getOne(@PathVariable Long id) {
//        return ResponseEntity.ok(service.getById(id));
//    }
//
//    /**
//     * GET My Products - Only authenticated AGRI vendor
//     */
//    @GetMapping("/my")
//    @PreAuthorize("hasAuthority('AGRI')")
//    public ResponseEntity<List<AgriProductResponseDTO>> getMy(Authentication auth) {
//        return ResponseEntity.ok(service.getMyProducts(auth));
//    }
//
//    /**
//     * UPDATE Agri Product - Currently uses JSON (no image update yet)
//     * You can extend this later to support file upload like create()
//     */
//    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('AGRI')")
//    public ResponseEntity<AgriProductResponseDTO> update(
//            @PathVariable Long id,
//            @Valid @RequestBody AgriProductCreateDTO dto,
//            Authentication auth) {
//
//        return ResponseEntity.ok(service.update(id, dto, auth));
//    }
//
//    /**
//     * DELETE Agri Product
//     */
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('AGRI')")
//    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
//        service.delete(id, auth);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * SEARCH Agri Products - Uncomment when ready
//     */
//    // @GetMapping("/search")
//    // @PreAuthorize("permitAll()")
//    // public ResponseEntity<List<AgriProductResponseDTO>> search(@RequestParam String keyword) {
//    //     return ResponseEntity.ok(service.search(keyword));
//    // }
//}




package com.agrowmart.controller;

import com.agrowmart.dto.auth.AgriProduct.AgriProductCreateDTO;
import com.agrowmart.dto.auth.AgriProduct.AgriProductResponseDTO;
import com.agrowmart.service.AgriProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/agri/products")  // ← Fixed path
public class AgriProductController {

    @Autowired
    private AgriProductService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('AGRI')")
    public ResponseEntity<AgriProductResponseDTO> create(
            @RequestParam("AgriproductName") @NotBlank String AgriproductName,
            @RequestParam("category") @NotBlank String category,
            @RequestParam("Agriprice") @NotNull @Positive BigDecimal Agriprice,
            @RequestParam("Agriunit") @NotBlank String Agriunit,
            @RequestParam("Agriquantity") @NotNull @PositiveOrZero Integer Agriquantity,

            @RequestParam(value = "Agridescription", required = false) String Agridescription,
            @RequestParam(value = "AgribrandName", required = false) String AgribrandName,
            @RequestParam(value = "AgripackagingType", required = false) String AgripackagingType,
            @RequestParam(value = "AgrilicenseNumber", required = false) String AgrilicenseNumber,
            @RequestParam(value = "AgrilicenseType", required = false) String AgrilicenseType,
            @RequestParam(value = "AgribatchNumber", required = false) String AgribatchNumber,
            @RequestParam(value = "AgrimanufacturerName", required = false) String AgrimanufacturerName,
            @RequestParam(value = "AgrimanufacturingDate", required = false) String AgrimanufacturingDate,
            @RequestParam(value = "AgriexpiryDate", required = false) String AgriexpiryDate,

            //------------ fertilizer
            
            @RequestParam(value = "fertilizerType", required = false) String fertilizerType,
            @RequestParam(value = "nutrientComposition", required = false) String nutrientComposition,
            @RequestParam(value = "fcoNumber", required = false) String fcoNumber,
            
            //-------------Seeds
            @RequestParam(value = "SeedscropType", required = false) String SeedscropType,
            @RequestParam(value = "Seedsvariety", required = false) String Seedsvariety,
            @RequestParam(value = "seedClass", required = false) String seedClass,
            @RequestParam(value = "SeedsgerminationPercentage", required = false) Double SeedsgerminationPercentage,
            @RequestParam(value = "SeedsphysicalPurityPercentage", required = false) Double SeedsphysicalPurityPercentage,
            @RequestParam(value = "SeedslotNumber", required = false) String SeedslotNumber,
            
           //-------pesticide
            
            @RequestParam(value = "Pesticidetype", required = false) String Pesticidetype,
            @RequestParam(value = "PesticideactiveIngredient", required = false) String PesticideactiveIngredient,
            @RequestParam(value = "Pesticidetoxicity", required = false) String Pesticidetoxicity,
            @RequestParam(value = "PesticidecibrcNumber", required = false) String PesticidecibrcNumber,
            @RequestParam(value = "Pesticideformulation", required = false) String Pesticideformulation,
            
           //----- pipe
            @RequestParam(value = "Pipetype", required = false) String Pipetype,
            @RequestParam(value = "Pipesize", required = false) String Pipesize,
            @RequestParam(value = "Pipelength", required = false) Double Pipelength,
            @RequestParam(value = "PipebisNumber", required = false) String PipebisNumber,

            @RequestPart(value = "AgriimageUrl", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "licenseImage", required = false) MultipartFile licenseImage,

            Authentication auth) {

        LocalDate manufacturingDate = AgrimanufacturingDate != null && !AgrimanufacturingDate.isBlank()
                ? LocalDate.parse(AgrimanufacturingDate) : null;
        LocalDate expiryDate = AgriexpiryDate != null && !AgriexpiryDate.isBlank()
                ? LocalDate.parse(AgriexpiryDate) : null;

        AgriProductCreateDTO dto = new AgriProductCreateDTO(
                AgriproductName, category, Agridescription, Agriprice, Agriunit, Agriquantity,
                null, AgribrandName, AgripackagingType, AgrilicenseNumber, AgrilicenseType, null,
                AgribatchNumber, AgrimanufacturerName, manufacturingDate, expiryDate,
                fertilizerType, nutrientComposition, fcoNumber,
                SeedscropType, Seedsvariety, seedClass, SeedsgerminationPercentage, SeedsphysicalPurityPercentage, SeedslotNumber,
                Pesticidetype, PesticideactiveIngredient, Pesticidetoxicity, PesticidecibrcNumber, Pesticideformulation,
                Pipetype, Pipesize, Pipelength, PipebisNumber
        );

        return ResponseEntity.ok(service.create(dto, imageFiles, licenseImage, auth));
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AgriProductResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AgriProductResponseDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('AGRI')")
    public ResponseEntity<List<AgriProductResponseDTO>> getMy(Authentication auth) {
        return ResponseEntity.ok(service.getMyProducts(auth));
    }
//
//    // Keep JSON update for now (safe & simple)
//    @PutMapping(value = "/{id}", consumes = "application/json")
//    @PreAuthorize("hasAuthority('AGRI')")
//    public ResponseEntity<AgriProductResponseDTO> update(
//            @PathVariable Long id,
//            @Valid @RequestBody AgriProductCreateDTO dto,
//            Authentication auth) {
//        return ResponseEntity.ok(service.update(id, dto, auth));
//    }
    
    
 // In AgriProductController.java

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('AGRI')")
    public ResponseEntity<AgriProductResponseDTO> updateWithFormData(
            @PathVariable Long id,
            @RequestParam(value = "AgriproductName", required = false) String AgriproductName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "Agridescription", required = false) String Agridescription,
            @RequestParam(value = "Agriprice", required = false) BigDecimal Agriprice,
            @RequestParam(value = "Agriunit", required = false) String Agriunit,
            @RequestParam(value = "Agriquantity", required = false) Integer Agriquantity,
            @RequestParam(value = "AgribrandName", required = false) String AgribrandName,
            @RequestParam(value = "AgripackagingType", required = false) String AgripackagingType,
            @RequestParam(value = "AgrilicenseNumber", required = false) String AgrilicenseNumber,
            @RequestParam(value = "AgrilicenseType", required = false) String AgrilicenseType,
            @RequestParam(value = "AgribatchNumber", required = false) String AgribatchNumber,
            @RequestParam(value = "AgrimanufacturerName", required = false) String AgrimanufacturerName,
            @RequestParam(value = "AgrimanufacturingDate", required = false) String AgrimanufacturingDate,
            @RequestParam(value = "AgriexpiryDate", required = false) String AgriexpiryDate,

            // Category-specific (all optional)
            @RequestParam(value = "fertilizerType", required = false) String fertilizerType,
            @RequestParam(value = "nutrientComposition", required = false) String nutrientComposition,
            @RequestParam(value = "fcoNumber", required = false) String fcoNumber,
            @RequestParam(value = "SeedscropType", required = false) String SeedscropType,
            @RequestParam(value = "Seedsvariety", required = false) String Seedsvariety,
            @RequestParam(value = "seedClass", required = false) String seedClass,
            @RequestParam(value = "SeedsgerminationPercentage", required = false) Double SeedsgerminationPercentage,
            @RequestParam(value = "SeedsphysicalPurityPercentage", required = false) Double SeedsphysicalPurityPercentage,
            @RequestParam(value = "SeedslotNumber", required = false) String SeedslotNumber,
            @RequestParam(value = "Pesticidetype", required = false) String Pesticidetype,
            @RequestParam(value = "PesticideactiveIngredient", required = false) String PesticideactiveIngredient,
            @RequestParam(value = "Pesticidetoxicity", required = false) String Pesticidetoxicity,
            @RequestParam(value = "PesticidecibrcNumber", required = false) String PesticidecibrcNumber,
            @RequestParam(value = "Pesticideformulation", required = false) String Pesticideformulation,
            @RequestParam(value = "Pipetype", required = false) String Pipetype,
            @RequestParam(value = "Pipesize", required = false) String Pipesize,
            @RequestParam(value = "Pipelength", required = false) Double Pipelength,
            @RequestParam(value = "PipebisNumber", required = false) String PipebisNumber,

            @RequestPart(value = "AgriimageUrl", required = false) List<MultipartFile> newImageFiles,
            @RequestPart(value = "licenseImage", required = false) MultipartFile newLicenseImage,
            Authentication auth) {

        // Parse dates safely
        LocalDate manufacturingDate = AgrimanufacturingDate != null && !AgrimanufacturingDate.isBlank()
                ? LocalDate.parse(AgrimanufacturingDate) : null;
        LocalDate expiryDate = AgriexpiryDate != null && !AgriexpiryDate.isBlank()
                ? LocalDate.parse(AgriexpiryDate) : null;

        // Build partial DTO with only provided fields
        AgriProductCreateDTO dto = new AgriProductCreateDTO(
                AgriproductName, category, Agridescription, Agriprice, Agriunit, Agriquantity,
                null, // imageUrls will be handled separately
                AgribrandName, AgripackagingType, AgrilicenseNumber, AgrilicenseType, null, // licenseImageUrl handled separately
                AgribatchNumber, AgrimanufacturerName, manufacturingDate, expiryDate,
                fertilizerType, nutrientComposition, fcoNumber,
                SeedscropType, Seedsvariety, seedClass, SeedsgerminationPercentage, SeedsphysicalPurityPercentage, SeedslotNumber,
                Pesticidetype, PesticideactiveIngredient, Pesticidetoxicity, PesticidecibrcNumber, Pesticideformulation,
                Pipetype, Pipesize, Pipelength, PipebisNumber
        );

        return ResponseEntity.ok(
                service.updateWithImages(id, dto, newImageFiles, newLicenseImage, auth)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('AGRI')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AgriProductResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(service.search(keyword));  // ← Fixed
    }
}