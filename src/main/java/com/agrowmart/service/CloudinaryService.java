

package com.agrowmart.service;

import com.agrowmart.exception.AuthExceptions.FileUploadException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CloudinaryService {
	private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
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
//    public String upload(MultipartFile file) throws IOException {
//
//    	if (file == null || file.isEmpty()) {
//            throw new FileUploadException("File is required and cannot be empty");
//        }
//        // ----- Validate Type (only jpg/jpeg/png) -----
//        String contentType = file.getContentType();
//        if (contentType == null ||
//                !(contentType.equals("image/png") ||
//                  contentType.equals("image/jpeg") ||
//                  contentType.equals("image/jpg"))) {
//
//        	throw new FileUploadException("Only PNG or JPG images are allowed");
//        }
//
//        // ----- Validate Max Size = 2MB -----
//        long maxSize = 2 * 1024 * 1024; // 2MB
//        if (file.getSize() > maxSize) {
//        	throw new FileUploadException("File too large. Maximum allowed size is 2 MB.");
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

    
    
 // ──────────────────────────────────────────────
    // UPLOAD IMAGE WITH VALIDATION (2 MB LIMIT, only jpg/png)
    // ──────────────────────────────────────────────
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File is required and cannot be empty");
        }

        // Validate content type (only image/png, image/jpeg, image/jpg)
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png") ||
                  contentType.equals("image/jpeg") ||
                  contentType.equals("image/jpg"))) {
            throw new FileUploadException("Only PNG or JPG images are allowed");
        }

        // Validate size: max 2MB
        long maxSize = 2 * 1024 * 1024; // 2MB
        if (file.getSize() > maxSize) {
            throw new FileUploadException("File too large. Maximum allowed size is 2 MB.");
        }

        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", folder,
                    "overwrite", true,
                    "use_filename", true
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String secureUrl = (String) uploadResult.get("secure_url");

            if (secureUrl == null || secureUrl.isBlank()) {
                throw new FileUploadException("Upload succeeded but no secure URL returned");
            }

            log.info("Image uploaded successfully: {}", secureUrl);
            return secureUrl;

        } catch (IOException e) {
            log.error("IO error during Cloudinary upload", e);
            throw new FileUploadException("Failed to read file during upload", e);
        } catch (Exception e) {
            log.error("Cloudinary upload failed", e);
            throw new FileUploadException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }
    
    
    // -------------------------------------------------------
    //  DELETE IMAGE FROM CLOUDINARY
    // -------------------------------------------------------
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

    
    
    public void delete(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            log.warn("Delete called with empty/null public ID - ignored");
            return;
        }

        try {
            Map result = cloudinary.uploader().destroy(publicId.trim(), ObjectUtils.emptyMap());
            String status = (String) result.get("result");

            if ("ok".equals(status)) {
                log.info("Successfully deleted image from Cloudinary: {}", publicId);
            } else {
                log.warn("Cloudinary delete returned non-ok status: {} for public_id: {}", status, publicId);
            }

        } catch (Exception e) {
            log.error("Failed to delete image from Cloudinary: {}", publicId, e);
            // Optional: throw if you want to fail loudly
            // throw new FileUploadException("Failed to delete image from Cloudinary", e);
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
    
    
    
 // add this code in cloudinaryservice.java      
    public void deleteByUrl(String url) throws Exception {
    if (url == null || !url.contains("res.cloudinary.com")) return;
    
    // Extract public_id from URL (example logic - adjust to your format)
    String publicId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
}
}