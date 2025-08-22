# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Install Tesseract OCR
RUN apt-get update && \
    apt-get install -y tesseract-ocr tesseract-ocr-eng tesseract-ocr-deu && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Create uploads directory
RUN mkdir -p /app/uploads

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker
ENV UPLOAD_PATH=/app/uploads
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata

# Run application
CMD ["java", "-jar", "target/docup-backend-0.0.1-SNAPSHOT.jar"]
