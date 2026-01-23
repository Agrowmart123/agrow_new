package com.agrowmart.dto.auth.product;


import java.util.List;

public record ProductFilterDTO(
 String sortBy,
 List<String> categories,
 Boolean inStock,
 Double lat,
 Double lon,
 String distanceFilter
) {}