// com.agrowmart.dto.auth.product.ProductApprovalRequest.java
package com.agrowmart.dto.auth.product;

public record ProductApprovalRequest(
    String action,           // "APPROVE" / "REJECT" / "DELETE"
    String rejectionReason   // optional
) {}