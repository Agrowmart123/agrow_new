//package com.agrowmart.controller;
//
//import com.agrowmart.dto.auth.offer.OfferRequestDTO;
//import com.agrowmart.dto.auth.offer.OfferResponseDTO;
//import com.agrowmart.entity.User;
//import com.agrowmart.service.OfferService;
//import com.agrowmart.service.ProductService;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/vendor/offers")
//@RequiredArgsConstructor
//public class VendorOfferController {
//
// private final OfferService offerService;
//
// public VendorOfferController(OfferService offerService) {
//     this.offerService = offerService;
// }
// 
// 
// @PostMapping
// public ResponseEntity<OfferResponseDTO> create(
//         @AuthenticationPrincipal User vendor,
//         @RequestBody OfferRequestDTO dto) {
//     return ResponseEntity.status(HttpStatus.CREATED)
//             .body(offerService.createOffer(vendor, dto));
// }
//
// @GetMapping
// public ResponseEntity<List<OfferResponseDTO>> getMyOffers(@AuthenticationPrincipal User vendor) {
//     return ResponseEntity.ok(offerService.getMyOffers(vendor));
// }
//
// @PutMapping("/{id}")
// public ResponseEntity<OfferResponseDTO> update(
//         @AuthenticationPrincipal User vendor,
//         @PathVariable Long id,
//         @RequestBody OfferRequestDTO dto) {
//     return ResponseEntity.ok(offerService.updateOffer(vendor, id, dto));
// }
//
// @DeleteMapping("/{id}")
// public ResponseEntity<Void> delete(
//         @AuthenticationPrincipal User vendor,
//         @PathVariable Long id) {
//     offerService.deactivate(vendor, id);
//     return ResponseEntity.noContent().build();
// }
//}

//--------
package com.agrowmart.controller;

import com.agrowmart.dto.auth.offer.OfferRequestDTO;
import com.agrowmart.dto.auth.offer.OfferResponseDTO;
import com.agrowmart.entity.User;
import com.agrowmart.service.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor/offers")
public class VendorOfferController {

    private final OfferService offerService;

    public VendorOfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<OfferResponseDTO> create(
            @AuthenticationPrincipal User vendor,
            @RequestBody OfferRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offerService.createOffer(vendor, dto));
    }

    @GetMapping
    public ResponseEntity<List<OfferResponseDTO>> getMyOffers(@AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(offerService.getMyOffers(vendor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferResponseDTO> update(
            @AuthenticationPrincipal User vendor,
            @PathVariable Long id,
            @RequestBody OfferRequestDTO dto) {
        return ResponseEntity.ok(offerService.updateOffer(vendor, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User vendor,
            @PathVariable Long id) {
        offerService.deactivate(vendor, id);
        return ResponseEntity.noContent().build();
    }
}