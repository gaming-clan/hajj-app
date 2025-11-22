package com.muslim.hajjrules.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Data encryption manager for sensitive Islamic content
 * Uses Android Keystore and AES encryption for data protection
 */
public class DataEncryptionManager {
    private static final String TAG = "DataEncryptionManager";
    private static final String KEY_ALIAS = "hajj_app_master_key";
    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12; // GCM recommended IV length
    private static final int KEY_SIZE = 256;

    private final Context context;
    private final SharedPreferences preferences;

    public DataEncryptionManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences("encryption_prefs", Context.MODE_PRIVATE);
    }

    /**
     * Initialize encryption keys
     */
    public void initialize() {
        try {
            generateOrRetrieveKey();
            Log.d(TAG, "Encryption manager initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize encryption manager", e);
        }
    }

    /**
     * Encrypt sensitive data
     */
    public String encrypt(String plaintext) throws EncryptionException {
        try {
            if (plaintext == null) {
                return null;
            }

            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // Initialize cipher
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            // Encrypt data
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine IV and ciphertext
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(iv);
            outputStream.write(ciphertext);

            return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "Encryption failed", e);
            throw new EncryptionException("Failed to encrypt data", e);
        }
    }

    /**
     * Decrypt sensitive data
     */
    public String decrypt(String encryptedText) throws EncryptionException {
        try {
            if (encryptedText == null) {
                return null;
            }

            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // Decode and split IV and ciphertext
            byte[] combinedData = Base64.decode(encryptedText, Base64.NO_WRAP);

            if (combinedData.length < IV_LENGTH) {
                throw new EncryptionException("Invalid encrypted data");
            }

            byte[] iv = new byte[IV_LENGTH];
            byte[] ciphertext = new byte[combinedData.length - IV_LENGTH];

            System.arraycopy(combinedData, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combinedData, IV_LENGTH, ciphertext, 0, ciphertext.length);

            // Initialize cipher
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            // Decrypt data
            byte[] plaintext = cipher.doFinal(ciphertext);

            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Decryption failed", e);
            throw new EncryptionException("Failed to decrypt data", e);
        }
    }

    /**
     * Encrypt file content
     */
    public void encryptFile(File inputFile, File outputFile) throws EncryptionException {
        try {
            if (!inputFile.exists()) {
                throw new EncryptionException("Input file does not exist");
            }

            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // Initialize cipher
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            // Write IV and encrypted data
            try (FileOutputStream fileOut = new FileOutputStream(outputFile);
                 CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {

                // Write IV first
                fileOut.write(iv);

                // Write encrypted file content
                try (FileInputStream fileIn = new FileInputStream(inputFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fileIn.read(buffer)) != -1) {
                        cipherOut.write(buffer, 0, bytesRead);
                    }
                }
            }

            Log.d(TAG, "File encrypted successfully: " + outputFile.getName());
        } catch (Exception e) {
            Log.e(TAG, "File encryption failed", e);
            throw new EncryptionException("Failed to encrypt file", e);
        }
    }

    /**
     * Decrypt file content
     */
    public void decryptFile(File inputFile, File outputFile) throws EncryptionException {
        try {
            if (!inputFile.exists()) {
                throw new EncryptionException("Input file does not exist");
            }

            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            try (FileInputStream fileIn = new FileInputStream(inputFile);
                 FileOutputStream fileOut = new FileOutputStream(outputFile)) {

                // Read IV
                byte[] iv = new byte[IV_LENGTH];
                int ivRead = fileIn.read(iv);
                if (ivRead != IV_LENGTH) {
                    throw new EncryptionException("Invalid encrypted file format");
                }

                // Initialize cipher
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

                // Decrypt and write content
                try (CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = cipherIn.read(buffer)) != -1) {
                        fileOut.write(buffer, 0, bytesRead);
                    }
                }
            }

            Log.d(TAG, "File decrypted successfully: " + outputFile.getName());
        } catch (Exception e) {
            Log.e(TAG, "File decryption failed", e);
            throw new EncryptionException("Failed to decrypt file", e);
        }
    }

    /**
     * Generate or retrieve encryption key from Android Keystore
     */
    private void generateOrRetrieveKey() throws EncryptionException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                generateKey();
            } else {
                Log.d(TAG, "Encryption key already exists in Keystore");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to generate or retrieve key", e);
            throw new EncryptionException("Key generation failed", e);
        }
    }

    /**
     * Generate new encryption key
     */
    private void generateKey() throws EncryptionException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            );

            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .setUserAuthenticationRequired(false)
            .build();

            keyGenerator.init(spec);
            keyGenerator.generateKey();

            Log.d(TAG, "New encryption key generated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to generate key", e);
            throw new EncryptionException("Key generation failed", e);
        }
    }

    /**
     * Get secret key from Android Keystore
     */
    private SecretKey getSecretKey() throws EncryptionException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);

            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            if (secretKeyEntry == null) {
                throw new EncryptionException("Encryption key not found");
            }

            return secretKeyEntry.getSecretKey();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get secret key", e);
            throw new EncryptionException("Failed to access encryption key", e);
        }
    }

    /**
     * Generate secure random string
     */
    public String generateSecureToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[length];
        random.nextBytes(tokenBytes);
        return Base64.encodeToString(tokenBytes, Base64.NO_WRAP).substring(0, length);
    }

    /**
     * Check if encryption is available
     */
    public boolean isEncryptionAvailable() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);
            return keyStore.containsAlias(KEY_ALIAS);
        } catch (Exception e) {
            Log.e(TAG, "Failed to check encryption availability", e);
            return false;
        }
    }

    /**
     * Get encryption information
     */
    public String getEncryptionInfo() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);

            boolean keyExists = keyStore.containsAlias(KEY_ALIAS);

            return "Encryption Status:\n" +
                   "Key Available: " + keyExists + "\n" +
                   "Key Alias: " + KEY_ALIAS + "\n" +
                   "Transformation: " + TRANSFORMATION + "\n" +
                   "Key Size: " + KEY_SIZE + " bits\n";
        } catch (Exception e) {
            return "Encryption status unavailable: " + e.getMessage();
        }
    }

    /**
     * Custom exception for encryption errors
     */
    public static class EncryptionException extends Exception {
        public EncryptionException(String message) {
            super(message);
        }

        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}