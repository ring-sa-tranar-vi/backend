package dev.salt.Ring20.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
@Profile("!local")
public class GcsStorageService implements FileStorageService {

    private final Storage storage;
    private final String bucketName;

    public GcsStorageService(@Value("${gcp.storage.bucket-name}") String bucketName) {
        storage = StorageOptions.getDefaultInstance().getService();
        this.bucketName = bucketName;
    }

    @Override
    public String getFileAccess(String filePath, int validForMinutes) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, filePath).build();
        URL signedUrl = storage.signUrl(
                blobInfo, validForMinutes, TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature()
        );

        return signedUrl.toString();
    }
}
