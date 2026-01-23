//
//package com.agrowmart.service;
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import jakarta.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Service
//public class CloudinaryService {
//
//    private Cloudinary cloudinary;
//
//    @Value("${cloudinary.cloud-name}")
//    private String cloudName;
//
//    @Value("${cloudinary.api-key}")
//    private String apiKey;
//
//    @Value("${cloudinary.api-secret}")
//    private String apiSecret;
//
//    @Value("${cloudinary.folder:agrowmart}") // default folder
//    private String folder;
//
//    @PostConstruct
//    private void init() {
//        cloudinary = new Cloudinary(ObjectUtils.asMap(
//                "cloud_name", cloudName,
//                "api_key", apiKey,
//                "api_secret", apiSecret,
//                "secure", true
//        ));
//    }
//
//    // -------------------------------------------------------
//    //  UPLOAD IMAGE WITH VALIDATION (2 MB LIMIT)
//    // -------------------------------------------------------
//    public String upload(MultipartFile file) throws IOException {
//
//        if (file == null || file.isEmpty()) {
//            throw new IllegalArgumentException("File is required");
//        }
//
//        // ----- Validate Type (only jpg/jpeg/png) -----
//        String contentType = file.getContentType();
//        if (contentType == null ||
//                !(contentType.equals("image/png") ||
//                  contentType.equals("image/jpeg") ||
//                  contentType.equals("image/jpg"))) {
//
//            throw new IllegalArgumentException("Only PNG or JPG images are allowed");
//        }
//
//        // ----- Validate Max Size = 2MB -----
//        long maxSize = 2 * 1024 * 1024; // 2MB
//        if (file.getSize() > maxSize) {
//            throw new IllegalArgumentException("File too large. Maximum allowed size is 2 MB.");
//        }
//
//        // ----- Upload -----
//        Map<String, Object> options = ObjectUtils.asMap(
//                "resource_type", "auto",
//                "folder", folder
//        );
//
//        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
//        return (String) uploadResult.get("secure_url");
//    }
//
//    // -------------------------------------------------------
//    //  DELETE IMAGE FROM CLOUDINARY
//    // -------------------------------------------------------
//    public void delete(String imageUrlOrPublicId) {
//
//        if (imageUrlOrPublicId == null || imageUrlOrPublicId.trim().isEmpty()) {
//            return;
//        }
//
//        String publicId = extractPublicId(imageUrlOrPublicId);
//
//        if (publicId == null) {
//            System.err.println("Failed to extract public_id from: " + imageUrlOrPublicId);
//            return;
//        }
//
//        try {
//            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap());
//            System.out.println("Deleted from Cloudinary: " + result.get("result"));
//        } catch (Exception e) {
//            System.err.println("Cloudinary delete failed: " + e.getMessage());
//        }
//    }
//
//    // -------------------------------------------------------
//    //  EXTRACT PUBLIC ID FROM CLOUDINARY URL
//    // -------------------------------------------------------
//    private String extractPublicId(String urlOrId) {
//
//        if (urlOrId == null || urlOrId.isBlank()) return null;
//
//        // If only public_id (no URL)
//        if (!urlOrId.contains("http")) {
//            return urlOrId.contains(".")
//                    ? urlOrId.substring(0, urlOrId.lastIndexOf('.'))
//                    : urlOrId;
//        }
//
//        // Regex method
//        String pattern = "\\/v\\d+\\/(.+?)\\.";
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(urlOrId);
//
//        if (m.find()) {
//            return m.group(1);
//        }
//
//        // Fallback
//        try {
//            String[] parts = urlOrId.split("/upload/");
//            if (parts.length > 1) {
//                String path = parts[1].split("\\?")[0];
//                return path.substring(0, path.lastIndexOf('.'));
//            }
//        } catch (Exception ignored) {}
//
//        return null;
//    }
//}



//------------------



//-




package com.agrowmart.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CloudinaryService {

    private Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Value("${cloudinary.folder:agrowmart}") // default folder
    private String folder;

    @PostConstruct
    private void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    // -------------------------------------------------------
    //  UPLOAD IMAGE WITH VALIDATION (2 MB LIMIT)
    // -------------------------------------------------------
    public String upload(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        // ----- Validate Type (only jpg/jpeg/png) -----
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png") ||
                  contentType.equals("image/jpeg") ||
                  contentType.equals("image/jpg"))) {

            throw new IllegalArgumentException("Only PNG or JPG images are allowed");
        }

        // ----- Validate Max Size = 2MB -----
        long maxSize = 2 * 1024 * 1024; // 2MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File too large. Maximum allowed size is 2 MB.");
        }

        // ----- Upload -----
        Map<String, Object> options = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", folder
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) uploadResult.get("secure_url");
    }

    // -------------------------------------------------------
    //  DELETE IMAGE FROM CLOUDINARY
    // -------------------------------------------------------
    public void delete(String imageUrlOrPublicId) {

        if (imageUrlOrPublicId == null || imageUrlOrPublicId.trim().isEmpty()) {
            return;
        }

        String publicId = extractPublicId(imageUrlOrPublicId);

        if (publicId == null) {
            System.err.println("Failed to extract public_id from: " + imageUrlOrPublicId);
            return;
        }

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap());
            System.out.println("Deleted from Cloudinary: " + result.get("result"));
        } catch (Exception e) {
            System.err.println("Cloudinary delete failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    //  EXTRACT PUBLIC ID FROM CLOUDINARY URL
    // -------------------------------------------------------
    private String extractPublicId(String urlOrId) {

        if (urlOrId == null || urlOrId.isBlank()) return null;

        // If only public_id (no URL)
        if (!urlOrId.contains("http")) {
            return urlOrId.contains(".")
                    ? urlOrId.substring(0, urlOrId.lastIndexOf('.'))
                    : urlOrId;
        }

        // Regex method
        String pattern = "\\/v\\d+\\/(.+?)\\.";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(urlOrId);

        if (m.find()) {
            return m.group(1);
        }

        // Fallback
        try {
            String[] parts = urlOrId.split("/upload/");
            if (parts.length > 1) {
                String path = parts[1].split("\\?")[0];
                return path.substring(0, path.lastIndexOf('.'));
            }
        } catch (Exception ignored) {}

        return null;
    }
}