# Backend (Spring Boot)

## Development Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- Tesseract OCR
- ClamAV (for local development)

### Installation

#### Tesseract OCR
```bash
# Ubuntu/Debian
sudo apt-get install tesseract-ocr tesseract-ocr-deu

# macOS
brew install tesseract tesseract-lang

# Windows
# Download from: https://github.com/UB-Mannheim/tesseract/wiki
```

#### ClamAV (Local Development)
```bash
# Ubuntu/Debian
sudo apt-get install clamav clamav-daemon

# macOS
brew install clamav

# Start ClamAV daemon
sudo freshclam
sudo clamd
```

### Run Application
```bash
mvn spring-boot:run
# API will be available at http://localhost:8080
```

### Build
```bash
mvn clean package
```

## Features

- **File Upload API**: REST endpoint for multipart file uploads
- **Virus Scanning**: ClamAV integration for security
- **OCR Processing**: Tesseract integration for text extraction
- **File Storage**: Organized storage with date-based directory structure
- **Metadata Response**: JSON response with file information and OCR results

## Project Structure

```
src/main/java/com/docup/
├── controller/             # REST controllers
│   └── UploadController.java      # File upload endpoint
├── service/                # Business logic services
│   ├── FileService.java           # File handling service
│   ├── VirusScanService.java      # ClamAV integration
│   ├── OcrService.java            # Tesseract OCR service
│   └── StorageService.java        # File storage service
├── model/                  # Data models
│   ├── UploadResponse.java        # API response model
│   └── FileMetadata.java          # File metadata model
├── config/                 # Configuration classes
│   ├── FileStorageConfig.java     # Storage configuration
│   └── CorsConfig.java            # CORS configuration
└── DocUpApplication.java   # Main application class

src/main/resources/
├── application.yml         # Application configuration
├── application-docker.yml  # Docker-specific configuration
└── static/                 # Static resources
```

## API Endpoints

### POST /upload
Upload a file for processing.

**Request:**
- Content-Type: `multipart/form-data`
- Body: `file` (binary data)

**Response:**
```json
{
  "filename": "2025-08-22_uuid_document.pdf",
  "size": 123456,
  "mimeType": "application/pdf",
  "ocrText": "Extracted text content from the document...",
  "uploadedAt": "2025-08-22T10:30:00Z"
}
```

**Error Responses:**
- `400 Bad Request`: Invalid file type or size
- `403 Forbidden`: Virus detected in file
- `500 Internal Server Error`: Processing failed

## Configuration

### application.yml
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

docup:
  upload:
    path: ${UPLOAD_PATH:./uploads}
    allowed-types: jpg,jpeg,png,pdf
  clamav:
    host: ${CLAMAV_HOST:localhost}
    port: ${CLAMAV_PORT:3310}
  ocr:
    language: deu+eng
```

## Docker

```bash
# Build Docker image
docker build -t docup-backend .

# Run container
docker run -p 8080:8080 \
  -v ./uploads:/app/uploads \
  -e CLAMAV_HOST=clamav \
  docup-backend
```

## Testing

```bash
# Run tests
mvn test

# Run integration tests
mvn verify
```
