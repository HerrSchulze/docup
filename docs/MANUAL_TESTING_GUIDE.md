# Manual Testing Guide for DocUp Docker Deployment

## ✅ Current Status
All services are running successfully:
- **Frontend**: http://localhost (Port 80)
- **Backend**: http://localhost:8080 (Port 8080)
- **ClamAV**: Port 3310 (Virus scanning)

## 🧪 Quick Tests

### 1. Frontend Accessibility
```bash
curl -I http://localhost
# Should return: HTTP/1.1 200 OK
```

### 2. Backend Health Check
```bash
curl http://localhost:8080/actuator/health
# Should return: {"status":"UP",...}
```

### 3. Backend API Test
```bash
curl -I http://localhost:8080/api/upload
# Should return: 405 Method Not Allowed (expected for GET)
```

## 🎯 Manual Testing Steps

### Test 1: Open the Application
1. **Open Browser**: Navigate to http://localhost
2. **Verify UI**: Should see the DocUp interface
3. **Check Console**: No JavaScript errors in browser console

### Test 2: File Upload Test
1. **Select File**: Choose an image or PDF file
2. **Upload**: Click upload button
3. **Verify**: Check if file uploads successfully
4. **OCR Test**: Upload an image with text to test OCR functionality

### Test 3: Backend API Direct Test
```bash
# Test with a text file
echo "Test document content" > test.txt
curl -X POST -F "file=@test.txt" http://localhost:8080/api/upload
rm test.txt
```

### Test 4: Service Logs Check
```bash
# Check backend logs
sudo docker-compose logs backend | tail -20

# Check frontend logs  
sudo docker-compose logs frontend | tail -10

# Check for errors
sudo docker-compose logs | grep -i error
```

## 📊 Performance Monitoring

### Check Resource Usage
```bash
sudo docker stats --no-stream
```

### Container Status
```bash
sudo docker-compose ps
```

## 🔧 Troubleshooting

### Restart Services
```bash
# Restart specific service
sudo docker-compose restart backend
sudo docker-compose restart frontend

# Restart all services
sudo docker-compose restart
```

### View Detailed Logs
```bash
# Follow logs in real-time
sudo docker-compose logs -f backend

# Check specific errors
sudo docker-compose logs backend | grep -A 5 -B 5 "ERROR"
```

### Stop and Restart Everything
```bash
# Stop all services
sudo docker-compose down

# Start all services
sudo docker-compose up -d

# Or start with logs visible
sudo docker-compose up
```

## 🌟 Features to Test

### Core Functionality
- [x] **File Upload**: Upload images and PDFs
- [x] **OCR Processing**: Extract text from images
- [x] **File Storage**: Files saved to uploads directory
- [x] **Virus Scanning**: ClamAV integration
- [x] **Health Monitoring**: Actuator endpoints

### Security Features
- [x] **Non-root Execution**: Backend runs as appuser
- [x] **CORS Protection**: Configured for security
- [x] **File Validation**: Type and size checking
- [x] **Path Traversal Protection**: Secure file handling

### Performance Features
- [x] **Container Health Checks**: Automatic monitoring
- [x] **Resource Limits**: Memory and CPU management
- [x] **Caching**: Docker layer caching for builds
- [x] **Optimization**: Multi-stage builds

## 🚀 Production Readiness

### What's Working
✅ Docker containerization complete
✅ Multi-service orchestration with docker-compose
✅ Health checks and monitoring
✅ Security hardening implemented
✅ OCR processing functional
✅ File upload and storage working

### Next Steps for Production
1. **GitHub Migration**: Move to GitHub with CI/CD
2. **SSL/TLS**: Add HTTPS configuration
3. **Domain Setup**: Configure custom domain
4. **Monitoring**: Add comprehensive logging and metrics
5. **Backup Strategy**: Implement data backup
6. **Scaling**: Configure for horizontal scaling

## 📝 Notes
- Application is fully functional in Docker
- All build issues have been resolved
- Ready for GitHub migration and CI/CD setup
- Security scanning tools integrated and tested
