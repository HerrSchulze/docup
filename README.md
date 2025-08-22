# DocUp - Document Upload with OCR and Virus Scanning

A modern web application for uploading documents (images and PDFs) with integrated OCR text recognition and virus scanning capabilities.

## Architecture

- **Frontend**: Angular with Tesseract.js for client-side OCR preview
- **Backend**: Spring Boot 3 with Tesseract for server-side OCR
- **Security**: ClamAV for virus scanning
- **Deployment**: Docker Compose

## Features

- 📷 Camera access for direct photo capture
- 📁 Drag & drop file upload (Images: JPG, PNG; Documents: PDF)
- 👁️ File preview with PDF thumbnail generation
- 📝 Client-side OCR preview with Tesseract.js
- 🔒 Virus scanning with ClamAV
- 📊 Upload progress tracking
- 💾 Organized file storage with date-based directory structure
- 🎨 Modern UI inspired by creditplus.de design

## Quick Start

### Option 1: Docker (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd docup

# Start all services with Docker Compose (may require sudo)
sudo docker-compose up --build

# Access the application
# Frontend: http://localhost
# Backend API: http://localhost:8080
```

### Option 2: Local Development (Backend Only)

If you encounter Docker permission issues, you can run the backend locally:

```bash
# Prerequisites: Java 17+, Maven, Tesseract OCR
cd backend

# Install Tesseract (Ubuntu/Debian)
sudo apt-get update
sudo apt-get install tesseract-ocr tesseract-ocr-eng

# Build and run the backend
./mvnw spring-boot:run

# Backend API will be available at: http://localhost:8080
# Test with: curl http://localhost:8080/api/health
```

### Option 3: Backend Only with Docker

```bash
# Start just backend and ClamAV (for testing)
sudo docker-compose -f docker-compose-backend-only.yml up --build

# Backend API: http://localhost:8080
```

## Development Setup

### Backend (Spring Boot)

```bash
cd backend

# Build and run locally (requires Java 17+, Tesseract, ClamAV)
./mvnw spring-boot:run

# Or build JAR
./mvnw clean package
java -jar target/docup-backend-0.0.1-SNAPSHOT.jar
```

### Frontend (Angular)

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm run build
```

## API Endpoints

### POST /api/upload
Upload a file for processing.

**Request:**
- Content-Type: multipart/form-data
- Body: file (binary)

**Response:**
```json
{
  "filename": "uuid_originalname.pdf",
  "size": 123456,
  "mimeType": "application/pdf",
  "ocrText": "Extracted text content...",
  "uploadedAt": "2025-08-22T10:30:00Z",
  "virusScanPassed": true,
  "storagePath": "/app/uploads/2025/08/22/uuid_file.pdf"
}
```

### GET /api/health
Check service health status.

### GET /api/upload/info
Get upload configuration (file size limits, allowed types, etc.).

## Project Structure

```
docup/
├── frontend/                 # Angular frontend application
├── backend/                  # Spring Boot backend API
├── uploads/                  # File storage directory
├── clamav/                   # ClamAV virus definition storage
├── docker/                   # Docker configuration files
├── docs/                     # Documentation
├── scripts/                  # Utility scripts
└── docker-compose.yml       # Docker orchestration
```

## Development

### Prerequisites

- Docker & Docker Compose
- Node.js 18+ (for local frontend development)
- Java 17+ (for local backend development)

### Local Development Setup

See individual README files in `frontend/` and `backend/` directories for detailed setup instructions.

## API Documentation

### POST /upload

Upload a file for processing.

**Request:**
- Content-Type: multipart/form-data
- Body: file (binary)

**Response:**
```json
{
  "filename": "uuid_originalname.pdf",
  "size": 123456,
  "mimeType": "application/pdf",
  "ocrText": "Extracted text content...",
  "uploadedAt": "2025-08-22T10:30:00Z"
}
```

## Configuration

Environment variables can be configured in `.env` files for each service.

## Security Features

- File type validation (images and PDFs only)
- Virus scanning before storage
- HTTPS support
- Input sanitization

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

[Add your license here]
