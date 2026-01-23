package com.agrowmart.service;

import com.agrowmart.dto.auth.offer.*;
import com.agrowmart.entity.User;
import com.agrowmart.entity.order.Offer;
import com.agrowmart.entity.order.OfferPrice;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final CloudinaryService cloudinaryService;

    public OfferServiceImpl(OfferRepository offerRepository,
                            CloudinaryService cloudinaryService) {
        this.offerRepository = offerRepository;
        this.cloudinaryService = cloudinaryService;
    }

    // =====================================================
    // REGULAR OFFERS (Percentage / Flat Discount Codes)
    // =====================================================

    @Override
    @Transactional
    public OfferResponseDTO createOffer(User vendor, OfferRequestDTO dto) {

        Offer offer = new Offer();
        mapRegularOffer(dto, offer, vendor);

        offer = offerRepository.save(offer);
        return mapOfferResponse(offer);
    }

    @Override
    public List<OfferResponseDTO> getMyOffers(User vendor) {
        return offerRepository.findByMerchantIdAndIsActiveTrue(vendor.getId())
                .stream()
                .filter(o -> o.getDiscountType() != Offer.DiscountType.FREE_PRODUCT)
                .map(this::mapOfferResponse)
                .toList();
    }

    @Override
    @Transactional
    public OfferResponseDTO updateOffer(User vendor, Long id, OfferRequestDTO dto) {

        Offer offer = getVendorOffer(vendor, id);

        mapRegularOffer(dto, offer, vendor);

        offer = offerRepository.save(offer);
        return mapOfferResponse(offer);
    }

    @Override
    @Transactional
    public void deactivate(User vendor, Long id) {
        Offer offer = getVendorOffer(vendor, id);
        offer.setActive(false);
        offerRepository.save(offer);
    }

    // =====================================================
    // FREE / PAID GIFT OFFERS
    // =====================================================

    @Override
    @Transactional
    public FreeGiftResponseDTO createFreeGiftOffer(User vendor, FreeGiftRequestDTO dto, MultipartFile image) {

        validateFreeGiftPrice(dto.originalPrice(), dto.offerPrice(), dto.free());

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Gift image is required");
        }

        String imageUrl = null;
		try {
			imageUrl = cloudinaryService.upload(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Offer offer = new Offer();
        offer.setMerchant(vendor);
        offer.setTitle("Gift: " + dto.productName());
        offer.setDiscountType(Offer.DiscountType.FREE_PRODUCT);
        offer.setActive(true);
        offer.setStartDate(LocalDate.now());
        offer.setEndDate(LocalDate.now().plusYears(1));

        offer.setFreeProductName(dto.productName());
        offer.setFreeProductImageUrl(imageUrl);
        offer.setFreeProductDescription(dto.description());
        offer.setFreeProductQuantity(dto.quantity());
        offer.setMinPurchaseAmount(dto.minPurchaseAmount());
        offer.setFreeGiftOffer(dto.free());

        // ========== ADD THIS BLOCK ==========
        OfferPrice offerPrice = new OfferPrice();
        offerPrice.setOriginalPrice(dto.originalPrice());
        offerPrice.setOfferPrice(dto.free() ? BigDecimal.ZERO : dto.offerPrice());
        offerPrice.setFree(dto.free());

        offer.setPrice(offerPrice);  // Link the child entity
        // =====================================

        offerRepository.save(offer);

        return mapFreeGiftResponse(offer);
    }
    
    @Override
    public List<FreeGiftResponseDTO> getMyFreeGiftOffers(User vendor) {
        return offerRepository.findByMerchantIdAndIsActiveTrue(vendor.getId())
                .stream()
                .filter(o -> o.getDiscountType() == Offer.DiscountType.FREE_PRODUCT)
                .map(this::mapFreeGiftResponse)
                .toList();
    }

    @Override
    @Transactional
    public FreeGiftResponseDTO updateFreeGiftOffer(
            User vendor,
            Long id,
            FreeGiftRequestDTO dto,
            MultipartFile image) {

        Offer offer = getVendorGiftOffer(vendor, id);

        validateFreeGiftPrice(dto.originalPrice(), dto.offerPrice(), dto.free());

        // Update basic fields
        offer.setFreeProductName(dto.productName());
        offer.setFreeProductDescription(dto.description());
        offer.setFreeProductQuantity(dto.quantity());
        offer.setMinPurchaseAmount(dto.minPurchaseAmount());
        offer.setFreeGiftOffer(dto.free());

        // Handle image update (only if new image provided)
        if (image != null && !image.isEmpty()) {
            try {
                offer.setFreeProductImageUrl(cloudinaryService.upload(image));
            } catch (IOException e) {
                throw new RuntimeException("Image upload failed", e);
            }
        }

        // ========== PRICE HANDLING (Your code – PERFECT!) ==========
        OfferPrice offerPrice = offer.getPrice();
        if (offerPrice == null) {
            offerPrice = new OfferPrice();
            offer.setPrice(offerPrice);
        }

        offerPrice.setOriginalPrice(dto.originalPrice());
        offerPrice.setOfferPrice(dto.free() ? BigDecimal.ZERO : dto.offerPrice());
        offerPrice.setFree(dto.free());
        // ===========================================================

        offerRepository.save(offer);

        return mapFreeGiftResponse(offer);
    }
    
    @Override
    @Transactional
    public void deactivateFreeGiftOffer(User vendor, Long id) {
        Offer offer = getVendorGiftOffer(vendor, id);
        offer.setActive(false);
        offerRepository.save(offer);
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private Offer getVendorOffer(User vendor, Long id) {
        return offerRepository.findById(id)
                .filter(o -> o.getMerchant().getId().equals(vendor.getId()))
                .filter(o -> o.getDiscountType() != Offer.DiscountType.FREE_PRODUCT)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found or not yours"));
    }

    private Offer getVendorGiftOffer(User vendor, Long id) {
        return offerRepository.findById(id)
                .filter(o -> o.getMerchant().getId().equals(vendor.getId()))
                .filter(o -> o.getDiscountType() == Offer.DiscountType.FREE_PRODUCT)
                .orElseThrow(() -> new ResourceNotFoundException("Gift offer not found"));
    }

    private void mapRegularOffer(OfferRequestDTO dto, Offer offer, User vendor) {
        offer.setTitle(dto.title());
        offer.setCode(dto.code().toUpperCase().trim());
        offer.setDiscountType(Offer.DiscountType.valueOf(dto.discountType()));

        offer.setDiscountPercent(dto.discountPercent());
        offer.setMinOrderAmount(dto.minOrderAmount());

        offer.setCustomerGroup(Offer.CustomerGroup.valueOf(dto.customerGroup()));
        offer.setCustomerType(Offer.CustomerType.valueOf(dto.customerType()));

        offer.setStartDate(dto.startDate());
        offer.setEndDate(dto.endDate());

        offer.setMerchant(vendor);
        offer.setActive(true);
    }

    private OfferResponseDTO mapOfferResponse(Offer offer) {
        return new OfferResponseDTO(
                offer.getId(),
                offer.getTitle(),
                offer.getCode(),
                offer.getDiscountType().name(),

                null, // originalPrice - not stored in entity anymore
                null, // offerPrice    - not stored in entity anymore
                false, // free         - only for free gift

                offer.getDiscountPercent(),
                offer.getMinOrderAmount(),

                offer.getCustomerGroup().name(),
                offer.getCustomerType().name(),
                offer.getStartDate(),
                offer.getEndDate(),
                offer.isActive()
        );
    }

    private FreeGiftResponseDTO mapFreeGiftResponse(Offer offer) {
        OfferPrice price = offer.getPrice();  // This should not be null if saved correctly

        BigDecimal originalPrice = price != null ? price.getOriginalPrice() : null;
        BigDecimal offerPrice = price != null ? price.getOfferPrice() : null;
        boolean free = price != null && price.isFree();

        return new FreeGiftResponseDTO(
                offer.getId(),
                offer.getFreeProductName(),
                offer.getFreeProductImageUrl(),
                offer.getFreeProductDescription(),
                offer.getFreeProductQuantity(),
                originalPrice,
                offer.getMinPurchaseAmount(),
                offerPrice,
                free,
                offer.isActive()
        );
    }
    // =====================================================
    // VALIDATION
    // =====================================================

    private void validateFreeGiftPrice(BigDecimal originalPrice, BigDecimal offerPrice, boolean free) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Original price must be greater than 0");
        }

        if (free) {
            // Allow offerPrice to be null or 0
            if (offerPrice != null && offerPrice.compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalArgumentException("Offer price must be 0 or not provided for FREE gifts");
            }
        } else {
            if (offerPrice == null || offerPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Offer price must be greater than 0 for paid gifts");
            }
            if (offerPrice.compareTo(originalPrice) >= 0) {
                throw new IllegalArgumentException("Offer price must be less than original price");
            }
        }
    }
}