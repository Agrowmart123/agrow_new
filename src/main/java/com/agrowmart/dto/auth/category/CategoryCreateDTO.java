package com.agrowmart.dto.auth.category;
//
public record CategoryCreateDTO(
        String name,
        String slug,      // REQUIRED
        Long parentId     // Optional
) {}

