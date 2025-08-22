import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType, HttpRequest } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { UploadResponse } from '../models/upload-response.interface';

export interface UploadProgress {
  progress: number;
  status: 'uploading' | 'processing' | 'completed' | 'error';
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private apiUrl = environment.apiUrl;
  private uploadProgressSubject = new BehaviorSubject<UploadProgress>({ progress: 0, status: 'completed', message: '' });
  
  constructor(private http: HttpClient) {}

  /**
   * Upload file with progress tracking
   */
  uploadFile(file: File): Observable<UploadResponse | UploadProgress> {
    const formData = new FormData();
    formData.append('file', file);

    const request = new HttpRequest('POST', `${this.apiUrl}/upload`, formData, {
      reportProgress: true
      // Let browser set Content-Type with multipart boundary automatically
    });

    return this.http.request<UploadResponse>(request).pipe(
      map((event: HttpEvent<UploadResponse>) => {
        console.log('HTTP Event Type:', event.type, 'Event:', event);
        
        switch (event.type) {
          case HttpEventType.Sent:
            console.log('Request sent - starting progress simulation for Chrome');
            const startProgress: UploadProgress = {
              progress: 10,
              status: 'uploading',
              message: 'Upload started...'
            };
            this.uploadProgressSubject.next(startProgress);
            return startProgress;

          case HttpEventType.UploadProgress:
            console.log('Upload Progress Event - Loaded:', event.loaded, 'Total:', event.total);
            if (event.total) {
              const progress = Math.round(100 * event.loaded / event.total);
              const uploadProgress: UploadProgress = {
                progress,
                status: progress < 100 ? 'uploading' : 'processing',
                message: progress < 100 ? `Uploading... ${progress}%` : 'Processing file...'
              };
              console.log('Setting progress to:', progress);
              this.uploadProgressSubject.next(uploadProgress);
              return uploadProgress;
            }
            console.log('No total size available for progress calculation');
            return this.uploadProgressSubject.value;

          case HttpEventType.ResponseHeader:
            console.log('Response headers received - file uploaded, processing...');
            const processingProgress: UploadProgress = {
              progress: 90,
              status: 'processing',
              message: 'Processing file...'
            };
            this.uploadProgressSubject.next(processingProgress);
            return processingProgress;

          case HttpEventType.Response:
            console.log('Response received:', event.body);
            const completedProgress: UploadProgress = {
              progress: 100,
              status: 'completed',
              message: 'Upload completed successfully'
            };
            this.uploadProgressSubject.next(completedProgress);
            return event.body as UploadResponse;

          default:
            console.log('Other HTTP event type:', event.type);
            // Return current state without updating progress
            return this.uploadProgressSubject.value;
        }
      })
    );
  }

  /**
   * Get current upload progress
   */
  getUploadProgress(): Observable<UploadProgress> {
    return this.uploadProgressSubject.asObservable();
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
    this.uploadProgressSubject.next({ progress: 0, status: 'completed', message: '' });
  }
}
