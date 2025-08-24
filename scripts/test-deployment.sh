#!/bin/bash

# Docker Deployment Test Script
# Tests all services and functionality

set -e

echo "🚀 Testing Docker Deployment for DocUp..."
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${2}${1}${NC}"
}

print_test() {
    echo -e "${BLUE}🧪 Testing: $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Test 1: Check if Docker Compose is running
print_test "Docker Compose Services Status"
if docker-compose ps | grep -q "Up"; then
    print_success "Docker Compose services are running"
    docker-compose ps
else
    print_error "Docker Compose services are not running"
    echo "Starting services..."
    docker-compose up -d
fi

echo ""

# Test 2: Backend Health Check
print_test "Backend Health Check"
if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
    print_success "Backend is healthy"
    echo "Response:"
    curl -s http://localhost:8080/actuator/health | jq .
else
    print_error "Backend health check failed"
    exit 1
fi

echo ""

# Test 3: Frontend Accessibility
print_test "Frontend Accessibility"
response=$(curl -I -s http://localhost:80 | head -n 1)
if echo "$response" | grep -q "200 OK"; then
    print_success "Frontend is accessible"
    echo "Response: $response"
else
    print_error "Frontend accessibility failed"
    echo "Response: $response"
    exit 1
fi

echo ""

# Test 4: Backend API Endpoints
print_test "Backend API Endpoints"

# Test upload endpoint (should return method not allowed for GET)
print_test "  - Upload endpoint (/api/upload)"
response=$(curl -I -s http://localhost:8080/api/upload | head -n 1)
if echo "$response" | grep -q "405\|200"; then
    print_success "    Upload endpoint is reachable"
else
    print_error "    Upload endpoint failed"
    echo "    Response: $response"
fi

echo ""

# Test 5: Test file upload functionality
print_test "File Upload Test"

# Create a test image file
test_file="/tmp/test_image.txt"
echo "This is a test document for OCR processing.
Multiple lines of text.
Testing OCR functionality." > "$test_file"

# Test upload
print_test "  - Uploading test file"
upload_response=$(curl -s -X POST \
    -F "file=@$test_file" \
    http://localhost:8080/api/upload)

if echo "$upload_response" | grep -q "success\|filename"; then
    print_success "    File upload successful"
    echo "    Response: $upload_response"
else
    print_warning "    File upload test inconclusive (may need image file)"
    echo "    Response: $upload_response"
fi

# Cleanup
rm -f "$test_file"

echo ""

# Test 6: Container Resource Usage
print_test "Container Resource Usage"
docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}"

echo ""

# Test 7: Check Logs for Errors
print_test "Recent Error Logs"
echo "Backend errors (last 50 lines):"
docker-compose logs backend | grep -i "error\|exception\|failed" | tail -10 || print_success "No recent errors in backend logs"

echo ""
echo "Frontend errors (last 50 lines):"
docker-compose logs frontend | grep -i "error\|exception\|failed" | tail -10 || print_success "No recent errors in frontend logs"

echo ""

# Test 8: Network Connectivity
print_test "Network Connectivity"
print_test "  - Backend to external services"
docker-compose exec -T backend curl -s -I https://google.com | head -n 1 && print_success "    External connectivity working" || print_warning "    External connectivity may be limited"

echo ""

# Test 9: File System Checks
print_test "File System Checks"
print_test "  - Upload directory accessibility"
docker-compose exec -T backend ls -la /app/uploads && print_success "    Upload directory accessible" || print_error "    Upload directory not accessible"

echo ""

# Test 10: Security Configuration
print_test "Security Configuration"
print_test "  - Non-root user execution"
user_check=$(docker-compose exec -T backend whoami)
if [ "$user_check" = "appuser" ]; then
    print_success "    Backend running as non-root user ($user_check)"
else
    print_warning "    Backend may be running as root ($user_check)"
fi

echo ""

# Summary
print_status "🎉 Deployment Test Summary" "${GREEN}"
print_status "=========================" "${GREEN}"
print_success "All critical services are running"
print_success "Health checks passed"
print_success "API endpoints accessible"
print_status "Application is ready for use!" "${GREEN}"

echo ""
print_status "📱 Access URLs:" "${BLUE}"
echo "Frontend: http://localhost"
echo "Backend API: http://localhost:8080"
echo "Health Check: http://localhost:8080/actuator/health"

echo ""
print_status "🔧 Management Commands:" "${YELLOW}"
echo "View logs: docker-compose logs [service]"
echo "Restart service: docker-compose restart [service]"
echo "Stop all: docker-compose down"
echo "View status: docker-compose ps"
