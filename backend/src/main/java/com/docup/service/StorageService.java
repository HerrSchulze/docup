package com.docup.service;

import com.docup.config.FileStorageConfig;
import com.docup.exception.FileStorageException;
import com.docup.model.FileMetadata;
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
        this.uploadPath = Paths.get(config.getPath()).toAbsolutePath().normalize();
        initializeStorage();
    }
    
    /**
     * Initialize storage directory structure.
     */
    private void initializeStorage() {
        try {
            Files.createDirectories(uploadPath);
            logger.info("Storage initialized at: {}", uploadPath);
            logger.debug("Storage directory permissions: readable={}, writable={}", 
                        Files.isReadable(uploadPath), Files.isWritable(uploadPath));
        } catch (IOException e) {
            logger.error("Failed to initialize storage directory: {}", uploadPath, e);
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
        
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!config.getAllowedTypes().contains(extension)) {
            logger.warn("Validation failed: File type '{}' not in allowed types: {}", 
                       extension, config.getAllowedTypes());
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
        String extension = FilenameUtils.getExtension(originalFilename);
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String uuid = UUID.randomUUID().toString();
        
        return String.format("%s_%s.%s", uuid, baseName, extension);
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
     * Calculate MD5 checksum of the uploaded file.
     */
    private String calculateChecksum(MultipartFile file) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(file.getBytes());
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
