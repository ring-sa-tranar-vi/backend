package dev.salt.Ring20.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LocalStorageServiceTest {

    private final LocalStorageService storageService = new LocalStorageService("8080");

    @Test
    void prefixesRelativePathsWithLocalStorageUrl() {
        assertThat(storageService.getFileAccess("trainers/eva/image.png", 15))
                .isEqualTo("http://localhost:8080/local-storage/trainers/eva/image.png");
    }

    @Test
    void leavesExternalUrlsUnchanged() {
        String url = "https://example.supabase.co/storage/v1/object/public/audio_files/workout.png";

        assertThat(storageService.getFileAccess(url, 15)).isEqualTo(url);
    }

    @Test
    void leavesMissingPathsUnchanged() {
        assertThat(storageService.getFileAccess(null, 15)).isNull();
        assertThat(storageService.getFileAccess("", 15)).isEmpty();
    }
}
