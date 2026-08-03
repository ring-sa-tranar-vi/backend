package dev.salt.Ring20.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Value("${app.firebase.config-json:}")
    private String firebaseConfigJson;

    @PostConstruct
    public void init() throws Exception {
        if (firebaseConfigJson == null || firebaseConfigJson.isBlank()) {
            return;
        }

        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        ByteArrayInputStream serviceAccount =
                new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));

        FirebaseOptions options =
                FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
        FirebaseApp.initializeApp(options);
    }
}
