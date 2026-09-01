package com.pamir.ppfarmsbackend.shared.storage.impl;

import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.BusinessRuleViolationException;
import com.pamir.ppfarmsbackend.shared.storage.StorageCategory;
import com.pamir.ppfarmsbackend.shared.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class SupabaseStorageServiceImpl implements StorageService {

    private final String supabaseUrl;
    private final String supabaseKey;
    private final String bucketAnimals;
    private final String bucketReceipts;
    private final String bucketReports;
    private final RestClient restClient;

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
    );
    private static final Set<String> ALLOWED_DOC_TYPES = Set.of(
            "application/pdf"
    );

    public SupabaseStorageServiceImpl(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-key}") String supabaseKey,
            @Value("${supabase.bucket.animals:farm-animals}") String bucketAnimals,
            @Value("${supabase.bucket.receipts:payment-proofs}") String bucketReceipts,
            @Value("${supabase.bucket.reports:farm-reports}") String bucketReports
    ) {
        this.supabaseUrl = supabaseUrl != null ? supabaseUrl.replaceAll("/$", "") : "";
        this.supabaseKey = supabaseKey;
        this.bucketAnimals = bucketAnimals;
        this.bucketReceipts = bucketReceipts;
        this.bucketReports = bucketReports;
        this.restClient = RestClient.builder().build();
    }

    @Override
    public String uploadFile(MultipartFile file, StorageCategory category, UUID organizationId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Cannot upload an empty file.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("File size exceeds the 5MB maximum limit. Selected size: "
                    + (file.getSize() / 1024 / 1024) + "MB");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "image/jpeg";
        }
        contentType = contentType.toLowerCase();

        boolean isImage = ALLOWED_IMAGE_TYPES.contains(contentType);
        boolean isDoc = ALLOWED_DOC_TYPES.contains(contentType);

        if (category == StorageCategory.REPORTS || category == StorageCategory.RECEIPTS) {
            if (!isImage && !isDoc) {
                throw new BadRequestException("Invalid file format. Only JPG, PNG, WEBP, and PDF files are allowed for receipts & reports.");
            }
        } else {
            if (!isImage) {
                throw new BadRequestException("Invalid file format. Only JPG, PNG, and WEBP image files are allowed.");
            }
        }

        String bucket = resolveBucket(category);
        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        String objectPath = buildObjectPath(category, organizationId, extension);

        try {
            byte[] fileBytes = file.getBytes();
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, objectPath);

            log.info("[STORAGE UPLOAD] Uploading to Supabase bucket='{}', path='{}', size={} bytes", bucket, objectPath, fileBytes.length);

            ResponseEntity<String> response = restClient.post()
                    .uri(uploadUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .header("x-upsert", "true")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileBytes)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucket, objectPath);
                log.info("[STORAGE SUCCESS] File successfully uploaded to {}", publicUrl);
                return publicUrl;
            } else {
                log.error("[STORAGE ERROR] Supabase storage upload failed with status: {}", response.getStatusCode());
                throw new BusinessRuleViolationException("Failed to upload image to cloud storage. Please try again.");
            }
        } catch (IOException e) {
            log.error("[STORAGE ERROR] Failed reading file bytes", e);
            throw new BusinessRuleViolationException("Could not process file upload: " + e.getMessage());
        } catch (Exception e) {
            log.error("[STORAGE EXCEPTION] Supabase upload error", e);
            throw new BusinessRuleViolationException("Storage upload service encountered an error: " + e.getMessage());
        }
    }

    @Override
    public void deleteFileByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        try {
            // Supabase URL format: https://<supabase-id>.supabase.co/storage/v1/object/public/<bucket>/<path>
            String prefix = supabaseUrl + "/storage/v1/object/public/";
            if (!fileUrl.startsWith(prefix)) {
                log.warn("[STORAGE DELETE] Ignored file deletion for foreign/non-Supabase URL: {}", fileUrl);
                return;
            }

            String remainder = fileUrl.substring(prefix.length());
            int slashIndex = remainder.indexOf('/');
            if (slashIndex == -1) {
                return;
            }

            String bucket = remainder.substring(0, slashIndex);
            String objectPath = remainder.substring(slashIndex + 1);

            String deleteEndpoint = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, objectPath);

            log.info("[STORAGE DELETE] Purging old file from Supabase: bucket='{}', path='{}'", bucket, objectPath);

            restClient.delete()
                    .uri(deleteEndpoint)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .retrieve()
                    .toBodilessEntity();

            log.info("[STORAGE DELETE SUCCESS] Successfully deleted file: {}", objectPath);
        } catch (Exception e) {
            log.warn("[STORAGE DELETE WARNING] Could not delete old file '{}': {}", fileUrl, e.getMessage());
        }
    }

    private String resolveBucket(StorageCategory category) {
        return switch (category) {
            case ANIMALS, AVATARS -> bucketAnimals;
            case RECEIPTS, SYSTEM_QR -> bucketReceipts;
            case REPORTS -> bucketReports;
        };
    }

    private String buildObjectPath(StorageCategory category, UUID organizationId, String extension) {
        String uniqueId = UUID.randomUUID().toString();
        String orgFolder = organizationId != null ? organizationId.toString() : "system";

        return switch (category) {
            case ANIMALS -> String.format("%s/animals/%s.%s", orgFolder, uniqueId, extension);
            case RECEIPTS -> String.format("%s/receipts/%s.%s", orgFolder, uniqueId, extension);
            case SYSTEM_QR -> String.format("system/bank-qr/%s.%s", uniqueId, extension);
            case AVATARS -> String.format("%s/avatars/%s.%s", orgFolder, uniqueId, extension);
            case REPORTS -> String.format("%s/reports/%s.%s", orgFolder, uniqueId, extension);
        };
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (ext.length() <= 5) {
                return ext;
            }
        }
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            case "application/pdf" -> "pdf";
            default -> "jpg";
        };
    }
}
