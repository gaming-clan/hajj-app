module.exports = {
  dependencies: {
    'react-native-vector-icons': {
      platforms: {
        ios: {
          sourceDir: '../node_modules/react-native-vector-icons/Fonts',
          project: 'ios/HajjApp.xcodeproj',
        },
      },
    },
    'react-native-fast-image': {
      platforms: {
        android: {
          sourceDir: '../node_modules/react-native-fast-image/android',
          packageImportPath: 'io.l rounde.fastimage.FastImagePackage',
        },
      },
    },
  },
  assets: ['./src/assets/fonts/'],
  commands: {
    // Custom build commands for production
    'build:android-prod': [
      'cd android && ./gradlew assembleRelease',
      'cd android && ./gradlew bundleRelease',
    ],
    'build:ios-prod': [
      'cd ios && xcodebuild -workspace HajjApp.xcworkspace -scheme HajjApp -configuration Release -destination generic/platform=iOS -archivePath HajjApp.xcarchive archive',
      'cd ios && xcodebuild -exportArchive -archivePath HajjApp.xcarchive -exportOptionsPlist ExportOptions.plist -exportPath ./build',
    ],
  },
};