# Frontend (Angular)

## Development Setup

### Prerequisites
- Node.js 18+
- Angular CLI 16+

### Installation
```bash
npm install -g @angular/cli
npm install
```

### Development Server
```bash
ng serve
# Application will be available at http://localhost:4200
```

### Build
```bash
ng build --prod
```

## Features

- **Camera Integration**: WebRTC API for direct photo capture
- **File Upload**: Drag & drop support for images and PDFs
- **Preview**: Image display and PDF thumbnail generation
- **OCR Preview**: Client-side text recognition with Tesseract.js
- **Progress Tracking**: Real-time upload progress indication
- **Responsive Design**: Mobile-first approach with creditplus.de styling

## Project Structure

```
src/
├── app/
│   ├── components/         # Angular components
│   │   ├── upload/         # File upload component
│   │   ├── preview/        # File preview component
│   │   ├── camera/         # Camera capture component
│   │   └── progress/       # Upload progress component
│   ├── services/           # Angular services
│   │   ├── upload.service.ts      # File upload service
│   │   ├── ocr.service.ts         # OCR service
│   │   └── camera.service.ts      # Camera service
│   ├── models/             # TypeScript interfaces
│   └── app.module.ts       # Main application module
├── assets/                 # Static assets
└── environments/           # Environment configurations
```

## API Integration

The frontend communicates with the Spring Boot backend via REST API:
- `POST /upload` - Upload files for processing

## Docker

```bash
# Build Docker image
docker build -t docup-frontend .

# Run container
docker run -p 80:80 docup-frontend
```
