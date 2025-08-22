#!/bin/bash

# DocUp Development Setup Script

echo "🚀 Setting up DocUp development environment..."

# Check prerequisites
echo "📋 Checking prerequisites..."

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

echo "✅ Prerequisites check passed!"

# Create necessary directories
echo "📁 Creating necessary directories..."
mkdir -p uploads/{2025,2024,2023}
mkdir -p clamav

# Set permissions
echo "🔐 Setting permissions..."
chmod 755 uploads
chmod 755 clamav

# Start services
echo "🐳 Starting Docker services..."
docker-compose up --build -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 30

# Check service health
echo "🏥 Checking service health..."
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Backend is healthy"
else
    echo "⚠️  Backend might still be starting up"
fi

if curl -f http://localhost > /dev/null 2>&1; then
    echo "✅ Frontend is healthy"
else
    echo "⚠️  Frontend might still be starting up"
fi

echo ""
echo "🎉 DocUp setup complete!"
echo ""
echo "📱 Access the application:"
echo "   Frontend: http://localhost"
echo "   Backend API: http://localhost:8080"
echo ""
echo "🛠️  Useful commands:"
echo "   View logs: docker-compose logs -f"
echo "   Stop services: docker-compose down"
echo "   Restart services: docker-compose restart"
echo ""
