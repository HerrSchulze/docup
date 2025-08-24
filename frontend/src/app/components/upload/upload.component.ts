import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { UploadService, UploadProgress } from '../../services/upload.service';
import { OcrService } from '../../services/ocr.service';
import { CameraService } from '../../services/camera.service';
import { UploadResponse } from '../../models/upload-response.interface';
import { FilePreview } from '../../models/file-metadata.interface';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @ViewChild('videoElement') videoElement!: ElementRef<HTMLVideoElement>;

  selectedFiles: FilePreview[] = [];
  isDragOver = false;
  isUploading = false;
  uploadProgress: number = 0;
  uploadResults: UploadResponse[] = [];
  errorMessage = '';
  
  // Enhanced progress feedback
  currentProgressStatus = '';
  currentProgressMessage = '';
  currentProgressPhase: 'preparing' | 'uploading' | 'virus-scan' | 'ocr-processing' | 'complete' | 'error' = 'preparing';
  estimatedTimeRemaining?: number;
  
  // Camera related
  isCameraActive = false;
  mediaStream: MediaStream | null = null;
  cameraError = '';

  constructor(
    private uploadService: UploadService,
    private ocrService: OcrService,
    private cameraService: CameraService
  ) {}

  ngOnInit(): void {
    // Subscribe to enhanced upload progress
    this.uploadService.progress$.subscribe((progress: UploadProgress) => {
      this.uploadProgress = progress.percentage;
      this.currentProgressStatus = progress.status;
      this.currentProgressMessage = progress.message;
      this.currentProgressPhase = progress.phase;
      this.estimatedTimeRemaining = progress.estimatedTimeRemaining;
    });
  }

  /**
   * Handle file selection via input
   */
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.handleFiles(Array.from(input.files));
    }
  }

  /**
   * Handle drag over event
   */
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = true;
  }

  /**
   * Handle drag leave event
   */
  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = false;
  }

  /**
   * Handle file drop
   */
  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = false;
    
    if (event.dataTransfer?.files) {
      this.handleFiles(Array.from(event.dataTransfer.files));
    }
  }

  /**
   * Process selected files
   */
  private async handleFiles(files: File[]): Promise<void> {
    this.errorMessage = '';
    this.selectedFiles = [];

    for (const file of files) {
      const validation = this.uploadService.validateFile(file);
      
      if (!validation.valid) {
        this.errorMessage = validation.errors.join(', ');
        continue;
      }

      const preview = await this.createFilePreview(file);
      this.selectedFiles.push(preview);

      // Start OCR preview for images
      if (file.type.startsWith('image/') && this.ocrService.isOcrSupported(file.type)) {
        this.performOcrPreview(preview);
      }
    }
  }

  /**
   * Create file preview
   */
  private async createFilePreview(file: File): Promise<FilePreview> {
    const preview: FilePreview = {
      file,
      metadata: {
        name: file.name,
        size: file.size,
        type: file.type,
        lastModified: new Date(file.lastModified)
      }
    };

    // Create image preview for image files
    if (file.type.startsWith('image/')) {
      preview.preview = await this.createImagePreview(file);
    }

    return preview;
  }

  /**
   * Create image preview URL
   */
  private createImagePreview(file: File): Promise<string> {
    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.onload = (e) => resolve(e.target?.result as string);
      reader.readAsDataURL(file);
    });
  }

  /**
   * Perform OCR preview (simplified)
   */
  private async performOcrPreview(filePreview: FilePreview): Promise<void> {
    try {
      // OCR preview disabled for now
      // if (filePreview.preview) {
      //   const ocrText = await this.ocrService.extractText(filePreview.preview);
      //   filePreview.ocrText = ocrText;
      // }
    } catch (error) {
      console.warn('OCR preview failed:', error);
    }
  }

  /**
   * Upload files to server
   */
  async uploadFiles(): Promise<void> {
    if (this.selectedFiles.length === 0) return;
    
    this.isUploading = true;
    this.uploadResults = [];
    this.uploadProgress = 0;
    this.errorMessage = '';
    this.currentProgressStatus = '';
    this.currentProgressMessage = '';

    try {
      for (let i = 0; i < this.selectedFiles.length; i++) {
        const filePreview = this.selectedFiles[i];
        
        // Reset progress for each file
        this.uploadProgress = 0;
        this.currentProgressStatus = '';
        this.currentProgressMessage = `Verarbeite Datei ${i + 1} von ${this.selectedFiles.length}: ${filePreview.metadata.name}`;
        
        try {
          // Upload file and get result
          const response = await new Promise<UploadResponse>((resolve, reject) => {
            this.uploadService.uploadFile(filePreview.file).subscribe({
              next: (response: UploadResponse) => {
                resolve(response);
              },
              error: (error: any) => {
                reject(error);
              }
            });
          });
          
          this.uploadResults.push(response);
          
        } catch (error) {
          console.error('Upload error:', error);
          this.errorMessage = `Fehler beim Hochladen von ${filePreview.metadata.name}: ${error}`;
        }
      }
      
      // All files completed
      this.currentProgressStatus = 'Abgeschlossen';
      this.currentProgressMessage = `${this.uploadResults.length} von ${this.selectedFiles.length} Dateien erfolgreich verarbeitet`;
      
    } finally {
      this.isUploading = false;
    }
  }  /**
   * Start camera (mock implementation)
   */
  async startCamera(): Promise<void> {
    this.cameraError = '';
    
    try {
      // Try to get user media
      this.mediaStream = await navigator.mediaDevices.getUserMedia({ video: true });
      
      if (this.videoElement && this.mediaStream) {
        this.videoElement.nativeElement.srcObject = this.mediaStream;
        this.isCameraActive = true;
      }
    } catch (error) {
      this.cameraError = error instanceof Error ? error.message : 'Camera access failed';
    }
  }

  /**
   * Stop camera
   */
  stopCamera(): void {
    if (this.mediaStream) {
      this.mediaStream.getTracks().forEach(track => track.stop());
      this.mediaStream = null;
      this.isCameraActive = false;
    }
  }

  /**
   * Capture photo from camera
   */
  async capturePhoto(): Promise<void> {
    if (!this.videoElement || !this.isCameraActive) {
      return;
    }

    try {
      const canvas = document.createElement('canvas');
      const video = this.videoElement.nativeElement;
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.drawImage(video, 0, 0);
        canvas.toBlob((blob) => {
          if (blob) {
            const photoFile = new File([blob], `photo_${Date.now()}.jpg`, { type: 'image/jpeg' });
            this.handleFiles([photoFile]);
          }
        }, 'image/jpeg', 0.9);
      }
      this.stopCamera();
    } catch (error) {
      this.cameraError = error instanceof Error ? error.message : 'Photo capture failed';
    }
  }

  /**
   * Remove file from selection
   */
  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  /**
   * Format file size for display
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  formatTime(milliseconds: number): string {
    const seconds = Math.ceil(milliseconds / 1000);
    if (seconds < 60) return `${seconds} Sek.`;
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')} Min.`;
  }

  /**
   * Get display name for file type
   */
  getFileTypeDisplay(mimeType: string): string {
    const typeMap: { [key: string]: string } = {
      'image/jpeg': 'JPEG Bild',
      'image/jpg': 'JPG Bild', 
      'image/png': 'PNG Bild',
      'application/pdf': 'PDF Dokument'
    };
    return typeMap[mimeType] || mimeType;
  }

  /**
   * Format date for display
   */
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Reset upload state for new upload
   */
  resetUpload(): void {
    this.selectedFiles = [];
    this.uploadResults = [];
    this.isUploading = false;
    this.uploadProgress = 0;
    this.errorMessage = '';
    
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
  }
}
