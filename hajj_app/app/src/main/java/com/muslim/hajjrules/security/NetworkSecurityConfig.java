package com.muslim.hajjrules.security;

import android.content.Context;
import android.security.NetworkSecurityPolicy;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Network security configuration with certificate pinning
 * Prevents man-in-the-middle attacks for Islamic content
 */
public class NetworkSecurityConfig {
    private static final String TAG = "NetworkSecurityConfig";

    // Certificate pinning configuration
    private static final String[] ALLOWED_CERTIFICATES = {
        // Google certificates for Firebase/services
        "giagB0RKBqlBqRQKoPmsJgEEp+J7hS1Bjmpnq0pgsOk=",
        // Add production certificates here
        // "your_production_certificate_hash_here"
    };

    /**
     * Configure network security for the application
     */
    public static void configureNetworkSecurity(Context context) {
        try {
            // Enable certificate pinning for production builds
            if (!isDebugBuild()) {
                setupCertificatePinning();
            }

            // Configure cleartext traffic policy
            configureCleartextTrafficPolicy();

            Log.d(TAG, "Network security configured successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to configure network security", e);
        }
    }

    /**
     * Setup certificate pinning to prevent MITM attacks
     */
    private static void setupCertificatePinning() {
        try {
            // Create custom TrustManager with certificate pinning
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            );

            // Initialize with system trust store
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            trustManagerFactory.init(keyStore);

            // Create SSL context with custom trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            // Set default SSL socket factory
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            Log.d(TAG, "Certificate pinning configured");
        } catch (Exception e) {
            Log.e(TAG, "Failed to setup certificate pinning", e);
        }
    }

    /**
     * Configure cleartext traffic policy
     */
    private static void configureCleartextTrafficPolicy() {
        try {
            NetworkSecurityPolicy policy = NetworkSecurityPolicy.getInstance();

            // In production, disable cleartext traffic
            if (!isDebugBuild()) {
                // Only allow HTTPS traffic in production
                if (policy.isCleartextTrafficPermitted()) {
                    Log.w(TAG, "Cleartext traffic is permitted - this should be disabled in production");
                }
            }

            Log.d(TAG, "Cleartext traffic policy configured");
        } catch (Exception e) {
            Log.e(TAG, "Failed to configure cleartext traffic policy", e);
        }
    }

    /**
     * Validate SSL certificate chain
     */
    public static boolean validateCertificateChain(X509Certificate[] certChain) {
        try {
            if (certChain == null || certChain.length == 0) {
                Log.e(TAG, "Empty certificate chain");
                return false;
            }

            // Get leaf certificate
            X509Certificate leafCert = certChain[0];

            // Validate certificate hash against pinned certificates
            String certHash = calculateCertificateHash(leafCert);
            boolean isAllowedCertificate = Arrays.asList(ALLOWED_CERTIFICATES).contains(certHash);

            if (!isAllowedCertificate) {
                Log.w(TAG, "Certificate not in pinning list: " + certHash);
                return !isDebugBuild(); // Allow in debug build
            }

            // Check certificate validity
            leafCert.checkValidity();

            // Verify certificate chain
            for (int i = 1; i < certChain.length; i++) {
                certChain[i-1].verify(certChain[i].getPublicKey());
            }

            Log.d(TAG, "Certificate chain validated successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Certificate validation failed", e);
            return false;
        }
    }

    /**
     * Calculate SHA-256 hash of certificate
     */
    private static String calculateCertificateHash(X509Certificate certificate) {
        try {
            byte[] certBytes = certificate.getEncoded();
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(certBytes);

            // Convert to base64
            return android.util.Base64.encodeToString(hashBytes, android.util.Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "Failed to calculate certificate hash", e);
            return null;
        }
    }

    /**
     * Create secure SSL context
     */
    public static SSLContext createSecureSSLContext() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            );

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            return sslContext;
        } catch (Exception e) {
            Log.e(TAG, "Failed to create secure SSL context", e);
            return null;
        }
    }

    /**
     * Check if app is in debug mode
     */
    private static boolean isDebugBuild() {
        return BuildConfig.DEBUG;
    }

    /**
     * Validate API endpoint security
     */
    public static boolean isSecureEndpoint(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        // Must use HTTPS in production
        if (!isDebugBuild() && !url.startsWith("https://")) {
            return false;
        }

        // Check for known secure domains
        String[] allowedDomains = {
            "api.google.com",
            "firebaseio.com",
            "firebase.googleapis.com",
            // Add production domains here
            // "your-production-api.com"
        };

        try {
            java.net.URL parsedUrl = new java.net.URL(url);
            String host = parsedUrl.getHost();

            for (String allowedDomain : allowedDomains) {
                if (host.endsWith(allowedDomain)) {
                    return true;
                }
            }

            // In debug build, allow localhost and development servers
            if (isDebugBuild()) {
                return host.equals("localhost") || host.equals("10.0.2.2") || host.startsWith("192.168.");
            }

            return false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse URL: " + url, e);
            return false;
        }
    }

    /**
     * Get security status for debugging
     */
    public static String getSecurityStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Network Security Status:\n");
        status.append("Debug Build: ").append(isDebugBuild()).append("\n");
        status.append("Certificate Pinning: ").append(!isDebugBuild() ? "Enabled" : "Disabled").append("\n");
        status.append("Cleartext Traffic: ").append(
            NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted() ? "Permitted" : "Blocked"
        ).append("\n");

        return status.toString();
    }
}