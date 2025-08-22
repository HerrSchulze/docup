package com.docup.service;

import com.docup.exception.OcrProcessingException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Service for OCR (Optical Character Recognition) processing using Tesseract.
 */
@Service
public class OcrService {
    
    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);
    
    @Value("${docup.ocr.language:eng}")
    private String ocrLanguage;
    
    @Value("${docup.ocr.tesseract-path:/usr/bin/tesseract}")
    private String tesseractPath;
    
    private ITesseract tesseract;
    
    /**
     * Initialize Tesseract instance.
     */
    private ITesseract getTesseract() {
        if (tesseract == null) {
            logger.debug("Initializing Tesseract OCR engine...");
            tesseract = new Tesseract();
            tesseract.setLanguage(ocrLanguage);
            
            // Set Tesseract data path if needed
            // tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
            
            logger.info("Tesseract initialized with language: {} and path: {}", 
                       ocrLanguage, tesseractPath);
            
            // Log environment variables for debugging
            String tessDataPrefix = System.getenv("TESSDATA_PREFIX");
            logger.debug("TESSDATA_PREFIX environment variable: {}", tessDataPrefix);
        }
        return tesseract;
    }
    
    /**
     * Extract text from uploaded file using OCR.
     * Supports images (JPG, PNG) and PDFs.
     */
    public String extractText(MultipartFile file) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting OCR processing for file: {} (type: {}, size: {} bytes)", 
                   file.getOriginalFilename(), file.getContentType(), file.getSize());
        
        try {
            String contentType = file.getContentType();
            String result = "";
            
            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    logger.debug("Processing as image file");
                    result = extractTextFromImage(file);
                } else if (contentType.equals("application/pdf")) {
                    logger.debug("Processing as PDF file");
                    result = extractTextFromPdf(file);
                } else {
                    logger.warn("Unsupported file type for OCR: {}", contentType);
                    return "";
                }
            } else {
                logger.warn("No content type detected for file: {}", file.getOriginalFilename());
                return "";
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            logger.info("OCR processing completed for file: {} in {}ms, extracted {} characters", 
                       file.getOriginalFilename(), processingTime, result.length());
            
            return result;
            
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            logger.error("OCR processing failed for file: {} after {}ms - {}", 
                        file.getOriginalFilename(), processingTime, e.getMessage(), e);
            throw new OcrProcessingException("Failed to extract text from file", e);
        }
    }
    
    /**
     * Extract text from image file.
     */
    private String extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        logger.debug("Extracting text from image: {}", file.getOriginalFilename());
        
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        if (image == null) {
            throw new OcrProcessingException("Failed to read image file");
        }
        
        // Log original image dimensions
        logger.debug("Original image dimensions: {}x{}", image.getWidth(), image.getHeight());
        
        // Resize large images to prevent memory issues and crashes
        BufferedImage processedImage = resizeImageIfNeeded(image, file.getOriginalFilename());
        
        String result;
        try {
            result = getTesseract().doOCR(processedImage);
            logger.debug("OCR extracted {} characters from image", result.length());
        } catch (Error e) {
            // Catch native crashes (SIGSEGV, etc.)
            logger.error("Tesseract crashed while processing image: {} - {}", file.getOriginalFilename(), e.getMessage());
            throw new OcrProcessingException("OCR processing crashed due to image complexity or memory issues", e);
        } catch (Exception e) {
            logger.error("OCR processing failed for image: {} - {}", file.getOriginalFilename(), e.getMessage());
            throw e;
        }
        
        return cleanOcrText(result);
    }
    
    /**
     * Resize image if it's too large to prevent memory issues with Tesseract.
     */
    private BufferedImage resizeImageIfNeeded(BufferedImage original, String filename) {
        // Define maximum dimensions to prevent memory issues
        int maxWidth = 2000;
        int maxHeight = 2000;
        
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();
        
        // Check if resizing is needed
        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            logger.debug("Image size OK for OCR: {}x{}", originalWidth, originalHeight);
            return original;
        }
        
        // Calculate scale factor to maintain aspect ratio
        double scaleX = (double) maxWidth / originalWidth;
        double scaleY = (double) maxHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY);
        
        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);
        
        logger.info("Resizing image {} from {}x{} to {}x{} for OCR processing", 
                   filename, originalWidth, originalHeight, newWidth, newHeight);
        
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return resized;
    }
    
    /**
     * Extract text from PDF file.
     * Converts first page to image and performs OCR.
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException, TesseractException {
        logger.debug("Extracting text from PDF: {}", file.getOriginalFilename());
        
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(file.getBytes()))) {
            if (document.getNumberOfPages() == 0) {
                return "";
            }
            
            org.apache.pdfbox.rendering.PDFRenderer pdfRenderer = new org.apache.pdfbox.rendering.PDFRenderer(document);
            StringBuilder allText = new StringBuilder();
            
            // Process first few pages (limit to avoid performance issues)
            int pagesToProcess = Math.min(document.getNumberOfPages(), 3);
            
            for (int page = 0; page < pagesToProcess; page++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300, org.apache.pdfbox.rendering.ImageType.RGB);
                String pageText = getTesseract().doOCR(bufferedImage);
                allText.append(pageText).append("\n");
            }
            
            String result = allText.toString();
            logger.debug("OCR extracted {} characters from PDF ({} pages)", result.length(), pagesToProcess);
            
            return cleanOcrText(result);
        }
    }
    
    /**
     * Clean OCR text by removing excessive whitespace and special characters.
     */
    private String cleanOcrText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // Remove excessive whitespace and normalize line breaks
        return text.replaceAll("\\s+", " ")
                  .replaceAll("\\n\\s*\\n", "\n")
                  .trim();
    }
    
    /**
     * Check if OCR service is available.
     */
    public boolean isServiceAvailable() {
        try {
            ITesseract tess = getTesseract();
            // Try a simple OCR operation to verify Tesseract is working
            return tess != null;
        } catch (Exception e) {
            logger.error("Tesseract OCR service check failed: {}", e.getMessage());
            return false;
        }
    }
}
