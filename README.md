# Easy Barcode Reader – QR Scanner

Easy Barcode Reader is a fast, lightweight, and modern Android application for scanning **QR codes and barcodes**.  
Built with **Kotlin**, **CameraX**, and **Jetpack Compose**, it provides a clean UI, smooth performance, and essential features like history tracking and favorites.

---

## Features

### Real Time QR & Barcode Scanning
- Powered by **CameraX**
- Fast detection with auto focus
- Supports all common QR and barcode formats

### Scan History
- Every scanned result is automatically saved
- Each entry includes:
  - URL preview
  - **Share** button
  - **Go to Site** button
- Items can be added to Favorites

### Favorites
- Save important scanned results
- Access them quickly from the bottom navigation bar
- Share or open them instantly

### Permission Handling
- Android 13+ compatible camera permission dialog
- Supports:
  - *While using the app*
  - *Only this time*
  - *Don’t allow*

### Modern UI (Jetpack Compose)
- Clean and minimal design
- Bottom navigation:
  - **Scan**
  - **History**
  - **Favorites**
- Smooth transitions and responsive layout

---

## Screenshots

<p align="center">
  <img src="SCREENSHOT_URL_1" width="30%" alt="Scan Screen" />
  <img src="SCREENSHOT_URL_2" width="30%" alt="History Screen" />
  <img src="SCREENSHOT_URL_3" width="30%" alt="Favorites Screen" />
  <img src="SCREENSHOT_URL_3" width="30%" alt="Favorites Screen" />
  <img src="SCREENSHOT_URL_3" width="30%" alt="Favorites Screen" />
</p>

---

## Tech Stack

| Technology | Usage |
|-----------|--------|
| **Kotlin** | Main programming language |
| **CameraX** | Real time camera scanning |
| **Jetpack Compose** | UI components and layout |
| **ViewModel / State** | Managing UI state |
| **Navigation** | Bottom navigation bar |
| **Data Storage** | Local history & favorites |

---

## Project Structure
```text
Easy_Barcode_Reader_QR_Scanner/
├── app/
│   ├── release/                     # Generated release APK/AAB files
│   └── src/
│       ├── main/
│       │   ├── java/com/main/easybarcode/
│       │   │   ├── ui/              # Jetpack Compose UI components & screens
│       │   │   ├── data/            # Local storage, models, repositories
│       │   │   ├── navigation/      # Bottom navigation & route management
│       │   │   └── viewmodel/       # ViewModels for state handling
│       │   └── res/                 # App resources (icons, strings, themes)
│       └── test/                    # Unit tests
├── gradle/                          # Gradle wrapper files
├── LICENSE                          # Apache License 2.0
├── README.md                        # Project documentation
└── .gitignore                       # Git ignore rules

