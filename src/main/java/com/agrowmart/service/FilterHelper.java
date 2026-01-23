// src/main/java/com/agrowmart/service/FilterHelper.java

package com.agrowmart.service;

import java.math.BigDecimal;

public class FilterHelper {

    public static int comparePrice(BigDecimal p1, BigDecimal p2, boolean ascending) {
        p1 = p1 != null ? p1 : BigDecimal.ZERO;
        p2 = p2 != null ? p2 : BigDecimal.ZERO;
        return ascending ? p1.compareTo(p2) : p2.compareTo(p1);
    }

    public static int compareRating(BigDecimal r1, BigDecimal r2, boolean ascending) {
        r1 = r1 != null ? r1 : BigDecimal.ZERO;
        r2 = r2 != null ? r2 : BigDecimal.ZERO;
        return ascending ? r1.compareTo(r2) : r2.compareTo(r1);
    }
}