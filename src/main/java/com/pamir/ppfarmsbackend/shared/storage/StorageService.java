package com.pamir.ppfarmsbackend.shared.storage;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface StorageService {
    /**
     * Uploads a file to Supabase Storage in an organization-scoped category folder.
     *
     * @param file the MultipartFile to upload
     * @param category the storage category (ANIMALS, RECEIPTS, SYSTEM_QR, etc.)
     * @param organizationId tenant organization UUID (or null for platform system assets)
     * @return the public/accessible URL of the stored file
     */
    String uploadFile(MultipartFile file, StorageCategory category, UUID organizationId);

    /**
     * Deletes a file from Supabase Storage given its full URL.
     *
     * @param fileUrl the stored file URL
     */
    void deleteFileByUrl(String fileUrl);
}
