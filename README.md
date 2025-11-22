# Hajj Rules App - React Native

A comprehensive guide application for Hajj rules and regulations built with React Native and TypeScript.

## 📱 Features

- **Complete Hajj Guide**: Comprehensive information about Hajj rules and regulations
- **Category-based Navigation**: Rules organized into logical categories
- **Search Functionality**: Search through all rules and descriptions
- **Miqat Information**: Interactive information about pilgrimage starting points
- **Multilingual Support**: Content in Albanian (Shqip)
- **Modern UI**: Clean, modern interface with Islamic-inspired design

## 🏗️ Architecture

### Technology Stack
- **React Native 0.72**: Cross-platform mobile framework
- **TypeScript**: Type-safe JavaScript
- **React Navigation 6**: Navigation library
- **React Native Vector Icons**: Icon library
- **React Native Paper**: Material Design components

### Project Structure
```
hajj-app/
├── src/
│   ├── assets/
│   │   ├── data/          # JSON data files
│   │   └── images/        # App images and icons
│   ├── screens/           # Screen components
│   ├── services/          # Data services and utilities
│   ├── types/            # TypeScript type definitions
│   └── ...
├── App.tsx               # Main app component
├── index.js              # App entry point
├── package.json          # Dependencies and scripts
└── ...
```

### Navigation Structure
- **Stack Navigator**: Main navigation container
  - **Splash Screen**: App loading screen
  - **Tab Navigator**: Bottom tab navigation
    - Home
    - Categories
    - Map (Miqat)
    - About
  - **Detail Screen**: Rule details view
  - **Search Screen**: Search functionality

## 🚀 Getting Started

### Prerequisites
- Node.js (>= 16.0.0)
- React Native CLI
- Android Studio (for Android development)
- Xcode (for iOS development, macOS only)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/gaming-clan/hajj-app.git
   cd hajj-app
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **iOS Setup (macOS only)**
   ```bash
   cd ios && pod install && cd ..
   ```

### Running the Application

#### For Android
```bash
npm run android
```

#### For iOS (macOS only)
```bash
npm run ios
```

#### Start Metro Bundler
```bash
npm start
```

### Building for Production

#### Android APK
```bash
npm run build:android
```

#### Android AAB
```bash
npm run build:android:bundle
```

## 📚 Content Structure

### Categories
1. **Shtyllat e Islamit** - The Five Pillars of Islam
2. **Edukata e Udhëtimit** - Travel Etiquette
3. **Ihrami** - Ihram Rules and Requirements
4. **Ndalesat gjatë Ihramit** - Prohibitions during Ihram
5. **Vendcaktimet (Miqat)** - Pilgrimage Starting Points

### Data Format
The app uses JSON files for content management, making it easy to update and localize content.

## 🛠️ Development

### Available Scripts
- `npm start` - Start Metro bundler
- `npm run android` - Run on Android
- `npm run ios` - Run on iOS
- `npm run lint` - Run ESLint
- `npm test` - Run tests
- `npm run build:android` - Build Android APK
- `npm run build:android:bundle` - Build Android AAB

### Code Style
The project uses ESLint and Prettier for code formatting and style consistency.

## 🔄 Migration from Android Native

This app was migrated from a native Android application to React Native, featuring:
- Complete UI redesign with modern React Native components
- Improved navigation with React Navigation
- Enhanced search functionality
- Better performance and cross-platform compatibility

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📧 Support

For support and questions, please open an issue on GitHub or contact the development team.

## 🙏 Acknowledgments

- Islamic content based on Quran and Sunnah
- UI inspiration from modern Islamic apps
- Community feedback and contributions