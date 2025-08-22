package com.docup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for DocUp backend service.
 * Provides file upload functionality with OCR and virus scanning capabilities.
 */
@SpringBootApplication
public class DocUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocUpApplication.class, args);
    }
}
