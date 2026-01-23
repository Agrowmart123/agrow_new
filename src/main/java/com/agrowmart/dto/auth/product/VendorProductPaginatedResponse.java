// src/main/java/com/agrowmart/dto/auth/product/VendorProductPaginatedResponse.java

package com.agrowmart.dto.auth.product;

import java.util.List;

public class VendorProductPaginatedResponse {
    private List<ProductResponseDTO> products;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int pageSize;

    public VendorProductPaginatedResponse() {}

    public VendorProductPaginatedResponse(List<ProductResponseDTO> products, int currentPage,
                                            int totalPages, long totalItems, int pageSize) {
        this.products = products;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.pageSize = pageSize;
    }

    // Getters and Setters
    public List<ProductResponseDTO> getProducts() { return products; }
    public void setProducts(List<ProductResponseDTO> products) { this.products = products; }
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}