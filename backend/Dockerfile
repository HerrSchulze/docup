# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Install Tesseract OCR with language data
RUN apt-get update && \
    apt-get install -y \
        tesseract-ocr \
        tesseract-ocr-eng \
        tesseract-ocr-deu \
        libtesseract-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    # Verify tessdata location and create symbolic link if needed
    find /usr -name "*.traineddata" -type f 2>/dev/null | head -5

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
# Set Tesseract data path
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata

# Run application
CMD ["java", "-jar", "target/docup-backend-0.0.1-SNAPSHOT.jar"]
