package com.docup.service;

import com.docup.model.FileMetadata;
import com.docup.model.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Main service for handling file upload operations.
 * Orchestrates virus scanning, OCR processing, and file storage.
 */
@Service
public class FileService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    
    private final StorageService storageService;
    private final VirusScanService virusScanService;
    private final OcrService ocrService;
    
    @Autowired
    public FileService(StorageService storageService, 
                      VirusScanService virusScanService, 
                      OcrService ocrService) {
        this.storageService = storageService;
        this.virusScanService = virusScanService;
        this.ocrService = ocrService;
    }
    
    /**
     * Process uploaded file: scan for viruses, extract text via OCR, and store.
     */
    public UploadResponse processUpload(MultipartFile file) {
        logger.info("Processing file upload: {} ({} bytes)", 
                   file.getOriginalFilename(), file.getSize());
        
        // Step 1: Virus scan
        boolean virusScanPassed = virusScanService.scanFile(file);
        logger.debug("Virus scan completed for file: {}", file.getOriginalFilename());
        
        // Step 2: OCR processing
        String ocrText = "";
        try {
            ocrText = ocrService.extractText(file);
            logger.debug("OCR processing completed for file: {}", file.getOriginalFilename());
        } catch (Exception e) {
            logger.warn("OCR processing failed for file: {} - {}", 
                       file.getOriginalFilename(), e.getMessage());
            // Continue with upload even if OCR fails
        }
        
        // Step 3: Store file
        FileMetadata metadata = storageService.storeFile(file);
        logger.info("File stored successfully: {}", metadata.getStoredFilename());
        
        // Step 4: Create response
        return new UploadResponse(
            metadata.getStoredFilename(),
            metadata.getSize(),
            metadata.getContentType(),
            ocrText,
            LocalDateTime.now(),
            virusScanPassed,
            metadata.getStoragePath()
        );
    }
    
    /**
     * Check service health status.
     */
    public boolean isHealthy() {
        return virusScanService.isServiceAvailable() && ocrService.isServiceAvailable();
    }
}
