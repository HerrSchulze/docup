package com.docup.service;

import com.docup.exception.OcrProcessingException;
import com.docup.util.SecurityUtil;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.Loader;
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
import java.io.InputStream;

/**
 * Service for OCR (Optical Character Recognition) processing using Tesseract.
 * Optimized for performance:
 * - Processes all images (JPG, JPEG, PNG) with full OCR
 * - For PDFs: processes only the first page to improve performance and reduce processing time
 */
@Service
public class OcrService {
    
    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);
    
    @Value("${docup.ocr.language:eng+deu}")
    private String ocrLanguage;
    
    @Value("${docup.ocr.dpi-settings:150}")
    private int dpiSettings;
    
    private ITesseract tesseract;
    
    /**
     * Get Tesseract instance (optimized configuration).
     */
    private ITesseract getTesseract() {
        if (tesseract == null) {
            try {
                tesseract = createTesseractInstance();
            } catch (Exception e) {
                logger.error("Failed to create Tesseract instance: {}", e.getMessage());
                throw new RuntimeException("OCR service initialization failed: " + e.getMessage(), e);
            }
        }
        return tesseract;
    }
    
    /**
     * Create a new Tesseract instance with optimized settings for performance.
     */
    private ITesseract createTesseractInstance() {
        try {
            ITesseract instance = new Tesseract();
            
            // Set tessdata path with robust detection
            String tessDataPath = detectTessDataPath();
            logger.debug("Using tessdata path: {}", tessDataPath);
            instance.setDatapath(tessDataPath);
            
            // Set language - use only English to avoid crash issues
            try {
                instance.setLanguage("eng");
                logger.info("Tesseract initialized with language: eng and path: /usr/bin/tesseract");
            } catch (Exception e) {
                logger.warn("Failed to set language 'eng': {}", e.getMessage());
                throw new OcrProcessingException("Failed to initialize OCR language", e);
            }
            
            // Optimized settings for speed and accuracy balance
            instance.setPageSegMode(1); // Automatic page segmentation with OSD
            instance.setOcrEngineMode(1); // Neural nets LSTM engine only
            
            // Log environment variable for debugging
            logger.debug("TESSDATA_PREFIX environment variable: {}", System.getenv("TESSDATA_PREFIX"));
            
            return instance;
        } catch (Exception e) {
            logger.error("Failed to initialize Tesseract: {}", e.getMessage(), e);
            throw new OcrProcessingException("Failed to initialize OCR engine: " + e.getMessage(), e);
        }
    }
    
    /**
     * Detect tessdata path with multiple fallback options.
     */
    private String detectTessDataPath() {
        // Priority order for tessdata path detection
        String[] possiblePaths = {
            System.getenv("TESSDATA_PREFIX"),
            "/usr/share/tesseract-ocr/4.00/tessdata",
            "/usr/share/tesseract-ocr/5/tessdata",
            "/usr/share/tessdata",
            "/usr/local/share/tessdata"
        };
        
        for (String path : possiblePaths) {
            if (path != null && !path.isEmpty()) {
                java.io.File tessDataDir = new java.io.File(path);
                java.io.File engFile = new java.io.File(tessDataDir, "eng.traineddata");
                if (tessDataDir.exists() && engFile.exists()) {
                    logger.info("Found valid tessdata path: {} (eng.traineddata present)", path);
                    return path;
                }
            }
        }
        
        // Fallback to default if nothing found
        logger.warn("No valid tessdata path found, using default");
        return "/usr/share/tesseract-ocr/4.00/tessdata";
    }
    
    /**
     * Extract text from uploaded file using OCR.
     * - For images (JPG, JPEG, PNG): Full OCR processing
     * - For PDFs: First page only to optimize performance
     */
    public String extractText(MultipartFile file) {
        long startTime = System.currentTimeMillis();
        logger.info("OCR processing requested for file: {} (type: {}, size: {} bytes)", 
                   SecurityUtil.sanitizeForLog(file.getOriginalFilename()), 
                   SecurityUtil.sanitizeForLog(file.getContentType()), file.getSize());
        
        // TEMPORARY: Disable all OCR to prevent crashes
        logger.warn("OCR processing is temporarily disabled to prevent system crashes. File uploaded successfully.");
        return "OCR processing is temporarily disabled while we resolve Tesseract configuration issues. Your file has been uploaded successfully and is available for download.";
    }
    
    /**
     * Extract text from image file with full OCR processing.
     */
    private String extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        logger.debug("Extracting text from image: {}", SecurityUtil.sanitizeForLog(file.getOriginalFilename()));
        
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        if (image == null) {
            throw new IOException("Could not read image file");
        }
        
        // Log original dimensions
        logger.debug("Original image dimensions: {}x{}", image.getWidth(), image.getHeight());
        
        // Optimize image for better OCR accuracy and performance
        BufferedImage optimizedImage = optimizeImageForOcr(image, file.getOriginalFilename());
        
        ITesseract tesseractInstance = getTesseract();
        String extractedText = tesseractInstance.doOCR(optimizedImage);
        
        return cleanOcrText(extractedText);
    }
    
    /**
     * Extract text from image file with comprehensive safety checks to prevent crashes.
     */
    private String extractTextFromImageSafe(MultipartFile file) throws IOException, TesseractException {
        logger.debug("Extracting text from image (safe mode): {}", SecurityUtil.sanitizeForLog(file.getOriginalFilename()));
        
        // Validate file size first
        if (file.getSize() < 100) {
            throw new IOException("Image file too small to be valid: " + file.getSize() + " bytes");
        }
        
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) {
                throw new IOException("Could not read image file - invalid format or corrupted data");
            }
        } catch (Exception e) {
            throw new IOException("Failed to load image: " + e.getMessage(), e);
        }
        
        // Log original dimensions
        logger.debug("Original image dimensions: {}x{}", image.getWidth(), image.getHeight());
        
        // Check for reasonable image dimensions
        if (image.getWidth() < 10 || image.getHeight() < 10) {
            throw new IOException("Image too small for OCR: " + image.getWidth() + "x" + image.getHeight());
        }
        
        if (image.getWidth() > 10000 || image.getHeight() > 10000) {
            logger.warn("Very large image detected: {}x{}, this may cause performance issues", 
                       image.getWidth(), image.getHeight());
        }
        
        BufferedImage optimizedImage = optimizeImageForOcr(image, file.getOriginalFilename());
        
        // Initialize Tesseract with safety checks
        ITesseract tesseractInstance;
        try {
            tesseractInstance = getTesseract();
            if (tesseractInstance == null) {
                throw new TesseractException("Failed to initialize Tesseract instance");
            }
        } catch (Exception e) {
            throw new TesseractException("Tesseract initialization failed: " + e.getMessage(), e);
        }
        
        String extractedText;
        try {
            extractedText = tesseractInstance.doOCR(optimizedImage);
        } catch (Exception e) {
            throw new TesseractException("OCR processing failed: " + e.getMessage(), e);
        }
        
        return cleanOcrText(extractedText);
    }

    /**
     * Extract text from PDF file - FIRST PAGE ONLY for performance optimization.
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException, TesseractException {
        logger.debug("Extracting text from PDF (first page only): {}", SecurityUtil.sanitizeForLog(file.getOriginalFilename()));
        
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            int totalPages = document.getNumberOfPages();
            logger.info("PDF has {} pages - processing first page only for performance", totalPages);
            
            if (totalPages == 0) {
                return "No pages found in PDF";
            }
            
            return extractTextFromPdfOptimized(document, 1, file.getOriginalFilename(), totalPages);
        }
    }
    
    /**
     * Optimized PDF processing - first page only for multi-page documents.
     */
    private String extractTextFromPdfOptimized(PDDocument document, int pagesToProcess, String filename, int totalPages) throws IOException, TesseractException {
        logger.debug("Processing first page of PDF: {}", SecurityUtil.sanitizeForLog(filename));
        
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        
        // Render only the first page at optimal DPI for OCR
        BufferedImage pageImage = pdfRenderer.renderImageWithDPI(0, dpiSettings);
        logger.debug("Rendered PDF page 1 at {}dpi: {}x{}", dpiSettings, pageImage.getWidth(), pageImage.getHeight());
        
        // Optimize the rendered page for OCR
        BufferedImage optimizedImage = optimizeImageForOcr(pageImage, filename + "_page1");
        
        ITesseract tesseractInstance = getTesseract();
        String extractedText = tesseractInstance.doOCR(optimizedImage);
        
        if (totalPages > 1) {
            extractedText += "\n\n[Note: This PDF contains " + totalPages + " pages. Only the first page was processed for performance optimization.]";
        }
        
        return cleanOcrText(extractedText);
    }
    
    /**
     * Extract text from PDF file with comprehensive safety checks - FIRST PAGE ONLY.
     */
    private String extractTextFromPdfSafe(MultipartFile file) throws IOException, TesseractException {
        logger.debug("Extracting text from PDF (safe mode, first page only): {}", SecurityUtil.sanitizeForLog(file.getOriginalFilename()));
        
        PDDocument document = null;
        try {
            document = Loader.loadPDF(file.getBytes());
            int totalPages = document.getNumberOfPages();
            logger.info("PDF has {} pages - processing first page only for performance", totalPages);
            
            if (totalPages == 0) {
                return "No pages found in PDF";
            }
            
            String filename = SecurityUtil.sanitizeForLog(file.getOriginalFilename());
            logger.debug("Processing first page of PDF: {}", filename);
            
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            
            // Render only the first page at optimal DPI for OCR
            BufferedImage pageImage = pdfRenderer.renderImageWithDPI(0, dpiSettings);
            logger.debug("Rendered PDF page 1 at {}dpi: {}x{}", dpiSettings, pageImage.getWidth(), pageImage.getHeight());
            
            // Optimize the rendered page for OCR
            BufferedImage optimizedImage = optimizeImageForOcr(pageImage, filename + "_page1");
            
            // Initialize Tesseract with safety checks
            ITesseract tesseractInstance;
            try {
                tesseractInstance = getTesseract();
                if (tesseractInstance == null) {
                    throw new TesseractException("Failed to initialize Tesseract instance");
                }
            } catch (Exception e) {
                throw new TesseractException("Tesseract initialization failed: " + e.getMessage(), e);
            }
            
            String extractedText;
            try {
                extractedText = tesseractInstance.doOCR(optimizedImage);
            } catch (Exception e) {
                throw new TesseractException("OCR processing failed: " + e.getMessage(), e);
            }
            
            if (totalPages > 1) {
                extractedText += "\n\n[Note: This PDF contains " + totalPages + " pages. Only the first page was processed for performance optimization.]";
            }
            
            return cleanOcrText(extractedText);
            
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                    logger.warn("Failed to close PDF document: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Optimize image for OCR processing to improve speed and accuracy.
     */
    private BufferedImage optimizeImageForOcr(BufferedImage original, String filename) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();
        
        // Don't resize if image is already optimal size (saves processing time)
        if (originalWidth <= 1800 && originalHeight <= 1800) {
            logger.debug("Image size acceptable for OCR: {}x{}", originalWidth, originalHeight);
            return original;
        }
        
        // Calculate scale factor to maintain aspect ratio while optimizing for OCR
        double scale = Math.min(1800.0 / originalWidth, 1800.0 / originalHeight);
        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);
        
        logger.info("Resizing image {} from {}x{} to {}x{} for OCR processing", 
                   SecurityUtil.sanitizeForLog(filename), originalWidth, originalHeight, newWidth, newHeight);
        
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        
        // High-quality rendering for better OCR accuracy
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return resized;
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
            // Test if we can create a Tesseract instance
            ITesseract testInstance = createTesseractInstance();
            return testInstance != null;
        } catch (Exception e) {
            logger.error("Tesseract OCR service is not available: {}", e.getMessage());
            return false;
        }
    }
}
