# Hajj App - Project Update and Fixes

This document summarizes the changes made to the Hajj App repository, addressing various build errors and updating dependencies. The goal was to ensure the application builds successfully and to improve its overall stability.

## Summary of Changes Made

### 1. DataManager.java File Fix
- **Issue:** Duplicate `import java.util.Locale;` statement at the end of the `DataManager.java` file.
- **Resolution:** The redundant import statement was removed from `/home/ubuntu/hajj-app/hajj_app/app/src/main/java/com/muslim/hajjrules/util/DataManager.java`.

### 2. Outdated Dependencies Update
- **Issue:** The `build.gradle` file contained outdated dependencies.
- **Resolution:** The following dependencies in `hajj_app/app/build.gradle` were updated to their latest stable versions:
    - `androidx.appcompat:appcompat` updated to `1.7.1`
    - `androidx.constraintlayout:constraintlayout` updated to `2.2.1`
    - `com.google.android.material:material` updated to `1.12.0`

### 3. JdkImageTransform Error Resolution
- **Issue:** Persistent `JdkImageTransform` errors during the build process.
- **Resolution:** After attempting to re-export environment variables and cleaning the project, the error persisted. The solution implemented was to add `aaptOptions { noCompress 'png', 'jpg' }` to the `android` block in `hajj_app/app/build.gradle`. This prevents compression of PNG and JPG assets, which was causing the `JdkImageTransform` error.

### 4. Removal of Problematic Mipmap Files
- **Issue:** Compilation errors related to `ic_launcher.png` and `ic_launcher_round.png` in `mipmap-mdpi`.
- **Resolution:** The problematic files `/home/ubuntu/hajj-app/hajj_app/app/src/main/res/mipmap-mdpi/ic_launcher.png` and `/home/ubuntu/hajj-app/hajj_app/app/src/main/res/mipmap-mdpi/ic_launcher_round.png` were removed. This resolved the `AAPT: error: file failed to compile` errors.

## Build Verification

After implementing the fixes, the application was successfully built using `./gradlew clean build` and `./gradlew assembleDebug` commands, indicating that the critical build errors have been resolved.

## Recommendations for Future Improvements

1.  **Lint Warnings:** Although the critical build errors are resolved, there might be remaining lint warnings. It is recommended to address these warnings to improve code quality and maintainability.
2.  **Further Dependency Updates:** Regularly check for and update all dependencies to their latest stable versions to ensure security, performance, and compatibility.
3.  **Automated Testing:** Implement a comprehensive suite of automated tests (unit, integration, and UI tests) to catch regressions and ensure application stability with future changes.
4.  **CI/CD Pipeline:** Set up a Continuous Integration/Continuous Deployment (CI/CD) pipeline to automate the build, test, and deployment processes, ensuring faster and more reliable releases.
5.  **Resource Optimization:** Review and optimize image assets and other resources to reduce application size and improve loading times.
6.  **Localization:** Ensure all strings are properly externalized and consider adding support for more languages if the app is intended for a wider audience.

## Setup Instructions (English)

To set up the development environment for the Hajj App:

### 1. Clone the Repository
```bash
git clone https://github.com/gaming-clan/hajj-app.git
```

### 2. Java Setup
- Install OpenJDK 17:
  ```bash
  sudo apt update && sudo apt install -y openjdk-17-jdk
  ```
- Set JAVA_HOME environment variable:
  ```bash
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
  export PATH=$PATH:$JAVA_HOME/bin
  ```

### 3. Android SDK Setup
- Create Android SDK directory:
  ```bash
  mkdir -p ~/android-sdk/cmdline-tools
  ```
- Download and install Android command-line tools:
  ```bash
  cd ~/android-sdk/cmdline-tools
  wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
  unzip commandlinetools-linux-11076708_latest.zip
  mv cmdline-tools latest
  ```
- Set Android SDK environment variables:
  ```bash
  export ANDROID_HOME=~/android-sdk
  export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
  ```
- Install required Android SDK components:
  ```bash
  yes | ~/android-sdk/cmdline-tools/latest/bin/sdkmanager --install "platforms;android-34" "build-tools;34.0.0"
  ```
- Create `local.properties` file in the `hajj-app` directory:
  ```bash
  echo "sdk.dir=/home/ubuntu/android-sdk" > /home/ubuntu/hajj-app/local.properties
  ```

### 4. Build and Run
- Navigate to the project directory:
  ```bash
  cd /home/ubuntu/hajj-app
  ```
- Clean and build the project:
  ```bash
  ./gradlew clean build
  ```
- Assemble the debug build:
  ```bash
  ./gradlew assembleDebug
  ```

## Udhëzime Për Konfigurimin (Shqip)

Për të konfiguruar mjedisin e zhvillimit për Aplikacionin Haxh:

### 1. Klononi Depon
```bash
git clone https://github.com/gaming-clan/hajj-app.git
```

### 2. Konfigurimi i Java
- Instaloni OpenJDK 17:
  ```bash
  sudo apt update && sudo apt install -y openjdk-17-jdk
  ```
- Vendosni variablin e mjedisit JAVA_HOME:
  ```bash
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
  export PATH=$PATH:$JAVA_HOME/bin
  ```

### 3. Konfigurimi i Android SDK
- Krijoni direktorinë e Android SDK:
  ```bash
  mkdir -p ~/android-sdk/cmdline-tools
  ```
- Shkarkoni dhe instaloni mjetet e linjës së komandës Android:
  ```bash
  cd ~/android-sdk/cmdline-tools
  wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
  unzip commandlinetools-linux-11076708_latest.zip
  mv cmdline-tools latest
  ```
- Vendosni variablat e mjedisit Android SDK:
  ```bash
  export ANDROID_HOME=~/android-sdk
  export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
  ```
- Instaloni komponentët e nevojshëm të Android SDK:
  ```bash
  yes | ~/android-sdk/cmdline-tools/latest/bin/sdkmanager --install "platforms;android-34" "build-tools;34.0.0"
  ```
- Krijoni skedarin `local.properties` në direktorinë `hajj-app`:
  ```bash
  echo "sdk.dir=/home/ubuntu/android-sdk" > /home/ubuntu/hajj-app/local.properties
  ```

### 4. Ndërtimi dhe Ekzekutimi
- Navigoni te direktoria e projektit:
  ```bash
  cd /home/ubuntu/hajj-app
  ```
- Pastroni dhe ndërtoni projektin:
  ```bash
  ./gradlew clean build
  ```
- Ndërtoni versionin debug:
  ```bash
  ./gradlew assembleDebug
  ```
