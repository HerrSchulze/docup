# Use OpenJDK 17 as base image (matching project configuration)
FROM openjdk:17-jdk-slim

# Multi-stage build for better caching and smaller final image
FROM openjdk:17-jdk-slim as builder

# Install Tesseract OCR and Maven
RUN apt-get update && \
    apt-get install -y \
        tesseract-ocr \
        tesseract-ocr-eng \
        tesseract-ocr-deu \
        libtesseract-dev \
        maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml ./

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

# Install Tesseract OCR for runtime
RUN apt-get update && \
    apt-get install -y \
        tesseract-ocr \
        tesseract-ocr-eng \
        tesseract-ocr-deu \
        libtesseract-dev \
        curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/docup-backend-0.0.1-SNAPSHOT.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker
ENV UPLOAD_PATH=/app/uploads
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
CMD ["java", "-jar", "app.jar"]
