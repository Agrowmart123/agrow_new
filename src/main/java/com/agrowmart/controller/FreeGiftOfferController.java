package com.agrowmart.controller;

import com.agrowmart.dto.auth.offer.FreeGiftRequestDTO;
import com.agrowmart.dto.auth.offer.FreeGiftResponseDTO;
import com.agrowmart.entity.User;
import com.agrowmart.service.OfferService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/vendor/free-gift-offer")
public class FreeGiftOfferController {

    private final OfferService offerService;

    public FreeGiftOfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FreeGiftResponseDTO> create(
            @AuthenticationPrincipal User vendor,
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("quantity") String quantity,
            @RequestParam("originalPrice") BigDecimal originalPrice,
            @RequestParam(value = "offerPrice", required = false) BigDecimal offerPrice,
            @RequestParam("free") Boolean free,
            @RequestParam("minPurchaseAmount") BigDecimal minPurchaseAmount,
            @RequestParam("image") MultipartFile image) {  // ← Remove "value=" and "required=false"

        if (free == null) {
            throw new IllegalArgumentException("The 'free' field is required (true or false)");
        }

        FreeGiftRequestDTO dto = new FreeGiftRequestDTO(
                productName, description, quantity, originalPrice, offerPrice, free, minPurchaseAmount
        );

        FreeGiftResponseDTO response = offerService.createFreeGiftOffer(vendor, dto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FreeGiftResponseDTO>> getMyOffers(@AuthenticationPrincipal User vendor) {
        List<FreeGiftResponseDTO> offers = offerService.getMyFreeGiftOffers(vendor);
        return ResponseEntity.ok(offers);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FreeGiftResponseDTO> update(
            @AuthenticationPrincipal User vendor,
            @PathVariable Long id,
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("quantity") String quantity,
            @RequestParam("originalPrice") BigDecimal originalPrice,
            @RequestParam(value = "offerPrice", required = false) BigDecimal offerPrice,
            @RequestParam("free") boolean free,
            @RequestParam("minPurchaseAmount") BigDecimal minPurchaseAmount,
            @RequestParam(value = "image") MultipartFile image) {

        FreeGiftRequestDTO dto = new FreeGiftRequestDTO(
                productName,
                description,
                quantity,
                originalPrice,
                offerPrice,
                free,
                minPurchaseAmount
        );

        FreeGiftResponseDTO response = offerService.updateFreeGiftOffer(vendor, id, dto, image);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @AuthenticationPrincipal User vendor,
            @PathVariable Long id) {
        offerService.deactivateFreeGiftOffer(vendor, id);
        return ResponseEntity.noContent().build();
    }
}