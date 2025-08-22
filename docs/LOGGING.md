# DocUp Backend Logging Enhancement

## 🎯 Overview
Enhanced comprehensive logging throughout the DocUp backend application for better monitoring, debugging, and operational insights.

## 📊 Logging Configuration

### Log Levels
- **INFO**: Key application events, processing summaries
- **DEBUG**: Detailed process tracking, performance metrics
- **WARN**: Non-critical issues, fallback scenarios
- **ERROR**: Critical failures, exceptions

### Log Output
- **Console**: Structured format with timestamps and thread info
- **File**: Rolling log files (100MB max, 30 days retention)
- **Location**: `logs/docup-backend.log`

## 🔍 Enhanced Components

### 1. UploadController
- **Request Tracking**: File name, size, processing time
- **Error Handling**: Detailed error classification and logging
- **Status Updates**: Upload completion, success/failure states

```
INFO - Received upload request for file: image.png (size: 67 bytes)
INFO - File upload completed successfully: uuid_image.png
```

### 2. FileService (Orchestration)
- **Workflow Coordination**: Step-by-step processing tracking
- **Service Integration**: Virus scan, OCR, storage coordination
- **Health Monitoring**: Service availability checks

```
INFO - Processing file upload: image.png (67 bytes)
DEBUG - Virus scan completed for file: image.png
DEBUG - OCR processing completed for file: image.png
```

### 3. StorageService
- **File Validation**: Size, type, security checks
- **Storage Process**: Directory creation, file copying, checksum calculation
- **Path Management**: Date-based organization tracking

```
DEBUG - Starting file storage process for: image.png (size: 67 bytes)
DEBUG - File validation passed for: image.png
INFO - File stored successfully: image.png -> /app/uploads/2025/08/22/uuid_image.png
```

### 4. OcrService (Tesseract Integration)
- **Processing Metrics**: Timing, character extraction counts
- **Engine Status**: Tesseract initialization, language loading
- **Error Handling**: OCR failures, fallback scenarios

```
INFO - Starting OCR processing for file: image.png (type: image/png, size: 67 bytes)
INFO - OCR processing completed for file: image.png in 353ms, extracted 0 characters
```

### 5. VirusScanService (ClamAV Integration)
- **Scan Process**: Connection status, scan timing
- **Service Health**: ClamAV availability monitoring
- **Security Events**: Virus detection, scan failures

```
INFO - Starting virus scan for file: image.png (size: 67 bytes) using ClamAV at clamav:3310
INFO - Virus scan completed for file: image.png in 25ms - CLEAN
```

## 📈 Key Metrics Tracked

### Performance Metrics
- **Upload Processing Time**: End-to-end timing
- **OCR Processing Time**: Text extraction duration
- **Virus Scan Time**: Security scanning duration
- **File Storage Time**: Persistence operations

### File Information
- **File Details**: Name, size, type, checksum
- **Storage Paths**: Date-based directory structure
- **Validation Results**: Type checking, size limits

### System Health
- **Service Availability**: ClamAV, Tesseract status
- **Resource Usage**: File size tracking
- **Error Rates**: Processing failures

## 🚨 Log Analysis Examples

### Successful Upload Flow
```
2025-08-22 19:42:38 [http-nio-8080-exec-1] INFO  [UploadController] - Received upload request for file: test.png (size: 67 bytes)
2025-08-22 19:42:38 [http-nio-8080-exec-1] INFO  [FileService] - Processing file upload: test.png (67 bytes)
2025-08-22 19:42:38 [http-nio-8080-exec-1] INFO  [VirusScanService] - Virus scan completed for file: test.png in 25ms - CLEAN
2025-08-22 19:42:39 [http-nio-8080-exec-1] INFO  [OcrService] - OCR processing completed for file: test.png in 353ms, extracted 0 characters
2025-08-22 19:42:39 [http-nio-8080-exec-1] INFO  [StorageService] - File stored successfully: test.png -> /app/uploads/2025/08/22/uuid_test.png
2025-08-22 19:42:39 [http-nio-8080-exec-1] INFO  [UploadController] - File upload completed successfully: uuid_test.png
```

### Error Scenarios
```
WARN - Validation failed: File type 'txt' not in allowed types: [jpg, jpeg, png, pdf]
ERROR - OCR processing failed for file: corrupt.png after 1250ms - TesseractException
```

## 🛠 Monitoring Commands

### Real-time Log Monitoring
```bash
# Follow backend logs
sudo docker-compose logs -f backend

# Filter specific log levels
sudo docker-compose logs backend | grep "ERROR\|WARN"

# Monitor upload activity
sudo docker-compose logs backend | grep "UploadController"
```

### Log File Access
```bash
# Inside container
sudo docker exec docup_backend_1 tail -f /app/logs/docup-backend.log

# Copy logs to host
sudo docker cp docup_backend_1:/app/logs/docup-backend.log ./backend-logs.log
```

## ✅ Benefits

1. **Debugging**: Detailed error tracking and root cause analysis
2. **Performance**: Processing time monitoring and optimization
3. **Security**: Virus detection and file validation tracking
4. **Operations**: System health monitoring and alerting
5. **Compliance**: Audit trail for file processing activities

The enhanced logging provides complete visibility into the DocUp application's file processing pipeline, enabling better monitoring, debugging, and operational insights.
