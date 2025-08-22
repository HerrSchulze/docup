#!/bin/bash

# DocUp Docker Deployment Test Script
echo "Testing DocUp Docker Deployment..."
echo "=================================="

# Check if Docker Compose is running
echo "1. Checking Docker Compose services..."
docker-compose ps

echo -e "\n2. Testing backend health endpoint..."
curl -s http://localhost/api/health | jq '.' 2>/dev/null || curl -s http://localhost/api/health

echo -e "\n3. Testing file upload with PNG..."
# Create a minimal test PNG
printf '\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1f\x15\xc4\x89\x00\x00\x00\nIDATx\x9cc\x00\x01\x00\x00\x05\x00\x01\r\n-\xdb\x00\x00\x00\x00IEND\xaeB`\x82' > /tmp/test-upload.png

# Upload the file
curl -s -X POST -F "file=@/tmp/test-upload.png" http://localhost/api/upload | jq '.' 2>/dev/null || curl -s -X POST -F "file=@/tmp/test-upload.png" http://localhost/api/upload

echo -e "\n4. Testing invalid file type rejection..."
echo "This should fail validation" > /tmp/test-invalid.txt
curl -s -X POST -F "file=@/tmp/test-invalid.txt" http://localhost/api/upload | jq '.' 2>/dev/null || curl -s -X POST -F "file=@/tmp/test-invalid.txt" http://localhost/api/upload

echo -e "\n\n✅ Docker deployment test completed!"
echo "Frontend: http://localhost"
echo "Backend API: http://localhost/api"
echo "Backend Health: http://localhost/api/health"

# Cleanup
rm -f /tmp/test-upload.png /tmp/test-invalid.txt
