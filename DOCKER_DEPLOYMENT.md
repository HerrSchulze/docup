# DocUp Docker Deployment Guide

## 🚀 Quick Start

1. **Prerequisites**
   - Docker and Docker ComposU  e installed
   - Port 80 and 8080 available

2. **Start the application**
   ```bash
   cd /path/to/docup
   sudo docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost
   - API: http://localhost/api
   - Health Check: http://localhost/api/health

4. **Test the deployment**
   ```bash
   ./scripts/test-docker-deployment.sh
   ```

## 📦 Services

### Frontend (nginx on port 80)
- Angular 16 application 
- Bootstrap UI with drag-drop upload
- Proxies API requests to backend
- Serves static files

### Backend (Spring Boot on port 8080)
- RESTful API for file upload
- Tesseract OCR text extraction
- ClamAV virus scanning
- File storage with date organization

### ClamAV (port 3310)
- Virus scanning service
- Health monitoring
- Virus definition updates disabled for development

## 🛠 Docker Configuration

### docker-compose.yml
- **Frontend**: nginx reverse proxy, Bootstrap UI
- **Backend**: Spring Boot with Docker profile, Tesseract OCR
- **ClamAV**: Virus scanning service with health checks
- **Network**: Isolated Docker network for services
- **Volumes**: Persistent storage for uploads and virus definitions

### Dockerfile (Backend)
- OpenJDK 17 base image
- Tesseract OCR installation (English + German)
- Maven build with dependency caching
- Environment variables for Docker deployment
- TESSDATA_PREFIX properly configured

### Dockerfile (Frontend) 
- Multi-stage build (Node.js + nginx)
- Angular production build
- Bootstrap CSS bundled
- nginx proxy configuration for API routes

## 🔧 Environment Variables

### Backend Docker Environment
- `SPRING_PROFILES_ACTIVE=docker`
- `UPLOAD_PATH=/app/uploads`
- `TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata`
- `CLAMAV_HOST=clamav`
- `CLAMAV_PORT=3310`

## 🌐 API Endpoints

- `GET /api/health` - Health check
- `POST /api/upload` - File upload with OCR and virus scan

## 📁 File Support
- **Images**: JPG, JPEG, PNG (with OCR)
- **Documents**: PDF (with OCR)
- **Max Size**: 10MB
- **Virus Scanning**: All files scanned before processing

## 🧪 Testing

Upload test files through the web interface or API:

```bash
# Test image upload
curl -X POST -F "file=@image.png" http://localhost/api/upload

# Test health
curl http://localhost/api/health
```

## 🔍 Troubleshooting

### Check service status
```bash
sudo docker-compose ps
```

### View logs
```bash
sudo docker-compose logs frontend
sudo docker-compose logs backend  
sudo docker-compose logs clamav
```

### Restart services
```bash
sudo docker-compose restart backend
```

### Clean rebuild
```bash
sudo docker-compose down
sudo docker-compose build --no-cache
sudo docker-compose up -d
```

## 🛡 Security Features

- File type validation (whitelist approach)
- File size limits (10MB)
- Virus scanning with ClamAV
- CORS protection
- Input validation

## 📊 Monitoring

- Backend health endpoint: `/api/health`
- ClamAV health checks built into Docker Compose
- Application logs available via `docker-compose logs`

## 🔄 Updates

To update the application:

1. Pull latest changes
2. Rebuild containers: `sudo docker-compose build`
3. Restart: `sudo docker-compose up -d`

## ⚠️ Production Considerations

For production deployment:
- Use proper TLS/SSL certificates
- Configure production virus definition updates  
- Set up log aggregation
- Configure backup strategy for uploads
- Use secrets management for sensitive data
- Set resource limits in docker-compose.yml
- Consider using Docker Swarm or Kubernetes for scaling
