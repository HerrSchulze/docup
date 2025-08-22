package com.docup.exception;

/**
 * Exception thrown when OCR processing fails.
 */
public class OcrProcessingException extends RuntimeException {
    
    public OcrProcessingException(String message) {
        super(message);
    }
    
    public OcrProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
