package dev.salt.Ring20.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class LocalStorageService implements FileStorageService {

    private final String serverPort;

    public LocalStorageService(@Value("${server.port}") String serverPort) {
        this.serverPort = serverPort;
    }

    // TODO: constants
    @Override
    public String getFileAccess(String filePath, int validForMinutes) {
        if (filePath == null || filePath.isBlank()) {
            return filePath;
        }
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            return filePath;
        }
        return "http://localhost:" + serverPort + "/local-storage/" + filePath;
    }
}
