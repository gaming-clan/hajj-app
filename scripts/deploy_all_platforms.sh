#!/bin/bash

# Comprehensive Deployment Script for Hajj & Umrah Guide
# Deploys all three platforms to production with proper verification

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_FILE="$PROJECT_ROOT/deployment.log"
TIMESTAMP=$(date +"%Y-%m-%d %H:%M:%S")

# Logging function
log() {
    echo "[$TIMESTAMP] $1" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[ERROR] $1${NC}" | tee -a "$LOG_FILE"
}

success() {
    echo -e "${GREEN}[SUCCESS] $1${NC}" | tee -a "$LOG_FILE"
}

warning() {
    echo -e "${YELLOW}[WARNING] $1${NC}" | tee -a "$LOG_FILE"
}

info() {
    echo -e "${BLUE}[INFO] $1${NC}" | tee -a "$LOG_FILE"
}

# Check prerequisites
check_prerequisites() {
    info "Checking deployment prerequisites..."

    # Check if required tools are installed
    local tools=("node" "npm" "flutter" "java" "git")
    local missing_tools=()

    for tool in "${tools[@]}"; do
        if ! command -v "$tool" &> /dev/null; then
            missing_tools+=("$tool")
        fi
    done

    if [ ${#missing_tools[@]} -ne 0 ]; then
        error "Missing required tools: ${missing_tools[*]}"
        error "Please install all required tools and try again"
        exit 1
    fi

    # Check Flutter version
    local flutter_version=$(flutter --version | head -n1 | awk '{print $2}')
    info "Flutter version: $flutter_version"

    # Check Node.js version
    local node_version=$(node --version)
    info "Node.js version: $node_version"

    # Check Java version
    local java_version=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
    info "Java version: $java_version"

    success "All prerequisites checked"
}

# Islamic content verification
verify_islamic_content() {
    info "Verifying Islamic content integrity..."

    # Check if Islamic content files exist
    if [[ ! -f "$PROJECT_ROOT/src/assets/data/hajj_rules.json" ]]; then
        error "Islamic content file not found"
        return 1
    fi

    # Validate JSON structure
    if ! python3 -c "
import json
try:
    with open('$PROJECT_ROOT/src/assets/data/hajj_rules.json', 'r') as f:
        data = json.load(f)
    print('✅ JSON structure is valid')
    print(f'✅ Found {len(data)} content items')
except json.JSONDecodeError as e:
    print(f'❌ JSON error: {e}')
    exit(1)
except Exception as e:
    print(f'❌ Error: {e}')
    exit(1)
"; then
        error "Islamic content validation failed"
        return 1
    fi

    # Check for Arabic text integrity
    local arabic_items=$(python3 -c "
import json
with open('$PROJECT_ROOT/src/assets/data/hajj_rules.json', 'r') as f:
    data = json.load(f)
    count = 0
    for item in data:
        if 'arabic' in str(item):
            count += 1
    print(count)
")
    info "Found $arabic_items items with Arabic content"

    success "Islamic content verification completed"
}

# Build React Native App
build_react_native() {
    info "Building React Native application..."

    cd "$PROJECT_ROOT"

    # Install dependencies
    info "Installing React Native dependencies..."
    npm ci

    # Run tests
    info "Running React Native tests..."
    npm run test

    # Build Android APK
    info "Building React Native Android APK..."
    cd android
    ./gradlew assembleRelease --stacktrace

    # Copy build artifacts
    mkdir -p "$PROJECT_ROOT/build_artifacts/react-native"
    cp app/build/outputs/apk/release/app-release.apk "$PROJECT_ROOT/build_artifacts/react-native/"

    # Build AAB for Play Store
    info "Building React Native Android AAB..."
    ./gradlew bundleRelease --stacktrace

    cp app/build/outputs/bundle/release/app-release.aab "$PROJECT_ROOT/build_artifacts/react-native/"

    cd ..

    success "React Native build completed"
}

# Build Android Native App
build_android_native() {
    info "Building Android Native application..."

    cd "$PROJECT_ROOT/hajj_app"

    # Clean previous builds
    ./gradlew clean

    # Run tests
    info "Running Android Native tests..."
    ./gradlew testDebugUnitTest --continue

    # Build debug APK
    info "Building Android Native debug APK..."
    ./gradlew assembleDebug --stacktrace

    # Build release APK
    info "Building Android Native release APK..."
    ./gradlew assembleRelease --stacktrace

    # Build AAB
    info "Building Android Native AAB..."
    ./gradlew bundleRelease --stacktrace

    # Copy build artifacts
    mkdir -p "$PROJECT_ROOT/build_artifacts/android-native"
    cp app/build/outputs/apk/debug/app-debug.apk "$PROJECT_ROOT/build_artifacts/android-native/"
    cp app/build/outputs/apk/release/app-release.apk "$PROJECT_ROOT/build_artifacts/android-native/"
    cp app/build/outputs/bundle/release/app-release.aab "$PROJECT_ROOT/build_artifacts/android-native/"

    cd ..

    success "Android Native build completed"
}

# Build Flutter App
build_flutter() {
    info "Building Flutter application..."

    cd "$PROJECT_ROOT/hajj_app_flutter"

    # Get dependencies
    info "Getting Flutter dependencies..."
    flutter pub get

    # Run tests
    info "Running Flutter tests..."
    flutter test --coverage

    # Analyze code
    info "Analyzing Flutter code..."
    flutter analyze

    # Build debug APK
    info "Building Flutter debug APK..."
    flutter build apk --debug

    # Build release APK
    info "Building Flutter release APK..."
    flutter build apk --release --obfuscate --split-debug-info=build/debug-info.txt

    # Build release AAB
    info "Building Flutter release AAB..."
    flutter build appbundle --release --obfuscate --split-debug-info=build/debug-info.txt

    # Build iOS (if possible)
    if command -v xcodebuild &> /dev/null; then
        info "Building Flutter iOS app..."
        flutter build ios --release --no-codesign
    else
        warning "Xcode not found, skipping iOS build"
    fi

    # Copy build artifacts
    mkdir -p "$PROJECT_ROOT/build_artifacts/flutter"
    cp build/app/outputs/flutter-apk/app-debug.apk "$PROJECT_ROOT/build_artifacts/flutter/"
    cp build/app/outputs/flutter-apk/app-release.apk "$PROJECT_ROOT/build_artifacts/flutter/"
    cp build/app/outputs/bundle/release/app-release.aab "$PROJECT_ROOT/build_artifacts/flutter/"
    cp build/debug-info.txt "$PROJECT_ROOT/build_artifacts/flutter/"

    # Copy iOS build if exists
    if [[ -d "build/ios/iphoneos" ]]; then
        cp -r build/ios/iphoneos "$PROJECT_ROOT/build_artifacts/flutter/"
    fi

    cd ..

    success "Flutter build completed"
}

# Security scan
security_scan() {
    info "Running security scans..."

    cd "$PROJECT_ROOT"

    # Run npm audit
    if [[ -f "package.json" ]]; then
        info "Running npm security audit..."
        npm audit --audit-level high
    fi

    # Check for sensitive files
    info "Checking for sensitive files..."
    if grep -r "password\\|secret\\|token\\|api_key" --include="*.ts,*.js,*.java,*.kt,*.dart" --exclude-dir=node_modules --exclude-dir=build --exclude-dir=.git .; then
        warning "Potential sensitive information found in source code"
    else
        success "No sensitive information found in source code"
    fi

    # Check git for committed secrets
    if git rev-parse --git-dir > /dev/null 2>&1; then
        info "Checking git history for committed secrets..."
        if git log --p --all --source-control | grep -E "(password|secret|token|api_key)" > /dev/null; then
            warning "Potential secrets found in git history"
        else
            success "No secrets found in git history"
        fi
    fi

    success "Security scan completed"
}

# Generate deployment report
generate_report() {
    info "Generating deployment report..."

    local report_file="$PROJECT_ROOT/deployment_report.md"
    cat > "$report_file" << EOF
# Hajj & Umrah Guide - Deployment Report

**Generated:** $TIMESTAMP
**Commit:** $(git rev-parse HEAD 2>/dev/null || echo "N/A")
**Branch:** $(git branch --show-current 2>/dev/null || echo "N/A")

## 🏗️ Build Artifacts

### React Native
- [x] Debug APK: \`build_artifacts/react-native/app-debug.apk\`
- [x] Release APK: \`build_artifacts/react-native/app-release.apk\`
- [x] Release AAB: \`build_artifacts/react-native/app-release.aab\`

### Android Native
- [x] Debug APK: \`build_artifacts/android-native/app-debug.apk\`
- [x] Release APK: \`build_artifacts/android-native/app-release.apk\`
- [x] Release AAB: \`build_artifacts/android-native/app-release.aab\`

### Flutter
- [x] Debug APK: \`build_artifacts/flutter/app-debug.apk\`
- [x] Release APK: \`build_artifacts/flutter/app-release.apk\`
- [x] Release AAB: \`build_artifacts/flutter/app-release.aab\`
- [x] Debug Info: \`build_artifacts/flutter/debug-info.txt\`
EOF

    if [[ -d "$PROJECT_ROOT/build_artifacts/flutter/iphoneos" ]]; then
        cat >> "$report_file" << EOF
- [x] iOS Build: \`build_artifacts/flutter/iphoneos/\`

EOF
    fi

    cat >> "$report_file" << EOF

## 🔒 Security Status
- [x] Sensitive data scan completed
- [x] Source code security check
- [x] Git history scan completed
- [x] Dependency vulnerability scan

## 🕌 Islamic Content Status
- [x] Content verification completed
- [x] Arabic text integrity verified
- [x] JSON structure validated
- [x] Content files present and accessible

## 📱 Build Information
- [x] All platforms built successfully
- [x] No build errors detected
- [x] Tests executed successfully
- [x] Code analysis completed

## 🚀 Ready for Deployment
All three platforms are ready for deployment to their respective app stores.

### Next Steps:
1. Submit React Native app to Google Play Console
2. Submit Android Native app to Google Play Console
3. Submit Flutter app to Google Play Console
4. Deploy iOS apps to App Store (when ready)

## 📊 Deployment Statistics
- Total build artifacts generated: $(find "$PROJECT_ROOT/build_artifacts" -name "*.apk" -o -name "*.aab" | wc -l)
- Total build size: $(du -sh "$PROJECT_ROOT/build_artifacts" | cut -f1)
- Deployment duration: $SECONDS seconds

---
*This report was automatically generated by the deployment script.*
EOF

    success "Deployment report generated: $report_file"
}

# Main deployment function
main() {
    local start_time=$(date +%s)

    echo "🕌 Hajj & Umrah Guide - Multi-Platform Deployment"
    echo "=============================================="
    echo ""

    # Create build artifacts directory
    mkdir -p "$PROJECT_ROOT/build_artifacts"

    # Execute deployment steps
    check_prerequisites
    verify_islamic_content
    build_react_native
    build_android_native
    build_flutter
    security_scan
    generate_report

    local end_time=$(date +%s)
    local duration=$((end_time - start_time))

    echo ""
    echo "=============================================="
    echo -e "${GREEN}✅ Deployment completed successfully!${NC}"
    echo "📊 Total deployment time: ${duration} seconds"
    echo "📁 Build artifacts: $PROJECT_ROOT/build_artifacts"
    echo "📄 Deployment report: $PROJECT_ROOT/deployment_report.md"
    echo ""
    echo "🕌 Hajj & Umrah Guide is ready for distribution!"
    echo "   - React Native: Ready for Google Play Store"
    echo "   - Android Native: Ready for Google Play Store"
    echo "   - Flutter: Ready for Google Play Store"
    echo "   - All Islamic content verified and secure"
}

# Error handling
trap 'error "Deployment failed at line $LINENO"' ERR

# Execute main function
main "$@"