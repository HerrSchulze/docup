package com.docup.util;

/**
 * Security utility class for sanitizing inputs to prevent various injection attacks.
 */
public class SecurityUtil {
    
    /**
     * Sanitize log message parameters to prevent CRLF injection attacks.
     * This method removes or escapes carriage return and line feed characters
     * that could be used to inject malicious content into log files.
     * 
     * @param input The input string to sanitize
     * @return Sanitized string safe for logging
     */
    public static String sanitizeForLog(Object input) {
        if (input == null) {
            return "null";
        }
        
        String str = input.toString();
        
        // Remove or replace potentially dangerous characters
        return str.replaceAll("[\r\n\t]", "_")
                  .replaceAll("\\p{Cntrl}", "_"); // Replace all control characters
    }
    
    /**
     * Sanitize filename to prevent path traversal and other filename-based attacks.
     * 
     * @param filename The filename to sanitize
     * @return Sanitized filename
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "unknown";
        }
        
        // Remove path traversal patterns and dangerous characters
        return filename.replaceAll("[\\\\/:*?\"<>|\\r\\n\\t]", "_")
                      .replaceAll("\\.\\.", "_")
                      .replaceAll("^\\.", "_") // Don't start with dot
                      .trim();
    }
    
    /**
     * Validate that a string doesn't contain null bytes.
     * 
     * @param input The input to validate
     * @throws SecurityException if null bytes are found
     */
    public static void validateNoNullBytes(String input) {
        if (input != null && input.contains("\0")) {
            throw new SecurityException("Input contains null bytes");
        }
    }
}
