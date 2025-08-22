package com.docup.controller;

import com.docup.exception.FileStorageException;
import com.docup.exception.OcrProcessingException;
import com.docup.exception.VirusDetectedException;
import com.docup.model.UploadResponse;
import com.docup.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for file upload operations.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost"})
public class UploadController {
    
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    
    private final FileService fileService;
    
    @Autowired
    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }
    
    /**
     * Upload file endpoint.
     * Accepts multipart/form-data with 'file' parameter.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Please select a file to upload"));
            }
            
            logger.info("Received upload request for file: {} (size: {} bytes)", 
                       file.getOriginalFilename(), file.getSize());
            
            UploadResponse response = fileService.processUpload(file);
            
            logger.info("File upload completed successfully: {}", response.getFilename());
            return ResponseEntity.ok(response);
            
        } catch (VirusDetectedException e) {
            logger.warn("Virus detected in uploaded file: {} - {}", 
                       file.getOriginalFilename(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse("File rejected: " + e.getMessage()));
                
        } catch (FileStorageException e) {
            logger.error("File storage error for file: {} - {}", 
                        file.getOriginalFilename(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Upload failed: " + e.getMessage()));
                
        } catch (OcrProcessingException e) {
            logger.warn("OCR processing failed for file: {} - {}", 
                       file.getOriginalFilename(), e.getMessage());
            // Still proceed with upload, just note the OCR failure
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .body(createErrorResponse("Upload successful but OCR failed: " + e.getMessage()));
                
        } catch (Exception e) {
            logger.error("Unexpected error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal server error occurred"));
        }
    }
    
    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("services", Map.of(
            "fileService", fileService.isHealthy() ? "UP" : "DOWN"
        ));
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Get upload status/info endpoint.
     */
    @GetMapping("/upload/info")
    public ResponseEntity<Map<String, Object>> getUploadInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("maxFileSize", "10MB");
        info.put("allowedTypes", new String[]{"jpg", "jpeg", "png", "pdf"});
        info.put("features", new String[]{"virus-scan", "ocr", "preview"});
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * Create standardized error response.
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
