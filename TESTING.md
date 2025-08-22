# 🧪 DocUp Testing Guide

## ✅ Current Status
Both the backend and frontend are now running successfully!

### 🌐 Access Points
- **Backend API**: http://localhost:8080
- **Simple Frontend**: http://localhost:3000
- **Health Check**: http://localhost:8080/api/health
- **Upload Info**: http://localhost:8080/api/upload/info

## 🚀 Quick Start Testing

### Option 1: Web Interface Testing
1. Open your browser and go to: **http://localhost:3000**
2. The page will automatically check if the backend is running
3. Drag and drop files or click to browse
4. Supported files: JPG, PNG, PDF (max 10MB)
5. Click "Upload Files" to test the functionality
6. View the JSON response with file metadata and OCR results

### Option 2: Command Line Testing
```bash
# Test health endpoint
curl http://localhost:8080/api/health

# Test upload info
curl http://localhost:8080/api/upload/info

# Upload a file (replace with your file path)
curl -X POST -F "file=@/path/to/your/file.jpg" http://localhost:8080/api/upload
```

## 🔧 Running the Services

### Start Backend (Terminal 1)
```bash
cd /home/i01033/docup
./scripts/test-backend.sh
```

### Start Frontend (Terminal 2)
```bash
cd /home/i01033/docup
./scripts/start-frontend.sh
```

## 📋 Features to Test

### ✅ File Upload
- [x] Image files (JPG, PNG)
- [x] PDF documents
- [x] File size validation (10MB limit)
- [x] Drag and drop interface
- [x] Multiple file selection

### ✅ OCR Processing
- [x] Text extraction from images
- [x] Text extraction from PDFs
- [x] OCR results in JSON response

### ✅ File Storage
- [x] Date-based directory structure
- [x] UUID filename generation
- [x] File metadata tracking

### ✅ Security
- [x] CORS configuration
- [x] File type validation
- [x] File size limits
- [x] Virus scanning (simplified for development)

### ✅ API Endpoints
- [x] GET /api/health - Backend health check
- [x] GET /api/upload/info - Upload configuration info
- [x] POST /api/upload - File upload with OCR

## 🧪 Test Scenarios

### Scenario 1: Image Upload
1. Take a photo or find an image with text
2. Upload via the web interface
3. Verify OCR text extraction appears in results

### Scenario 2: PDF Upload
1. Find a PDF document
2. Upload via the web interface
3. Verify text content is extracted

### Scenario 3: Error Handling
1. Try uploading a file larger than 10MB
2. Try uploading an unsupported file type
3. Verify appropriate error messages

## 📁 File Storage Location
Uploaded files are stored in: `/home/i01033/docup/backend/uploads/`
Structure: `uploads/{year}/{month}/{day}/{uuid}_{filename}.{ext}`

## 🛠 Troubleshooting

### Backend Issues
- Check Java 17+ is installed: `java --version`
- Check Tesseract is installed: `tesseract --version`
- Verify port 8080 is available: `netstat -tlnp | grep 8080`

### Frontend Issues
- Check Python 3 is installed: `python3 --version`
- Verify port 3000 is available: `netstat -tlnp | grep 3000`
- Clear browser cache if needed

### CORS Issues
- The backend includes CORS configuration for localhost:3000
- If testing from different ports, update CorsConfig.java

## 🎯 Next Steps for Production

1. **Docker Deployment**: Fix remaining Docker permission issues
2. **Database Integration**: Add PostgreSQL or similar for metadata
3. **Advanced OCR**: Fine-tune Tesseract settings
4. **Enhanced Security**: Implement proper virus scanning
5. **UI Improvements**: Replace simple frontend with full Angular app
6. **Authentication**: Add user management and authentication
7. **Cloud Storage**: Integrate with AWS S3 or similar

## 📊 Performance Notes
- OCR processing may take 5-30 seconds for large files
- Files are processed synchronously (consider async for production)
- No file cleanup scheduled (implement retention policies)

---

**Happy Testing! 🎉**

The core functionality is working. You can now upload documents and see OCR text extraction in action.
