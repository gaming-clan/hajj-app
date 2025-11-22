package com.muslim.hajjrules.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Asynchronous JSON loader for Islamic content
 * Prevents main thread blocking when loading large Hajj data files
 */
public class AsyncJsonLoader {
    private static final String TAG = "AsyncJsonLoader";
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Interface for JSON loading callbacks
     */
    public interface JsonLoadCallback {
        void onLoadComplete(JSONObject jsonObject);
        void onLoadError(String errorMessage);
    }

    /**
     * Load JSON file asynchronously from assets
     * @param context Application context
     * @param fileName JSON file name in assets folder
     * @param callback Callback for loading result
     */
    public static void loadJsonFromAssets(Context context, String fileName, JsonLoadCallback callback) {
        executorService.execute(() -> {
            try {
                JSONObject jsonObject = loadJsonFromAssetsSync(context, fileName);

                // Post result to main thread
                if (callback != null) {
                    // Use main thread handler
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onLoadComplete(jsonObject));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading JSON file: " + fileName, e);

                if (callback != null) {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onLoadError(e.getMessage()));
                }
            }
        });
    }

    /**
     * Synchronous JSON loading (must be called from background thread)
     * @param context Application context
     * @param fileName JSON file name
     * @return Loaded JSONObject
     * @throws IOException If file cannot be read
     * @throws JSONException If JSON cannot be parsed
     */
    private static JSONObject loadJsonFromAssetsSync(Context context, String fileName) throws IOException, JSONException {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = context.getAssets().open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return new JSONObject(stringBuilder.toString());
    }

    /**
     * Load multiple JSON files in parallel
     * @param context Application context
     * @param fileNames Array of JSON file names
     * @param callback Callback for batch loading results
     */
    public static void loadMultipleJsonFiles(Context context, String[] fileNames, MultipleJsonLoadCallback callback) {
        if (fileNames == null || fileNames.length == 0) {
            if (callback != null) {
                callback.onAllFilesLoaded(new JSONObject[0]);
            }
            return;
        }

        final JSONObject[] results = new JSONObject[fileNames.length];
        final boolean[] completed = new boolean[fileNames.length];
        final String[] errors = new String[fileNames.length];
        final int[] completedCount = {0};

        for (int i = 0; i < fileNames.length; i++) {
            final int index = i;
            final String fileName = fileNames[i];

            executorService.execute(() -> {
                try {
                    JSONObject jsonObject = loadJsonFromAssetsSync(context, fileName);
                    results[index] = jsonObject;
                    completed[index] = true;
                } catch (Exception e) {
                    Log.e(TAG, "Error loading JSON file: " + fileName, e);
                    errors[index] = e.getMessage();
                    completed[index] = true;
                }

                synchronized (completedCount) {
                    completedCount[0]++;

                    // Check if all files are loaded
                    if (completedCount[0] == fileNames.length) {
                        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mainHandler.post(() -> {
                            if (callback != null) {
                                callback.onAllFilesLoaded(results);

                                // Check if there were any errors
                                boolean hasErrors = false;
                                for (String error : errors) {
                                    if (error != null) {
                                        hasErrors = true;
                                        break;
                                    }
                                }

                                if (hasErrors) {
                                    callback.onErrors(errors);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Interface for multiple JSON file loading callbacks
     */
    public interface MultipleJsonLoadCallback {
        void onAllFilesLoaded(JSONObject[] jsonObjects);
        void onErrors(String[] errors);
    }

    /**
     * Shutdown the executor service
     * Call this when the app is destroyed
     */
    public static void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}