package com.docup.model;

import java.time.LocalDateTime;

/**
 * File metadata model containing information about uploaded files.
 */
public class FileMetadata {
    
    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private long size;
    private String storagePath;
    private LocalDateTime uploadTime;
    private String checksum;

    public FileMetadata() {}

    public FileMetadata(String originalFilename, String storedFilename, String contentType, 
                       long size, String storagePath, LocalDateTime uploadTime, String checksum) {
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.contentType = contentType;
        this.size = size;
        this.storagePath = storagePath;
        this.uploadTime = uploadTime;
        this.checksum = checksum;
    }

    // Getters and Setters
    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
