
package com.agrowmart.service;

import com.agrowmart.dto.auth.AgriProduct.AgriProductCreateDTO;
import com.agrowmart.dto.auth.AgriProduct.AgriProductResponseDTO;
import com.agrowmart.dto.auth.AgriProduct.AgriVendorInfoDTO;
import com.agrowmart.entity.User;
import com.agrowmart.entity.AgriProduct.*;
import com.agrowmart.entity.AgriProduct.BaseAgriProduct.ApprovalStatus;
import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.AuthExceptions.FileUploadException;
import com.agrowmart.exception.SubscriptionLimitExceededException;
import com.agrowmart.repository.AgriProductRepository;
import com.agrowmart.repository.UserRepository;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgriProductService {

    @Autowired
    private AgriProductRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private SubscriptionService subscriptionService;  // Required for subscription check

    // Helper: Get authenticated vendor
    private User getCurrentVendor(Authentication auth) {
    	if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationFailedException("Unauthorized: Please login first");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }
        throw new AuthenticationFailedException("Invalid authentication: Expected User object");
    }

    // Helper: Map vendor to DTO
    private AgriVendorInfoDTO mapVendor(User user) {
        return new AgriVendorInfoDTO(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getBusinessName(),
                user.getPhotoUrl(),
                user.getCity(),
                user.getState()
        );
    }

    // Helper: Convert entity to response DTO
    public AgriProductResponseDTO entityToDto(BaseAgriProduct p) {

        String fertilizerType = null, nutrientComposition = null, fcoNumber = null;
        String seedscropType = null, seedsvariety = null, seedClass = null;
        Double seedsgerminationPercentage = null, seedsphysicalPurityPercentage = null;
        String seedslotNumber = null;
        String pesticideType = null, pesticideActiveIngredient = null, pesticideToxicity = null;
        String pesticideCibrcNumber = null, pesticideFormulation = null;
        String pipeType = null, pipeSize = null, pipeBisNumber = null;
        Double pipeLength = null;

        if (p instanceof Fertilizer f) {
            fertilizerType = f.getFertilizerType();
            nutrientComposition = f.getNutrientComposition();
            fcoNumber = f.getFcoNumber();
        } else if (p instanceof Seeds s) {
            seedscropType = s.getSeedscropType();
            seedsvariety = s.getSeedsvariety();
            seedClass = s.getSeedClass();
            seedsgerminationPercentage = s.getSeedsgerminationPercentage();
            seedsphysicalPurityPercentage = s.getSeedsphysicalPurityPercentage();
            seedslotNumber = s.getSeedslotNumber();
        } else if (p instanceof Pesticide pes) {
            pesticideType = pes.getPesticidetype();
            pesticideActiveIngredient = pes.getPesticideactiveIngredient();
            pesticideToxicity = pes.getPesticidetoxicity();
            pesticideCibrcNumber = pes.getPesticidecibrcNumber();
            pesticideFormulation = pes.getPesticideformulation();
        } else if (p instanceof Pipe pipe) {
            pipeType = pipe.getPipetype();
            pipeSize = pipe.getPipesize();
            pipeLength = pipe.getPipelength();
            pipeBisNumber = pipe.getPipebisNumber();
        }

        return new AgriProductResponseDTO(
            p.getId(),
            p.getAgriproductName(),
            p.getAgricategory(),
            p.getAgridescription(),
            p.getAgriprice(),
            p.getAgriunit(),
            p.getAgriquantity(),

            p.getAgriImageUrls(),   // ✅ CORRECT POSITION
            p.getCreatedAt(),       // ✅ CORRECT POSITION

            p.getAgribrandName(),
            p.getAgripackagingType(),
            p.getAgrilicenseNumber(),
            p.getAgrilicenseType(),
            p.getVerified(),
            p.isVisibleToCustomers(),
            p.getApprovalStatus(),
            p.getRejectionReason(),
            mapVendor(p.getVendor()),

            fertilizerType, nutrientComposition, fcoNumber,
            seedscropType, seedsvariety, seedClass,
            seedsgerminationPercentage, seedsphysicalPurityPercentage, seedslotNumber,
            pesticideType, pesticideActiveIngredient, pesticideToxicity,
            pesticideCibrcNumber, pesticideFormulation,
            pipeType, pipeSize, pipeLength, pipeBisNumber
        );
    }


    // ==================================================================
    // Combined validation: Online + Profile complete + Subscription + AGRI only
    // ==================================================================
    private void validateVendorCanManageProducts(User vendor) {
    	if (vendor == null) {
            throw new AuthenticationFailedException("Vendor authentication required");
        }

        // 1. Only AGRI vendors
        if (!"AGRI".equals(vendor.getRole().getName())) {
            throw new BusinessValidationException("Only AGRI vendors can manage agricultural products");
        }

        // 2. Must be ONLINE
        if (!"ONLINE".equalsIgnoreCase(vendor.getOnlineStatus())) {
            throw new BusinessValidationException("You must be ONLINE to manage products. Update your status first.");
        }

        // 3. Profile must be completed
        if (!"YES".equals(vendor.getProfileCompleted())) {
            throw new BusinessValidationException("Please complete your profile 100% before managing products");
        }
        

        // 4. Subscription check (product limit, active subscription, etc.)
        subscriptionService.checkProductLimit(vendor);
    }

    // ==================================================================
    // CREATE – protected by validation
    // ==================================================================
    @Transactional   // ← THIS LINE IS CRUCIAL
    public AgriProductResponseDTO create(
            AgriProductCreateDTO dto,
            List<MultipartFile> imageFiles,
            MultipartFile licenseImageFile,
            Authentication auth) {

        User vendor = getCurrentVendor(auth);

        // All checks in one place
        validateVendorCanManageProducts(vendor);
        
        if (dto == null || dto.category() == null || dto.category().trim().isEmpty()) {
            throw new BusinessValidationException("Product category is required");
        }
        
        
        BaseAgriProduct product = switch (dto.category().toUpperCase()) {
            case "FERTILIZER" -> new Fertilizer();
            case "SEEDS" -> new Seeds();
            case "PESTICIDE" -> new Pesticide();
            case "PIPE" -> new Pipe();
            default -> throw new BusinessValidationException("Invalid category: " + dto.category());
        };

        // Upload product images
        List<String> uploadedImageUrls = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    try {
                        String url = cloudinaryService.upload(file);
                        uploadedImageUrls.add(url);
                    } catch (FileUploadException e) {
                        throw new FileUploadException("Failed to upload product image: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        // Upload license image
        String licenseImageUrl = dto.AgrilicenseImageUrl();
        if (licenseImageFile != null && !licenseImageFile.isEmpty()) {
            try {
                licenseImageUrl = cloudinaryService.upload(licenseImageFile);
            } catch (FileUploadException e) {
                throw new FileUploadException("Failed to upload license image", e);
            }
        }

        // Set common fields
        product.setAgriproductName(dto.AgriproductName());
        product.setAgridescription(dto.Agridescription());
        product.setAgriprice(dto.Agriprice());
        product.setAgriunit(dto.Agriunit());
        product.setAgriquantity(dto.Agriquantity());
        product.setAgriImageUrls(uploadedImageUrls);
        product.setAgribrandName(dto.AgribrandName());
        product.setAgripackagingType(dto.AgripackagingType());
        product.setAgrilicenseNumber(dto.AgrilicenseNumber());
        product.setAgrilicenseType(dto.AgrilicenseType());
        product.setAgrilicenseImageUrl(licenseImageUrl);
        product.setAgribatchNumber(dto.AgribatchNumber());
        product.setAgrimanufacturerName(dto.AgrimanufacturerName());
        product.setAgrimanufacturingDate(dto.AgrimanufacturingDate());
        product.setAgriexpiryDate(dto.AgriexpiryDate());
        product.setVendor(vendor);

        
        product.setApprovalStatus(BaseAgriProduct.ApprovalStatus.PENDING);
        product.setVisibleToCustomers(false);
        
        // Category-specific fields
        if (product instanceof Fertilizer f) {
            f.setFertilizerType(dto.fertilizerType());
            f.setNutrientComposition(dto.nutrientComposition());
            f.setFcoNumber(dto.fcoNumber());
        } else if (product instanceof Seeds s) {
            s.setSeedscropType(dto.SeedscropType());
            s.setSeedsvariety(dto.Seedsvariety());
            s.setSeedClass(dto.seedClass());
            s.setSeedsgerminationPercentage(dto.SeedsgerminationPercentage());
            s.setSeedsphysicalPurityPercentage(dto.SeedsphysicalPurityPercentage());
            s.setSeedslotNumber(dto.SeedslotNumber());
        } else if (product instanceof Pesticide pes) {
            pes.setPesticidetype(dto.Pesticidetype());
            pes.setPesticideactiveIngredient(dto.PesticideactiveIngredient());
            pes.setPesticidetoxicity(dto.Pesticidetoxicity());
            pes.setPesticidecibrcNumber(dto.PesticidecibrcNumber());
            pes.setPesticideformulation(dto.Pesticideformulation());
        } else if (product instanceof Pipe pipe) {
            pipe.setPipetype(dto.Pipetype());
            pipe.setPipesize(dto.Pipesize());
            pipe.setPipelength(dto.Pipelength());
            pipe.setPipebisNumber(dto.PipebisNumber());
        }

        BaseAgriProduct saved = repository.save(product);
        return entityToDto(saved);
    }

    // ==================================================================
    // UPDATE – protected by validation
    // ==================================================================
    @Transactional   // ← THIS LINE IS CRUCIAL
    public AgriProductResponseDTO update(Long id, AgriProductCreateDTO dto, Authentication auth) {
        User vendor = getCurrentVendor(auth);

        // All checks in one place
        validateVendorCanManageProducts(vendor);

        BaseAgriProduct existing = repository.findByIdAndVendor(id, vendor)
                .orElseThrow(() -> new BusinessValidationException("Product not found or you don't own it"));
        // Update fields
        existing.setAgriproductName(dto.AgriproductName());
        existing.setAgridescription(dto.Agridescription());
        existing.setAgriprice(dto.Agriprice());
        existing.setAgriunit(dto.Agriunit());
        existing.setAgriquantity(dto.Agriquantity());
        existing.setAgribrandName(dto.AgribrandName());
        existing.setAgripackagingType(dto.AgripackagingType());
        existing.setAgrilicenseNumber(dto.AgrilicenseNumber());
        existing.setAgrilicenseType(dto.AgrilicenseType());
        existing.setAgrilicenseImageUrl(dto.AgrilicenseImageUrl());
        existing.setAgribatchNumber(dto.AgribatchNumber());
        existing.setAgrimanufacturerName(dto.AgrimanufacturerName());
        existing.setAgrimanufacturingDate(dto.AgrimanufacturingDate());
        existing.setAgriexpiryDate(dto.AgriexpiryDate());

        // Category-specific updates
        if (existing instanceof Fertilizer f) {
            f.setFertilizerType(dto.fertilizerType());
            f.setNutrientComposition(dto.nutrientComposition());
            f.setFcoNumber(dto.fcoNumber());
        } else if (existing instanceof Seeds s) {
            s.setSeedscropType(dto.SeedscropType());
            s.setSeedsvariety(dto.Seedsvariety());
            s.setSeedClass(dto.seedClass());
            s.setSeedsgerminationPercentage(dto.SeedsgerminationPercentage());
            s.setSeedsphysicalPurityPercentage(dto.SeedsphysicalPurityPercentage());
            s.setSeedslotNumber(dto.SeedslotNumber());
        } else if (existing instanceof Pesticide pes) {
            pes.setPesticidetype(dto.Pesticidetype());
            pes.setPesticideactiveIngredient(dto.PesticideactiveIngredient());
            pes.setPesticidetoxicity(dto.Pesticidetoxicity());
            pes.setPesticidecibrcNumber(dto.PesticidecibrcNumber());
            pes.setPesticideformulation(dto.Pesticideformulation());
        } else if (existing instanceof Pipe pipe) {
            pipe.setPipetype(dto.Pipetype());
            pipe.setPipesize(dto.Pipesize());
            pipe.setPipelength(dto.Pipelength());
            pipe.setPipebisNumber(dto.PipebisNumber());
        }

        BaseAgriProduct saved = repository.save(existing);
        return entityToDto(saved);
    }

    // ==================================================================
    // UPDATE WITH IMAGES – protected by validation
    // ==================================================================
    public AgriProductResponseDTO updateWithImages(
            Long id,
            AgriProductCreateDTO dto,
            List<MultipartFile> newImageFiles,
            MultipartFile newLicenseImage,
            Authentication auth) {

        User vendor = getCurrentVendor(auth);

        // All checks in one place
        validateVendorCanManageProducts(vendor);
        BaseAgriProduct existing = repository.findByIdAndVendor(id, vendor)
                .orElseThrow(() -> new BusinessValidationException("Product not found or you don't own it"));
        // Handle images
        List<String> currentImages = existing.getAgriImageUrls() != null ?
                new ArrayList<>(existing.getAgriImageUrls()) : new ArrayList<>();

        if (newImageFiles != null && !newImageFiles.isEmpty()) {
            List<String> uploadedUrls = new ArrayList<>();
            for (MultipartFile file : newImageFiles) {
                if (!file.isEmpty()) {
                    try {
                        String url = cloudinaryService.upload(file);
                        uploadedUrls.add(url);
                    } catch (FileUploadException e) {
                        throw new FileUploadException("Failed to upload new product image", e);
                    }
                }
            }
            currentImages = uploadedUrls; // replace
        }

        if (newLicenseImage != null && !newLicenseImage.isEmpty()) {
            try {
                String newLicenseUrl = cloudinaryService.upload(newLicenseImage);
                existing.setAgrilicenseImageUrl(newLicenseUrl);
            } catch (FileUploadException e) {
                throw new FileUploadException("Failed to upload new license image", e);
            }
        }

        // Update fields if provided
        if (dto.AgriproductName() != null) existing.setAgriproductName(dto.AgriproductName());
        if (dto.Agridescription() != null) existing.setAgridescription(dto.Agridescription());
        if (dto.Agriprice() != null) existing.setAgriprice(dto.Agriprice());
        if (dto.Agriunit() != null) existing.setAgriunit(dto.Agriunit());
        if (dto.Agriquantity() != null) existing.setAgriquantity(dto.Agriquantity());
        if (dto.AgribrandName() != null) existing.setAgribrandName(dto.AgribrandName());
        if (dto.AgripackagingType() != null) existing.setAgripackagingType(dto.AgripackagingType());
        if (dto.AgrilicenseNumber() != null) existing.setAgrilicenseNumber(dto.AgrilicenseNumber());
        if (dto.AgrilicenseType() != null) existing.setAgrilicenseType(dto.AgrilicenseType());
        if (dto.AgribatchNumber() != null) existing.setAgribatchNumber(dto.AgribatchNumber());
        if (dto.AgrimanufacturerName() != null) existing.setAgrimanufacturerName(dto.AgrimanufacturerName());
        if (dto.AgrimanufacturingDate() != null) existing.setAgrimanufacturingDate(dto.AgrimanufacturingDate());
        if (dto.AgriexpiryDate() != null) existing.setAgriexpiryDate(dto.AgriexpiryDate());

        // Category-specific
        if (existing instanceof Fertilizer f) {
            if (dto.fertilizerType() != null) f.setFertilizerType(dto.fertilizerType());
            if (dto.nutrientComposition() != null) f.setNutrientComposition(dto.nutrientComposition());
            if (dto.fcoNumber() != null) f.setFcoNumber(dto.fcoNumber());
        } else if (existing instanceof Seeds s) {
            if (dto.SeedscropType() != null) s.setSeedscropType(dto.SeedscropType());
            if (dto.Seedsvariety() != null) s.setSeedsvariety(dto.Seedsvariety());
            if (dto.seedClass() != null) s.setSeedClass(dto.seedClass());
            if (dto.SeedsgerminationPercentage() != null) s.setSeedsgerminationPercentage(dto.SeedsgerminationPercentage());
            if (dto.SeedsphysicalPurityPercentage() != null) s.setSeedsphysicalPurityPercentage(dto.SeedsphysicalPurityPercentage());
            if (dto.SeedslotNumber() != null) s.setSeedslotNumber(dto.SeedslotNumber());
        } // add Pesticide & Pipe if needed

        existing.setAgriImageUrls(currentImages);
        BaseAgriProduct saved = repository.save(existing);
        return entityToDto(saved);
    }

    // ==================================================================
    // Other methods (unchanged)
    // ==================================================================
 // read-only methods can have readOnly = true
   

 // In AgriProductService.java, update the getAll method to ensure only approved are visible:
    @Transactional(readOnly = true)
    public List<AgriProductResponseDTO> getAll() {
        return repository.findByVisibleToCustomersTrue().stream()
                .filter(p -> p.getApprovalStatus() == BaseAgriProduct.ApprovalStatus.APPROVED)
                .map(this::entityToDto)
                .toList();
    }
    
 // read-only methods can have readOnly = true
    @Transactional(readOnly = true)
    public List<AgriProductResponseDTO> getMyProducts(Authentication auth) {
        User vendor = getCurrentVendor(auth);
        return repository.findByVendor(vendor).stream()
                .map(this::entityToDto)
                .toList();
    }
    
 // read-only methods can have readOnly = true
    @Transactional(readOnly = true)
    public AgriProductResponseDTO getById(Long id) {
        return entityToDto(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
    }

    
   

    
    
 // In AgriProductService.java, update the delete method:
    @Transactional
    public void delete(Long id, Authentication auth) {
        User vendor = getCurrentVendor(auth);
        
  
        BaseAgriProduct product = repository.findByIdAndVendor(id, vendor)
                .orElseThrow(() -> new BusinessValidationException("Product not found or you don't own it"));
        
        if (product.getApprovalStatus() == BaseAgriProduct.ApprovalStatus.APPROVED) {
        	 throw new BusinessValidationException("Approved products cannot be deleted. Contact admin.");
        }
        
        
        repository.delete(product);
    }
    
    
    @Transactional(readOnly = true)
    public List<AgriProductResponseDTO> search(String keyword) {
        return repository.searchVisible(keyword)
            .stream()
            .map(this::entityToDto)
            .toList();
    }
 //-----------------   
    @Transactional
    public AgriProductResponseDTO updateVisibility(Long id, boolean visible, Authentication auth) {
        User vendor = getCurrentVendor(auth);
        
        // Only owner can change visibility
        BaseAgriProduct product = repository.findByIdAndVendor(id, vendor)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Product not found or you are not the owner"
            ));

        product.setVisibleToCustomers(visible);
        
        BaseAgriProduct saved = repository.save(product);
        
        return entityToDto(saved);
    }
    
    
    
 // Add this method to AgriProductService.java
    @Transactional(readOnly = true)
    public List<AgriProductResponseDTO> getMyRejectedProducts(Authentication auth) {
        User vendor = getCurrentVendor(auth);
        return repository.findByVendorAndApprovalStatus(vendor, ApprovalStatus.REJECTED)
                .stream()
                .map(this::entityToDto)
                .toList();
    }
    
 // Optional: Add getMyPendingProducts if needed, similar to getMyRejectedProducts
    @Transactional(readOnly = true)
    public List<AgriProductResponseDTO> getMyPendingProducts(Authentication auth) {
        User vendor = getCurrentVendor(auth);
        return repository.findByVendorAndApprovalStatus(vendor, ApprovalStatus.PENDING)
                .stream()
                .map(this::entityToDto)
                .toList();
    }
}