# DocUp - Document Upload and OCR Processing Application

A full-stack web application for document upload, OCR text extraction, and virus scanning. Built with Angular frontend, Spring Boot backend, and Docker deployment.

## 🚀 Features

- **File Upload**: Support for JPG, PNG, and PDF files up to 50MB
- **Camera Integration**: Direct photo capture from web browser
- **OCR Processing**: Dual OCR system with client-side preview and server-side extraction
- **Virus Scanning**: Real-time virus detection with ClamAV
- **Image Processing**: Automatic resizing for large images to prevent crashes
- **Cross-Browser Support**: Compatible with Chrome, Firefox, and other modern browsers
- **Comprehensive Logging**: Detailed operation tracking and performance metrics
- **Docker Deployment**: Fully containerized with nginx proxy
- **Modern UI**: Clean, responsive interface inspired by professional design standards

## 🏗️ Architecture

- **Frontend**: Angular with TypeScript, Tesseract.js for preview, nginx proxy
- **Backend**: Spring Boot 3 with Java 17, Tesseract OCR engine
- **Virus Scanner**: ClamAV daemon for security
- **Storage**: Organized file system with date-based directory structure
- **Deployment**: Docker Compose orchestration with three services

## 📋 Prerequisites

- Docker and Docker Compose
- Git
- 4GB+ RAM (recommended for OCR processing)
- 10GB+ disk space for file storage

## 🛠️ Quick Start

### Option 1: Docker Deployment (Recommended)

1. **Clone the repository**:
   ```bash
   git clone <your-gitlab-repo-url>
   cd docup
   ```

2. **Start the application**:
   ```bash
   # May require sudo for Docker access
   sudo docker-compose up --build -d
   ```

3. **Access the application**:
   - Web Interface: http://localhost
   - Backend API: http://localhost:8080
   - Health Check: http://localhost:8080/api/health

### Option 2: Local Development

For development or if Docker permissions are restricted:

```bash
# Prerequisites: Java 17+, Maven, Tesseract OCR
cd backend

# Install Tesseract (Ubuntu/Debian)
sudo apt-get update
sudo apt-get install tesseract-ocr tesseract-ocr-eng tesseract-ocr-deu

# Build and run the backend
./mvnw spring-boot:run

# Backend API will be available at: http://localhost:8080
# Test with: curl http://localhost:8080/api/health
```

## 🐳 Docker Services

The application consists of three containerized services:

- **Frontend** (Port 80): Angular app with nginx proxy for API routing
- **Backend** (Port 8080): Spring Boot API server with OCR processing
- **ClamAV** (Port 3310): Virus scanning daemon with updated definitions

### Environment Configuration

Create a `.env` file for custom settings (optional):
```bash
UPLOAD_PATH=./uploads
SPRING_PROFILES_ACTIVE=docker
MAX_FILE_SIZE=50MB
```

### Docker Commands

```bash
# Build all services
docker-compose build

# Start services in background
docker-compose up -d

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs clamav

# Stop services
docker-compose down

# Restart specific service
docker-compose restart backend
```

## 📁 Project Structure

```
docup/
├── backend/                    # Spring Boot API
│   ├── src/main/java/com/docup/
│   │   ├── controller/         # REST controllers
│   │   ├── service/           # Business logic
│   │   ├── config/            # Configuration classes
│   │   └── util/              # Utility classes
│   ├── src/main/resources/    # Configuration files
│   ├── uploads/               # File storage (gitignored)
│   └── Dockerfile             # Backend container config
├── frontend/                  # Angular web application
│   ├── src/app/
│   │   ├── components/        # UI components
│   │   ├── services/          # API services
│   │   └── models/           # TypeScript interfaces
│   ├── nginx.conf            # Proxy configuration
│   └── Dockerfile            # Frontend container config
├── scripts/                  # Utility scripts
├── docs/                     # Additional documentation
├── docker-compose.yml        # Main deployment config
├── docker-compose-backend-only.yml  # Backend-only testing
└── README.md                 # This documentation
```

## 📊 API Documentation

### Upload Endpoint

**Upload a file for OCR processing:**

```http
POST /api/upload
Content-Type: multipart/form-data

Body: file (JPG, PNG, or PDF up to 50MB)
```

**Response:**
```json
{
  "filename": "processed-filename.jpg",
  "size": 1234567,
  "mimeType": "image/jpeg",
  "ocrText": "Extracted text content from the document...",
  "uploadedAt": "2025-08-22T20:00:00Z",
  "virusScanPassed": true,
  "storagePath": "/uploads/2025/08/22/unique-id_filename.jpg"
}
```

