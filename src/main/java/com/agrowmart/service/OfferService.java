package com.agrowmart.service;

import com.agrowmart.dto.auth.offer.*;
import com.agrowmart.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OfferService {

    OfferResponseDTO createOffer(User vendor, OfferRequestDTO dto);
    List<OfferResponseDTO> getMyOffers(User vendor);
    OfferResponseDTO updateOffer(User vendor, Long id, OfferRequestDTO dto);
    void deactivate(User vendor, Long id);

    // FREE GIFT
    FreeGiftResponseDTO createFreeGiftOffer(User vendor, FreeGiftRequestDTO dto, MultipartFile image);
    List<FreeGiftResponseDTO> getMyFreeGiftOffers(User vendor);
    FreeGiftResponseDTO updateFreeGiftOffer(User vendor, Long id, FreeGiftRequestDTO dto, MultipartFile image);
    void deactivateFreeGiftOffer(User vendor, Long id);
}
