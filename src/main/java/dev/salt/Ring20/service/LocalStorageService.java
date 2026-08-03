package dev.salt.Ring20.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class LocalStorageService implements FileStorageService {

    private static final String PUBLIC_BASE_URL =
            "https://mizofvemlvooaycnevys.supabase.co/storage/v1/object/public/ringsatranarvi_files";

    @Override
    public String getFileAccess(String filePath, int validForMinutes) {
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            return filePath;
        }

        return PUBLIC_BASE_URL + "/" + filePath;
    }
}
