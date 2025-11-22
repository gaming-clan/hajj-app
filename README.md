# 🕌 Hajj & Umrah Guide - Complete Multi-Platform Islamic Application

[![CI/CD](https://github.com/muslim/hajj-app/workflows/CI-CD-React-Native/badge.svg)](https://github.com/muslim/hajj-app/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-iOS%20%7C%20Android%20%7C%20Web-blue.svg)](https://github.com/muslim/hajj-app)
[![Languages](https://img.shields.io/badge/Languages-Arabic%20%7C%20English%20%7C%20Albanian-green.svg)](https://github.com/muslim/hajj-app)

A comprehensive multi-platform mobile application providing verified Islamic guidance for Hajj and Umrah rituals, built with React Native, Android Native, and Flutter implementations.

## 🕌 **Project Overview**

The **Hajj & Umrah Guide** is a meticulously crafted digital companion for Muslims performing the sacred pilgrimages of Hajj and Umrah. This project demonstrates best practices in multi-platform development while maintaining the highest standards of religious accuracy, accessibility, and user experience.

### ✨ **Key Features**

#### **🕌 Verified Islamic Content**
- **100% Authenticated**: All content verified against authentic Islamic sources (Quran, Sahih Bukhari, Muslim, Abu Dawud)
- **Original Arabic Text**: Complete Arabic text for all du'aas and religious formulas
- **Scholar-Approved**: Content reviewed and approved by qualified Islamic scholars
- **Multiple Fiqh Schools**: Comprehensive coverage of major Islamic jurisprudence schools

#### **🌍 Multilingual Support**
- **Arabic (العربية)**: Full RTL support with proper Arabic typography
- **English**: Complete translation with phonetic Arabic support
- **Albanian (Shqip)**: Native language support for Balkan Muslims

#### **♿ Accessibility Excellence**
- **Screen Reader Support**: Full TalkBack and VoiceOver compatibility
- **High Contrast Mode**: Enhanced visibility for visual impairments
- **Adjustable Font Sizes**: Support for elderly users
- **RTL Layout**: Proper right-to-left support for Arabic content

#### **🔒 Enterprise Security**
- **End-to-End Encryption**: AES-256 encryption for sensitive data
- **Network Security**: HTTPS enforcement with certificate pinning
- **Input Validation**: Comprehensive protection against XSS and injection attacks
- **Secure Storage**: Platform-specific secure storage implementations

#### **📱 Cross-Platform Excellence**
- **React Native**: TypeScript-based cross-platform solution
- **Android Native**: Kotlin-based native implementation with MVVM architecture
- **Flutter**: Dart-based beautiful UI with Material Design 3

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