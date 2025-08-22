# DocUp Project Structure

This document describes the complete directory structure of the DocUp application.

## Root Directory Structure

```
docup/
├── .env                          # Environment variables
├── .gitignore                    # Git ignore rules
├── docker-compose.yml            # Docker orchestration
├── README.md                     # Main project documentation
├── frontend/                     # Angular frontend application
├── backend/                      # Spring Boot backend API
├── uploads/                      # File storage directory
├── clamav/                       # ClamAV virus definition storage
├── docker/                       # Docker configuration files
├── docs/                         # Additional documentation
├── scripts/                      # Utility scripts
└── .github/                      # GitHub workflows and docs
    └── specification.md          # Project requirements
```

## Frontend Structure (Angular)

```
frontend/
├── README.md                     # Frontend documentation
├── package.json                  # Node.js dependencies
├── angular.json                  # Angular CLI configuration
├── tsconfig.json                 # TypeScript configuration
├── Dockerfile                    # Frontend Docker image
├── src/
│   ├── main.ts                   # Application entry point
│   ├── index.html                # HTML template
│   ├── styles.css                # Global styles
│   ├── app/
│   │   ├── app.module.ts         # Main Angular module
│   │   ├── app.component.ts      # Root component
│   │   ├── app-routing.module.ts # Routing configuration
│   │   ├── components/           # Angular components
│   │   │   ├── upload/           # File upload component
│   │   │   ├── preview/          # File preview component
│   │   │   ├── camera/           # Camera capture component
│   │   │   ├── progress/         # Upload progress component
│   │   │   └── shared/           # Shared components
│   │   ├── services/             # Angular services
│   │   │   ├── upload.service.ts # File upload service
│   │   │   ├── ocr.service.ts    # OCR service (Tesseract.js)
│   │   │   ├── camera.service.ts # Camera service
│   │   │   └── api.service.ts    # Backend API service
│   │   └── models/               # TypeScript interfaces
│   │       ├── upload-response.interface.ts
│   │       ├── file-metadata.interface.ts
│   │       └── ocr-result.interface.ts
│   ├── assets/                   # Static assets
│   │   ├── images/               # Image files
│   │   ├── icons/                # Icon files
│   │   └── styles/               # SCSS/CSS files
│   └── environments/             # Environment configurations
│       ├── environment.ts        # Development environment
│       └── environment.prod.ts   # Production environment
```

## Backend Structure (Spring Boot)

```
backend/
├── README.md                     # Backend documentation
├── pom.xml                       # Maven dependencies
├── Dockerfile                    # Backend Docker image
├── src/
│   ├── main/
│   │   ├── java/com/docup/
│   │   │   ├── DocUpApplication.java     # Main application class
│   │   │   ├── controller/               # REST controllers
│   │   │   │   ├── UploadController.java # File upload endpoint
│   │   │   │   └── HealthController.java # Health check endpoint
│   │   │   ├── service/                  # Business logic services
│   │   │   │   ├── FileService.java      # File handling service
│   │   │   │   ├── VirusScanService.java # ClamAV integration
│   │   │   │   ├── OcrService.java       # Tesseract OCR service
│   │   │   │   └── StorageService.java   # File storage service
│   │   │   ├── model/                    # Data models
│   │   │   │   ├── UploadResponse.java   # API response model
│   │   │   │   ├── FileMetadata.java     # File metadata model
│   │   │   │   └── ScanResult.java       # Virus scan result model
│   │   │   ├── config/                   # Configuration classes
│   │   │   │   ├── FileStorageConfig.java    # Storage configuration
│   │   │   │   ├── CorsConfig.java           # CORS configuration
│   │   │   │   └── SecurityConfig.java       # Security configuration
│   │   │   ├── exception/                # Custom exceptions
│   │   │   │   ├── FileStorageException.java
│   │   │   │   ├── VirusDetectedException.java
│   │   │   │   └── OcrProcessingException.java
│   │   │   └── util/                     # Utility classes
│   │   │       ├── FileUtils.java        # File utility methods
│   │   │       └── UuidGenerator.java    # UUID generation
│   │   └── resources/
│   │       ├── application.yml           # Main configuration
│   │       ├── application-docker.yml    # Docker configuration
│   │       ├── application-test.yml      # Test configuration
│   │       └── static/                   # Static resources
│   └── test/
│       └── java/                         # Test classes
│           ├── controller/               # Controller tests
│           ├── service/                  # Service tests
│           └── integration/              # Integration tests
```

## Storage Structure

```
uploads/
├── .gitkeep                      # Keep directory in git
└── {yyyy}/                       # Year-based organization
    └── {MM}/                     # Month-based organization
        └── {dd}/                 # Day-based organization
            └── {uuid}_filename.ext   # Stored files
```

## Docker Configuration

```
docker/
├── frontend/
│   └── nginx.conf                # Nginx configuration for frontend
├── backend/
│   └── application-docker.yml    # Backend Docker configuration
└── clamav/
    └── clamd.conf                # ClamAV configuration
```

## Documentation

```
docs/
├── api/                          # API documentation
│   ├── swagger.yml               # OpenAPI specification
│   └── endpoints.md              # API endpoint documentation
├── deployment/                   # Deployment guides
│   ├── docker.md                 # Docker deployment
│   └── production.md             # Production deployment
├── development/                  # Development guides
│   ├── setup.md                  # Development setup
│   └── contributing.md           # Contribution guidelines
└── architecture/                 # Architecture documentation
    ├── overview.md               # System overview
    └── security.md               # Security considerations
```

## Scripts

```
scripts/
├── setup.sh                     # Development environment setup
├── deploy.sh                    # Deployment script
├── backup.sh                    # Backup script
└── clean.sh                     # Cleanup script
```

## Key Features by Directory

### Frontend (`/frontend`)
- Angular application with modern UI
- Camera integration for photo capture
- Drag & drop file upload
- Real-time OCR preview with Tesseract.js
- Upload progress tracking
- Responsive design

### Backend (`/backend`)
- Spring Boot 3 REST API
- ClamAV virus scanning integration
- Tesseract OCR processing
- Organized file storage
- Comprehensive error handling

### Infrastructure
- Docker Compose orchestration
- ClamAV service for virus scanning
- Nginx for frontend serving
- Volume persistence for uploads and virus definitions

This structure supports the full feature set described in the specification while maintaining clean separation of concerns and scalability.
