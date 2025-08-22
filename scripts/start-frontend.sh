#!/bin/bash

echo "🌐 Starting Simple Frontend Test Server"
echo "======================================="

# Check if Python 3 is installed
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 is not installed. Please install Python 3"
    exit 1
fi

echo "✅ Python 3 is available"

# Navigate to frontend-simple directory
cd "$(dirname "$0")/../frontend-simple"

echo "📁 Current directory: $(pwd)"
echo "🚀 Starting HTTP server on port 3000..."
echo ""
echo "   Frontend will be available at: http://localhost:3000"
echo "   Make sure the backend is running on port 8080"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

# Start Python HTTP server
python3 -m http.server 3000
