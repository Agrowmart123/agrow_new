package com.agrowmart.util;

//src/main/java/com/agrowmart/util/MaskingUtil.java

public class MaskingUtil {

 public static String maskAadhaar(String aadhaar) {
     if (aadhaar == null || aadhaar.trim().isEmpty() || aadhaar.length() < 4) {
         return "N/A";
     }
     return "********" + aadhaar.substring(aadhaar.length() - 4);
 }

 public static String maskPan(String pan) {
     if (pan == null || pan.trim().isEmpty() || pan.length() < 4) {
         return "N/A";
     }
     // Standard PAN format: ABCDE1234F → show first 5 + last 1
     return pan.substring(0, 5) + "****" + pan.charAt(pan.length() - 1);
 }

 public static String maskBankAccount(String accountNo) {
     if (accountNo == null || accountNo.trim().isEmpty() || accountNo.length() < 4) {
         return "N/A";
     }
     return "****" + accountNo.substring(accountNo.length() - 4);
 }
}