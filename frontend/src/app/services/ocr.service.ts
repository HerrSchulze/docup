import { Injectable } from '@angular/core';
import { OcrResult } from '../models/ocr-result.interface';

@Injectable({
  providedIn: 'root'
})
export class OcrService {
  
  constructor() {}

  /**
   * OCR is now handled by the backend service.
   * This service is kept for potential future frontend-only OCR features.
   */
  async extractTextFromImage(file: File): Promise<OcrResult> {
    // Return empty result since OCR is handled by backend
    return {
      text: '',
      confidence: 0,
      processingTime: 0
    };
  }

  /**
   * Extract text from PDF (handled by backend)
   */
  async extractTextFromPdf(file: File): Promise<OcrResult> {
    // Return empty result since OCR is handled by backend
    return {
      text: '',
      confidence: 0,
      processingTime: 0
    };
  }

  /**
   * Check if file type is supported for OCR
   */
  isOcrSupported(fileType: string): boolean {
    const supportedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];
    return supportedTypes.includes(fileType);
  }

  /**
   * Get estimated processing time based on file size
   */
  getEstimatedProcessingTime(fileSize: number): number {
    // Rough estimate: 1MB = 5 seconds processing time
    return Math.max(fileSize / (1024 * 1024) * 5, 2);
  }
}
