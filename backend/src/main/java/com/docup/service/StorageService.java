package com.docup.service;

import com.docup.config.FileStorageConfig;
import com.docup.exception.FileStorageException;
import com.docup.model.FileMetadata;
import com.docup.util.SecurityUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service for handling file storage operations.
 * Implements date-based directory structure: /uploads/{yyyy}/{MM}/{dd}/{uuid}_filename.ext
 */
@Service
public class StorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
    
    private final FileStorageConfig config;
    private final Path uploadPath;
    
    @Autowired
    public StorageService(FileStorageConfig config) {
        this.config = config;
        // Validate and normalize the upload path to prevent path traversal
        String configPath = config.getPath();
        if (configPath == null || configPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Upload path cannot be null or empty");
        }
        this.uploadPath = Paths.get(configPath).toAbsolutePath().normalize();
        // Additional security check to ensure path is within expected boundaries
        validateUploadPath(this.uploadPath);
        initializeStorage();
    }
    
    /**
     * Validate that the upload path is secure and within expected boundaries.
     */
    private void validateUploadPath(Path path) {
        // Ensure the path doesn't contain suspicious patterns
        String pathStr = path.toString();
        if (pathStr.contains("..") || pathStr.contains("~")) {
            throw new SecurityException("Invalid upload path detected: " + pathStr);
        }
    }
    
    /**
     * Initialize storage directory structure.
     */
    private void initializeStorage() {
        try {
            Files.createDirectories(uploadPath);
            logger.info("Storage initialized at: {}", SecurityUtil.sanitizeForLog(uploadPath));
            logger.debug("Storage directory permissions: readable={}, writable={}", 
                        Files.isReadable(uploadPath), Files.isWritable(uploadPath));
        } catch (IOException e) {
            logger.error("Failed to initialize storage directory: {}", SecurityUtil.sanitizeForLog(uploadPath), e);
            throw new FileStorageException("Could not create upload directory", e);
        }
    }
    
    /**
     * Store uploaded file with organized directory structure.
     */
    public FileMetadata storeFile(MultipartFile file) {
        logger.debug("Starting file storage process for: {} (size: {} bytes)", 
                    file.getOriginalFilename(), file.getSize());
        
        validateFile(file);
        
        LocalDateTime now = LocalDateTime.now();
        String storedFilename = generateStoredFilename(file.getOriginalFilename());
        Path datePath = createDateBasedPath(now);
        Path targetPath = datePath.resolve(storedFilename);
        
        logger.debug("Target storage path: {}", targetPath);
        
        try {
            Files.createDirectories(datePath);
            logger.debug("Created date directory: {}", datePath);
            
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("File copied to storage location");
            
            String checksum = calculateChecksum(file);
            logger.debug("File checksum calculated: {}", checksum);
            
            logger.info("File stored successfully: {} -> {}", 
                       file.getOriginalFilename(), targetPath);
            
            return new FileMetadata(
                file.getOriginalFilename(),
                storedFilename,
                file.getContentType(),
                file.getSize(),
                targetPath.toString(),
                now,
                checksum
            );
            
        } catch (IOException e) {
            logger.error("Failed to store file: {} at path: {}", 
                        file.getOriginalFilename(), targetPath, e);
            throw new FileStorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }
    
    /**
     * Validate uploaded file against configured constraints.
     */
    private void validateFile(MultipartFile file) {
        logger.debug("Validating file: {} (size: {} bytes, type: {})", 
                    file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        if (file.isEmpty()) {
            logger.warn("Validation failed: Empty file submitted");
            throw new FileStorageException("Cannot store empty file");
        }
        
        if (file.getSize() > config.getMaxFileSize()) {
            logger.warn("Validation failed: File size {} exceeds maximum {}", 
                       file.getSize(), config.getMaxFileSize());
            throw new FileStorageException("File size exceeds maximum allowed size");
        }
        
        // Use secure filename processing with null byte validation
        String originalFilename = file.getOriginalFilename();
        SecurityUtil.validateNoNullBytes(originalFilename);
        String extension = getSecureFileExtension(originalFilename).toLowerCase();
        if (!config.getAllowedTypes().contains(extension)) {
            logger.warn("Validation failed: File type '{}' not in allowed types: {}", 
                       SecurityUtil.sanitizeForLog(extension), config.getAllowedTypes());
            throw new FileStorageException("File type not allowed: " + extension);
        }
        
        String filename = file.getOriginalFilename();
        if (filename != null && filename.contains("..")) {
            logger.warn("Validation failed: Filename contains invalid path sequence: {}", filename);
            throw new FileStorageException("Filename contains invalid path sequence");
        }
        
        logger.debug("File validation passed for: {}", file.getOriginalFilename());
    }
    
    /**
     * Generate unique filename with UUID prefix.
     */
    private String generateStoredFilename(String originalFilename) {
        SecurityUtil.validateNoNullBytes(originalFilename);
        String extension = getSecureFileExtension(originalFilename);
        String baseName = getSecureBaseName(originalFilename);
        String uuid = UUID.randomUUID().toString();
        
        return String.format("%s_%s.%s", uuid, baseName, extension);
    }
    
    /**
     * Securely extract file extension without null byte vulnerabilities.
     */
    private String getSecureFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "";
        }
        
        // Remove null bytes and normalize
        String clean = filename.replaceAll("\0", "");
        int lastDot = clean.lastIndexOf('.');
        if (lastDot > 0 && lastDot < clean.length() - 1) {
            return clean.substring(lastDot + 1);
        }
        return "";
    }
    
    /**
     * Securely extract base filename without null byte vulnerabilities.
     */
    private String getSecureBaseName(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "unnamed";
        }
        
        // Remove null bytes and normalize
        String clean = filename.replaceAll("\0", "");
        int lastDot = clean.lastIndexOf('.');
        if (lastDot > 0) {
            return SecurityUtil.sanitizeFilename(clean.substring(0, lastDot));
        }
        return SecurityUtil.sanitizeFilename(clean);
    }
    
    /**
     * Create date-based directory path: {yyyy}/{MM}/{dd}
     */
    private Path createDateBasedPath(LocalDateTime dateTime) {
        String year = dateTime.format(DateTimeFormatter.ofPattern("yyyy"));
        String month = dateTime.format(DateTimeFormatter.ofPattern("MM"));
        String day = dateTime.format(DateTimeFormatter.ofPattern("dd"));
        
        return uploadPath.resolve(year).resolve(month).resolve(day);
    }
    
    /**
     * Calculate SHA-256 checksum of the uploaded file.
     */
    private String calculateChecksum(MultipartFile file) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] digest = sha256.digest(file.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.warn("Failed to calculate checksum for file: {}", file.getOriginalFilename(), e);
            return null;
        }
    }
    
    /**
     * Get file by path.
     */
    public Path getFile(String filename) {
        return uploadPath.resolve(filename).normalize();
    }
    
    /**
     * Check if file exists.
     */
    public boolean fileExists(String filename) {
        Path file = getFile(filename);
        return Files.exists(file) && Files.isRegularFile(file);
    }
}
