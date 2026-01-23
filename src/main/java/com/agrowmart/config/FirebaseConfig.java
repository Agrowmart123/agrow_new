
package com.agrowmart.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import javax.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            var resource = new ClassPathResource("serviceAccountKey.json");
            System.out.println("Trying to load: " + resource.getURL());
            System.out.println("File exists: " + resource.exists());
            System.out.println("File size: " + resource.contentLength() + " bytes");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Firebase load failed – check file!", e);
        }
    }
}