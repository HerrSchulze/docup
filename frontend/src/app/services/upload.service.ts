import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType, HttpRequest } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';
import { UploadResponse } from '../models/upload-response.interface';

export interface UploadProgress {
  status: string;
  percentage: number;
  phase: 'preparing' | 'uploading' | 'virus-scan' | 'ocr-processing' | 'complete' | 'error';
  message: string;
  estimatedTimeRemaining?: number;
}

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private readonly apiUrl = environment.production ? '/api' : 'http://localhost:8080/api';
  private progressSubject = new Subject<UploadProgress>();
  private startTime = 0;

  public progress$ = this.progressSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  /**
   * Upload file with detailed progress tracking
   */
  public uploadFile(file: File): Observable<UploadResponse> {
    this.startTime = Date.now();
    
    return new Observable<UploadResponse>(observer => {
      this.updateProgress('preparing', 0, 'Datei wird vorbereitet...');

      const formData = new FormData();
      formData.append('file', file);

      const request = new HttpRequest('POST', `${this.apiUrl}/upload`, formData, {
        reportProgress: true
      });

      this.http.request(request).subscribe({
        next: (event: HttpEvent<any>) => {
          this.handleUploadEvent(event, observer, file);
        },
        error: (error) => {
          this.updateProgress('error', 0, this.getErrorMessage(error));
          observer.error(error);
        }
      });
    });
  }

  /**
   * Get current upload progress
   */
  getUploadProgress(): Observable<UploadProgress> {
    return this.progressSubject.asObservable();
  }

  /**
   * Get upload info (file size limits, allowed types, etc.)
   */
  getUploadInfo(): Observable<any> {
    return this.http.get(`${this.apiUrl}/upload/info`);
  }

  /**
   * Validate file before upload
   */
  validateFile(file: File): { valid: boolean; errors: string[] } {
    const errors: string[] = [];
    const maxSize = 10 * 1024 * 1024; // 10MB
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];

    if (file.size > maxSize) {
      errors.push('File size exceeds 10MB limit');
    }

    if (!allowedTypes.includes(file.type)) {
      errors.push('File type not supported. Please use JPG, PNG, or PDF files.');
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }

  /**
   * Reset upload progress
   */
  resetProgress(): void {
    this.progressSubject.next({ 
      status: 'Bereit', 
      percentage: 0, 
      phase: 'preparing', 
      message: '' 
    });
  }

  /**
   * Handle different HTTP events during upload
   */
  private handleUploadEvent(event: HttpEvent<any>, observer: any, file: File): void {
    switch (event.type) {
      case HttpEventType.Sent:
        this.updateProgress('uploading', 5, 'Upload gestartet...');
        break;

      case HttpEventType.UploadProgress:
        if (event.total) {
          const uploadPercentage = Math.round((event.loaded / event.total) * 100);
          const adjustedPercentage = Math.min(uploadPercentage * 0.3, 30); // Upload is 30% of total process
          this.updateProgress('uploading', adjustedPercentage, 
            `Datei wird hochgeladen... ${uploadPercentage}%`);
        }
        break;

      case HttpEventType.ResponseHeader:
        this.updateProgress('virus-scan', 35, 'Virenprüfung läuft...');
        // Simulate virus scan progress
        this.simulateVirusScanProgress();
        break;

      case HttpEventType.DownloadProgress:
        // This indicates the server is sending the response (OCR processing complete)
        this.updateProgress('ocr-processing', 85, 'Texterkennung fast abgeschlossen...');
        break;

      case HttpEventType.Response:
        if (event.body) {
          this.updateProgress('complete', 100, 'Upload erfolgreich abgeschlossen!');
          observer.next(event.body);
          observer.complete();
        }
        break;

      default:
        // Handle other event types silently
        break;
    }
  }

  /**
   * Simulate virus scan progress with realistic timing
   */
  private simulateVirusScanProgress(): void {
    let progress = 35;
    const interval = setInterval(() => {
      progress += 2;
      if (progress >= 50) {
        clearInterval(interval);
        this.updateProgress('ocr-processing', 50, 'Texterkennung wird gestartet...');
        this.simulateOcrProgress();
      } else {
        this.updateProgress('virus-scan', progress, 
          'Virenprüfung läuft... Datei wird auf Schadsoftware überprüft');
      }
    }, 200);
  }

  /**
   * Simulate OCR processing progress
   */
  private simulateOcrProgress(): void {
    let progress = 50;
    const messages = [
      'Text wird analysiert...',
      'Zeichen werden erkannt...',
      'Wörter werden zusammengefügt...',
      'Textformatierung wird verarbeitet...',
      'Qualitätsprüfung läuft...'
    ];
    
    let messageIndex = 0;
    const interval = setInterval(() => {
      progress += 3;
      if (progress >= 95) {
        clearInterval(interval);
        this.updateProgress('ocr-processing', 95, 'Texterkennung wird abgeschlossen...');
      } else {
        const message = messages[Math.floor((progress - 50) / 9)] || messages[messageIndex];
        this.updateProgress('ocr-processing', progress, message);
        if ((progress - 50) % 9 === 0) messageIndex++;
      }
    }, 300);
  }

  /**
   * Update progress and notify subscribers
   */
  private updateProgress(phase: UploadProgress['phase'], percentage: number, message: string): void {
    const estimatedTimeRemaining = this.calculateEstimatedTime(percentage);
    
    this.progressSubject.next({
      status: this.getStatusText(phase),
      percentage,
      phase,
      message,
      estimatedTimeRemaining
    });
  }

  /**
   * Calculate estimated time remaining
   */
  private calculateEstimatedTime(percentage: number): number | undefined {
    if (percentage <= 5 || this.startTime === 0) return undefined;
    
    const elapsed = Date.now() - this.startTime;
    const estimatedTotal = (elapsed / percentage) * 100;
    return Math.max(0, estimatedTotal - elapsed);
  }

  /**
   * Get user-friendly status text
   */
  private getStatusText(phase: UploadProgress['phase']): string {
    switch (phase) {
      case 'preparing': return 'Vorbereitung';
      case 'uploading': return 'Upload';
      case 'virus-scan': return 'Sicherheitsprüfung';
      case 'ocr-processing': return 'Texterkennung';
      case 'complete': return 'Abgeschlossen';
      case 'error': return 'Fehler';
      default: return 'Verarbeitung';
    }
  }

  /**
   * Get user-friendly error message
   */
  private getErrorMessage(error: any): string {
    if (error.status === 413) {
      return 'Die Datei ist zu groß. Maximale Größe: 10 MB';
    }
    if (error.status === 415) {
      return 'Dateityp nicht unterstützt. Erlaubt: JPG, PNG, PDF';
    }
    if (error.status === 0) {
      return 'Verbindungsfehler. Bitte überprüfen Sie Ihre Internetverbindung';
    }
    if (error.error?.message) {
      return error.error.message;
    }
    return 'Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es erneut';
  }
}
