package com.docup.exception;

/**
 * Exception thrown when a virus is detected in an uploaded file.
 */
public class VirusDetectedException extends RuntimeException {
    
    private final String virusName;
    
    public VirusDetectedException(String message, String virusName) {
        super(message);
        this.virusName = virusName;
    }
    
    public String getVirusName() {
        return virusName;
    }
}
