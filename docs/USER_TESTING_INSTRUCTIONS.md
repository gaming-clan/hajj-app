# User Testing Instructions

This document provides instructions for users to test the Hajj App, focusing on verifying the newly integrated OpenStreetMap (OSM) functionality and the overall application build.

## Prerequisites

*   **Android Studio:** Ensure you have Android Studio installed on your machine.
*   **Git:** Ensure Git is installed and configured.
*   **Cloned Repository:** You should have the `hajj-app` repository cloned to your local machine.

## Steps to Test

1.  **Pull Latest Changes:**
    Open your terminal or Git Bash, navigate to the root directory of your `hajj-app` project, and pull the latest changes from the GitHub repository:
    ```bash
    git pull origin main
    ```

2.  **Open Project in Android Studio:**
    *   Close any open projects in Android Studio (File > Close Project).
    *   From the Android Studio welcome screen, select **"Open an existing Android Studio project"** or **"Open"**.
    *   Navigate to the **root directory of the `hajj-app` folder** (the one containing `build.gradle`, `settings.gradle`, `hajj_app`, `docs`, etc.).
    *   Select the `hajj-app` folder and click **"Open"**.

3.  **Gradle Sync:**
    Android Studio should automatically start a Gradle sync. Wait for it to complete. If it doesn't start automatically, look for the "Sync Project with Gradle Files" button (usually an elephant with a downward arrow or a refresh icon) in the toolbar and click it.

4.  **Configure Gradle Run Task (if necessary):**
    If you encounter issues running the app, ensure your Gradle Run Configuration is set up correctly:
    *   Go to **Run > Edit Configurations...**.
    *   Select your Gradle configuration (e.g., "app").
    *   In the **Tasks:** field, ensure it says `:app:assembleDebug`.
    *   Click **Apply** and **OK**.

5.  **Build and Run the Application:**
    *   Connect an Android device or start an emulator.
    *   Click the green **"Run app"** button (usually a green triangle) in the Android Studio toolbar.

## Verification Steps

Once the application is running on your device/emulator, perform the following checks:

1.  **Overall Application Functionality:**
    *   Navigate through the existing sections of the app (Home, Categories, Search, About) to ensure they are working as expected.

2.  **OpenStreetMap (OSM) Integration:**
    *   Look for a new navigation item (e.g., "Map" or "Harta") in the bottom navigation bar.
    *   Tap on the "Map" item to open the map view.
    *   Verify that the map loads correctly and displays the area around the Kaaba (Mecca).
    *   Test basic map interactions: zooming in/out, panning, and multi-touch controls.

3.  **Documentation:**
    *   Check the `docs` folder in the project structure for `README.md` (English) and `README_sq.md` (Albanian) files.
    *   Open these files and verify that the content is complete and includes the new images and information about OSM integration.

Please report any issues or unexpected behavior you encounter during testing.

