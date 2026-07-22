package dev.salt.Ring20.service;

public interface FileStorageService {

    String getFileAccess(String filePath, int validForMinutes);
}