### Health Check

**Check service availability:**

```http
GET /api/health
```

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2025-08-22T20:00:00Z",
  "services": {
    "backend": "healthy",
    "clamav": "connected",
    "ocr": "available"
  }
}
```

## 🔧 Development Workflow

### Backend Development

```bash
cd backend

# Run with hot reload
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build production JAR
./mvnw clean package -DskipTests
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Development server with proxy
npm start

# Build for production
npm run build

# Run tests
npm test
```

### Testing

```bash
# Test backend only
./scripts/test-backend.sh

# Test full Docker deployment
./scripts/test-docker-deployment.sh

# Integration tests
curl -X POST -F "file=@scan-test.jpg" http://localhost:8080/api/upload
```

## 🚨 Troubleshooting

### Common Issues and Solutions

1. **Upload fails at 10-50%**:
   - **Cause**: Browser compatibility issues with Chrome
   - **Solution**: Try Firefox or check nginx proxy configuration
   - **Fix**: The nginx.conf includes Chrome-specific optimizations

2. **OCR processing crashes**:
   - **Cause**: Large images overwhelming Tesseract
   - **Solution**: Images are automatically resized to max 2000x2000px
   - **Monitoring**: Check backend logs for image processing details

3. **Virus scanner unavailable**:
   - **Cause**: ClamAV container needs time to initialize (2-3 minutes)
   - **Solution**: Wait for virus definitions to download
   - **Check**: `docker-compose logs clamav`

4. **File upload size limit exceeded**:
   - **Current Limit**: 50MB maximum per file
   - **Frontend**: Shows file size validation
   - **Backend**: Enforced by Spring Boot configuration

5. **Docker permission denied**:
   - **Solution**: Use `sudo` with docker-compose commands
   - **Alternative**: Add user to docker group: `sudo usermod -aG docker $USER`

### Debug Commands

```bash
# Check service status
docker-compose ps

# View detailed logs with timestamps
docker-compose logs -t -f

# Check file permissions
ls -la uploads/

# Test OCR locally
tesseract scan-test.jpg output.txt

# Test virus scanner
echo "test" | docker exec -i docup-clamav-1 clamdscan -
```

## 🔒 Security Features

- **File Type Validation**: Only JPG, PNG, and PDF files accepted
- **File Size Limits**: 50MB maximum to prevent abuse
- **Virus Scanning**: All uploads scanned with ClamAV before processing
- **CORS Protection**: Configured for secure cross-origin requests
- **Input Sanitization**: File names and content properly validated
- **Secure Storage**: Files stored with UUID prefixes to prevent conflicts

## 🛡️ Monitoring and Logging

The application includes comprehensive logging and monitoring:

### Log Categories

- **Upload Operations**: File processing, storage, and validation
- **OCR Processing**: Text extraction performance and errors
- **Virus Scanning**: Security scan results and threats detected
- **Performance Metrics**: Response times and resource usage
- **Error Tracking**: Detailed error messages and stack traces

### Log Locations

```bash
# Backend logs (in container)
docker-compose logs backend

# Frontend nginx logs
docker-compose logs frontend

# View specific log levels
docker-compose logs backend | grep ERROR
docker-compose logs backend | grep WARN
```

## 🤝 Contributing

1. **Fork the repository** on GitLab
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make your changes** following the coding guidelines
4. **Add tests** for new functionality
5. **Commit your changes**: `git commit -m 'Add amazing feature'`
6. **Push to your branch**: `git push origin feature/amazing-feature`
7. **Create a Merge Request** with a clear description

### Development Guidelines

- Follow Java coding standards for backend
- Use Angular/TypeScript best practices for frontend
- Add unit tests for new features
- Update documentation for API changes
- Test Docker deployment before submitting

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For issues, questions, or contributions:

1. **Check the troubleshooting section** above
2. **Review Docker logs** for error details
3. **Search existing issues** in GitLab
4. **Create a new issue** with:
   - Steps to reproduce
   - Expected vs actual behavior
   - System information (OS, Docker version)
   - Relevant log excerpts

## 📚 Additional Documentation

- [Project Structure](docs/project-structure.md) - Detailed component overview
- [Logging Documentation](docs/LOGGING.md) - Comprehensive logging guide
- [Docker Deployment](DOCKER_DEPLOYMENT.md) - Advanced deployment options
- [Testing Guide](TESTING.md) - Testing procedures and automation

---

**DocUp** - Making document processing simple, secure, and efficient! 🚀

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
