# **Anforderungsdokument – Webapplikation für Datei-Upload mit OCR und Virenscan**

## 1\. Zielsetzung

Die Webapplikation soll es Nutzern ermöglichen, Dateien (Bilder und PDFs) über den Browser hochzuladen, diese vorab zu prüfen (Vorschau, OCR-Textanzeige) und serverseitig zu verarbeiten (Virenscan, OCR, Speicherung).  
Das System soll eine moderne, benutzerfreundliche Oberfläche im Stil von creditplus.de bieten und vollständig containerisiert via docker-compose deploybar sein.

## 2\. Technologien

| Bereich | Technologie / Tool |
| --- | --- |
| Frontend | Angular, Tesseract.js (OCR-Vorschau) |
| Backend | Spring Boot 3, ClamAV (Virenscan), Tesseract (OCR) |
| Storage | Lokales Filesystem |
| Deployment | docker-compose |

## 3\. Funktionale Anforderungen

### 3.1 Frontend

- Kamera-Zugriff & Datei-Upload
  - Zugriff auf Kamera (WebRTC API) für Direktaufnahme.
  - Upload von Dateien (Bilder, PDFs) per Drag & Drop oder Dateiauswahl.
- Vorschau
  - Anzeige hochgeladener Bilder direkt im Browser.
  - PDF-Vorschau (erste Seite als Thumbnail).
- Fortschrittsanzeige
  - Upload-Progressbar in Prozent.
- OCR-Vorschau
  - Clientseitige Texterkennung mit Tesseract.js.
  - Anzeige des erkannten Textes vor dem Upload.

### 3.2 Backend

- Dateiempfang
  - REST-Endpoint (POST /upload) für Datei-Uploads.
- Virenscan
  - Integration von ClamAV zur Prüfung hochgeladener Dateien.
  - Upload wird abgelehnt, wenn Datei infiziert ist.
- OCR
  - Serverseitige Texterkennung mit Tesseract.
  - Unterstützung für Bilder und PDFs.
- Speicherung
  - Speicherung der Originaldatei im lokalen Filesystem.
  - Struktur: /uploads/{yyyy}/{MM}/{dd}/{uuid}\_filename.ext
- Metadaten-Rückgabe
  - JSON-Response mit:
    - Dateiname
    - Dateigröße
    - MIME-Type
    - OCR-Text
    - Upload-Datum/Zeit

## 4\. Nicht-funktionale Anforderungen

- Performance: Upload und OCR-Verarbeitung sollen bei Dateien < 10 MB unter 5 Sekunden dauern.
- Sicherheit:
  - HTTPS-Unterstützung.
  - Eingeschränkte Dateitypen (Bilder: JPG, PNG; PDFs).
  - Virenscan vor Speicherung.
- Usability:
  - Responsive Design (Mobile, Tablet, Desktop).
  - UI-Layout an creditplus.de angelehnt.
- Wartbarkeit:
  - Saubere Trennung von Frontend und Backend.
  - Konfiguration über .env-Dateien.

## 5\. API-Spezifikation (Beispiel)

POST /upload

Request:

Content-Type: multipart/form-data

file: &lt;Datei&gt;

Response (200 OK):

{

"filename": "uuid_originalname.pdf",

"size": 123456,

"mimeType": "application/pdf",

"ocrText": "Erkannter Text...",

"uploadedAt": "2025-08-20T20:31:00Z"

}

## 6\. Deployment (docker-compose)

- Services:
  - frontend (Angular App, Nginx-Serve)
  - backend (Spring Boot 3)
  - clamav (ClamAV Daemon)
- Volumes:
  - /uploads für persistente Dateispeicherung.
- Netzwerk:
  - Gemeinsames Docker-Netzwerk für interne Kommunikation.
- Beispielstruktur:

version: "3.8"

services:

frontend:

build: ./frontend

ports:

\- "80:80"

backend:

build: ./backend

ports:

\- "8080:8080"

volumes:

\- ./uploads:/app/uploads

depends_on:

\- clamav

clamav:

image: clamav/clamav:latest

ports:

\- "3310:3310"

volumes:

\- ./clamav:/var/lib/clamav

## 7\. Erweiterungspotenzial

- Benutzer-Authentifizierung (JWT).
- Mehrsprachige OCR-Unterstützung.
- Speicherung in Cloud-Storage (optional).
- Asynchrone Verarbeitung mit Message Queue.