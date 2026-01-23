// src/main/java/com/agrowmart/service/ProductSpecifications.java

package com.agrowmart.service;

import com.agrowmart.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), Product.ProductStatus.ACTIVE);
    }

    public static Specification<Product> inCategories(List<String> categories) {
        return (root, query, cb) -> root.get("category").get("name").in(categories);
    }

    public static Specification<Product> isInStock(boolean inStock) {
        return (root, query, cb) -> cb.equal(root.get("inStock"), inStock);
    }
}