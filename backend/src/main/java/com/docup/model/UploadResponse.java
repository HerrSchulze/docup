package com.docup.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Response model for file upload operations.
 * Contains file metadata and processing results.
 */
public class UploadResponse {
    
    private String filename;
    private long size;
    private String mimeType;
    private String ocrText;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime uploadedAt;
    
    private boolean virusScanPassed;
    private String storagePath;

    public UploadResponse() {}

    public UploadResponse(String filename, long size, String mimeType, String ocrText, 
                         LocalDateTime uploadedAt, boolean virusScanPassed, String storagePath) {
        this.filename = filename;
        this.size = size;
        this.mimeType = mimeType;
        this.ocrText = ocrText;
        this.uploadedAt = uploadedAt;
        this.virusScanPassed = virusScanPassed;
        this.storagePath = storagePath;
    }

    // Getters and Setters
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getOcrText() {
        return ocrText;
    }

    public void setOcrText(String ocrText) {
        this.ocrText = ocrText;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public boolean isVirusScanPassed() {
        return virusScanPassed;
    }

    public void setVirusScanPassed(boolean virusScanPassed) {
        this.virusScanPassed = virusScanPassed;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
}
