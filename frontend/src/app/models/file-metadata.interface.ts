export interface FileMetadata {
  name: string;
  size: number;
  type: string;
  lastModified: number;
}

export interface FilePreview {
  file: File;
  preview: string;
  metadata: FileMetadata;
  ocrText?: string;
}
