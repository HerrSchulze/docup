# DocUp v1.0.0 - Production Deployment Guide

## 🚀 Production Release Deployment

### Quick Production Setup
```bash
# 1. Clone the repository
git clone https://github.com/HerrSchulze/docup.git
cd docup

# 2. Deploy production containers
sudo docker-compose up --build -d

# 3. Verify deployment
curl http://localhost:8080/api/health
```

### 🔧 Production Configuration

#### System Requirements
- **Minimum**: 4GB RAM, 10GB storage
- **Recommended**: 8GB RAM, 50GB storage
- **Docker**: v20.10+ with Docker Compose v2.0+

#### Network Ports
- **80**: Frontend (nginx)
- **8080**: Backend API  
- **3310**: ClamAV virus scanner

#### Environment Variables
```yaml
# Backend Configuration
CLAMAV_HOST=clamav
CLAMAV_PORT=3310
UPLOAD_PATH=/app/uploads
SPRING_PROFILES_ACTIVE=docker
TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata
```

### 📋 Production Checklist

#### Pre-Deployment
- [ ] Docker and Docker Compose installed
- [ ] Ports 80, 8080, 3310 available
- [ ] Sufficient disk space (10GB+ recommended)
- [ ] Network connectivity for ClamAV updates

#### Post-Deployment Verification
- [ ] Frontend accessible at http://localhost
- [ ] Backend health check: `curl http://localhost:8080/api/health`
- [ ] ClamAV status: `sudo docker-compose logs clamav`
- [ ] File upload test with small image/PDF

### 🛡️ Security Configuration

#### Production Security Features
- ✅ Virus scanning enabled (ClamAV)
- ✅ File type validation (JPG, PNG, PDF only)  
- ✅ File size limits (50MB max)
- ✅ Input sanitization and validation
- ✅ Secure file storage outside web root
- ✅ CORS protection configured

#### Additional Security Recommendations
- Use reverse proxy with SSL/TLS in production
- Configure firewall rules for port access
- Regular ClamAV database updates
- Monitor disk usage for uploads directory
- Implement log rotation for container logs

### 📊 Monitoring & Maintenance

#### Health Checks
```bash
# Backend health
curl http://localhost:8080/api/health

# Container status  
sudo docker-compose ps

# Service logs
sudo docker-compose logs backend
sudo docker-compose logs clamav
sudo docker-compose logs frontend
```

#### Log Locations
- **Backend**: `sudo docker-compose logs backend`
- **ClamAV**: `sudo docker-compose logs clamav`  
- **Frontend**: `sudo docker-compose logs frontend`

#### Maintenance Tasks
- Regular ClamAV database updates (automatic)
- Monitor `/uploads` directory size
- Log rotation (configure externally)
- Container updates (rebuild with latest images)

### 🔄 Updates & Upgrades

#### Updating the Application
```bash
# Pull latest changes
git pull origin main

# Rebuild and restart
sudo docker-compose down
sudo docker-compose up --build -d
```

#### Backup Strategy
```bash
# Backup uploaded files
sudo cp -r ./uploads ./uploads-backup-$(date +%Y%m%d)

# Backup configuration
cp docker-compose.yml docker-compose.yml.backup
```

### 🐛 Troubleshooting

#### Common Issues
1. **Container won't start**: Check port availability and Docker permissions
2. **Upload fails**: Verify ClamAV is healthy and running
3. **High memory usage**: Monitor file processing, adjust container limits
4. **Disk space**: Clean old uploads, implement retention policy

#### Support Commands
```bash
# Reset all containers
sudo docker-compose down && sudo docker-compose up --build -d

# Check container resource usage
sudo docker stats

# View detailed logs
sudo docker-compose logs -f backend
```

### 📈 Performance Tuning

#### Production Optimizations
- **File Size**: Configured for 50MB max upload
- **Memory**: Java heap optimized for container environment
- **Storage**: Date-organized directory structure
- **Processing**: Virus scanning optimized for speed

#### Scaling Considerations
- Add load balancer for multiple backend instances
- Implement shared storage for multi-container deployment  
- Configure database for metadata storage (future enhancement)
- Add CDN for static file serving

---

## 🎯 Production Ready!

DocUp v1.0.0 is production-ready with:
- ✅ Stable file upload and storage
- ✅ Comprehensive virus scanning  
- ✅ Security hardened deployment
- ✅ Docker containerization
- ✅ Monitoring and logging

**Note**: OCR processing is temporarily disabled in v1.0.0 for stability. Full OCR functionality will be restored in v1.1.0 with enhanced error handling and performance optimizations.
