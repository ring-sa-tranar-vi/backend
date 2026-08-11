package dev.salt.Ring20.service.storage;

public interface FileStorageService {

    String getFileAccess(String filePath, int validForMinutes);
}
