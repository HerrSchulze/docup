#!/bin/bash

echo "🧪 Testing DocUp Backend Locally"
echo "================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17+"
    exit 1
fi

# Check Java version
java_version=$(java -version 2>&1 | grep -oP '(?<=version ")[^"]*')
echo "☕ Java version: $java_version"

# Check Java version
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "✅ Java version: $java_version"

# Check if Tesseract is installed
if ! command -v tesseract &> /dev/null; then
    echo "⚠️  Tesseract OCR is not installed. Installing..."
    sudo apt-get update && sudo apt-get install -y tesseract-ocr tesseract-ocr-eng
else
    tesseract_version=$(tesseract --version 2>&1 | head -n 1)
    echo "✅ $tesseract_version"
fi

# Navigate to backend directory
cd backend

echo ""
echo "🔨 Building backend..."
./mvnw clean compile

if [ $? -eq 0 ]; then
    echo "✅ Backend compilation successful!"
    echo ""
    echo "🚀 Starting backend server..."
    echo "   Backend will be available at: http://localhost:8080"
    echo "   Health check: http://localhost:8080/api/health"
    echo "   Upload info: http://localhost:8080/api/upload/info"
    echo ""
    echo "Press Ctrl+C to stop the server"
    echo ""
    
    # Set Tesseract data path
    export TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
    
    ./mvnw spring-boot:run
else
    echo "❌ Backend compilation failed!"
    exit 1
fi
