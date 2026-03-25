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
  - While using the app
  - Only this time
  - Don’t allow
    

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
<img src="https://github.com/user-attachments/assets/82432605-c18f-4efc-b115-acdca6d35580" width="30%" alt="Scan Screen" />
<img src="https://github.com/user-attachments/assets/d85de76a-24ee-4f4a-a0b5-e242e2020180" width="30%" alt="Favorites Screen" />
<img src="https://github.com/user-attachments/assets/df34dd43-aa87-4925-af41-492c8ecb22b2" width="30%" alt="Favorites Screen" />
<img src="https://github.com/user-attachments/assets/dc66d7a9-9b03-4b86-aa66-ac553579c01d" width="30%" alt="History Screen" />
<img src="https://github.com/user-attachments/assets/df64e47e-bd4e-49db-90b7-5db7b3ac466f" width="30%" alt="Favorites Screen" />
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
QR-BARCODE-SCANNER/
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
```
## Installation

To set up the project locally:

1. **Clone the repository**

```bash
git clone https://github.com/mesu1gok/qr-barcode-scanner.git
```
2. **Open the project in Android Studio**

- Launch Android Studio.

- Select Open an existing project.

- Choose the cloned folder.

3. **Build & Run**

- Connect an Android device or start an emulator.

- Click Run in Android Studio.

## License
This project is licensed under the Apache License 2.0.  
See the [LICENSE](https://github.com/mesu1gok/qr-barcode-scanner/blob/main/LICENSE) file for details.

