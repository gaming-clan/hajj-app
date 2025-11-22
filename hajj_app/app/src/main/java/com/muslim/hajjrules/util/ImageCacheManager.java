package com.muslim.hajjrules.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Memory and disk cache manager for Islamic images
 * Implements two-level caching for optimal performance
 */
public class ImageCacheManager {
    private static final String TAG = "ImageCacheManager";
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final int MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8); // 1/8 of available memory

    private static ImageCacheManager instance;
    private final Context context;
    private final LruCache<String, Bitmap> memoryCache;
    private final File diskCacheDir;
    private final ExecutorService executorService;

    private ImageCacheManager(Context context) {
        this.context = context.getApplicationContext();
        this.memoryCache = new LruCache<String, Bitmap>(MEMORY_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // Return size in KB
            }
        };
        this.diskCacheDir = new File(context.getCacheDir(), "image_cache");
        this.executorService = Executors.newFixedThreadPool(2);

        // Create disk cache directory
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
    }

    public static synchronized ImageCacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new ImageCacheManager(context);
        }
        return instance;
    }

    /**
     * Interface for image loading callbacks
     */
    public interface ImageLoadCallback {
        void onImageLoaded(Bitmap bitmap);
        void onImageLoadError(String errorMessage);
    }

    /**
     * Load image with caching
     * @param imageUrl Image URL or asset path
     * @param callback Loading callback
     */
    public void loadImage(String imageUrl, ImageLoadCallback callback) {
        // Check memory cache first
        Bitmap cachedBitmap = getBitmapFromMemCache(imageUrl);
        if (cachedBitmap != null) {
            if (callback != null) {
                callback.onImageLoaded(cachedBitmap);
            }
            return;
        }

        // Check disk cache
        executorService.execute(() -> {
            try {
                Bitmap diskCachedBitmap = getBitmapFromDiskCache(imageUrl);
                if (diskCachedBitmap != null) {
                    // Add to memory cache
                    addBitmapToMemoryCache(imageUrl, diskCachedBitmap);

                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> {
                        if (callback != null) {
                            callback.onImageLoaded(diskCachedBitmap);
                        }
                    });
                    return;
                }

                // Load from network or assets
                Bitmap bitmap = loadImageFromSource(imageUrl);
                if (bitmap != null) {
                    // Cache the bitmap
                    addBitmapToMemoryCache(imageUrl, bitmap);
                    saveBitmapToDiskCache(imageUrl, bitmap);

                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> {
                        if (callback != null) {
                            callback.onImageLoaded(bitmap);
                        }
                    });
                } else {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> {
                        if (callback != null) {
                            callback.onImageLoadError("Failed to load image");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading image: " + imageUrl, e);

                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onImageLoadError(e.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Load image from source (network or assets)
     */
    private Bitmap loadImageFromSource(String imageUrl) throws IOException {
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            // Load from network
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        } else if (imageUrl.startsWith("assets://")) {
            // Load from assets
            String assetPath = imageUrl.substring(9); // Remove "assets://" prefix
            InputStream inputStream = context.getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        }
        return null;
    }

    /**
     * Add bitmap to memory cache
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    /**
     * Get bitmap from memory cache
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    /**
     * Save bitmap to disk cache
     */
    private void saveBitmapToDiskCache(String key, Bitmap bitmap) throws IOException {
        String hashedKey = hashKey(key);
        File cacheFile = new File(diskCacheDir, hashedKey);

        try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
    }

    /**
     * Get bitmap from disk cache
     */
    private Bitmap getBitmapFromDiskCache(String key) {
        String hashedKey = hashKey(key);
        File cacheFile = new File(diskCacheDir, hashedKey);

        if (cacheFile.exists()) {
            return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        }
        return null;
    }

    /**
     * Create hash key for cache file names
     */
    private String hashKey(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(key.getBytes());
            byte[] hash = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple key if hashing fails
            return String.valueOf(key.hashCode());
        }
    }

    /**
     * Clear all caches
     */
    public void clearCache() {
        // Clear memory cache
        memoryCache.evictAll();

        // Clear disk cache
        executorService.execute(() -> {
            File[] files = diskCacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        });
    }

    /**
     * Get cache statistics
     */
    public String getCacheStats() {
        return "Memory Cache: " + memoryCache.size() + "/" + memoryCache.maxSize() + " KB";
    }

    /**
     * Shutdown the executor service
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}