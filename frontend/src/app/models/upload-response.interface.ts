export interface UploadResponse {
  filename: string;
  size: number;
  mimeType: string;
  ocrText: string;
  uploadedAt: string;
  virusScanPassed: boolean;
  storagePath: string;
}

export interface UploadError {
  error: boolean;
  message: string;
  timestamp: number;
}
