package com.docup.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration properties for file storage settings.
 */
@Configuration
@ConfigurationProperties(prefix = "docup.upload")
public class FileStorageConfig {
    
    private String path = "./uploads";
    private List<String> allowedTypes = List.of("jpg", "jpeg", "png", "pdf");
    private long maxFileSize = 10485760L; // 10MB
    
    // Getters and Setters
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public List<String> getAllowedTypes() {
        return allowedTypes;
    }
    
    public void setAllowedTypes(List<String> allowedTypes) {
        this.allowedTypes = allowedTypes;
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
