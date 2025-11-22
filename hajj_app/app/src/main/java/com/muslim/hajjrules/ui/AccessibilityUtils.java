package com.muslim.hajjrules.ui;

import android.content.Context;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.AccessibilityManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * Utility class for accessibility features and RTL support
 * Provides tools for making Islamic content accessible to all users
 */
public class AccessibilityUtils {

    /**
     * Setup accessibility for Islamic content views
     */
    public static void setupAccessibilityForView(View view, String contentDescription, String hint) {
        if (view == null) return;

        view.setContentDescription(contentDescription);

        if (hint != null && hint.trim().length() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                view.setAccessibilityPaneTitle(hint);
            }
        }

        // Set appropriate accessibility roles
        if (view instanceof TextView) {
            setupTextViewAccessibility((TextView) view, contentDescription, hint);
        }
    }

    /**
     * Setup accessibility for TextView with Arabic content
     */
    public static void setupTextViewAccessibility(TextView textView, String contentDescription, String hint) {
        if (textView == null) return;

        // Set content description for screen readers
        if (contentDescription != null) {
            textView.setContentDescription(contentDescription);
        }

        // Announce text changes for dynamic content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        }

        // Handle RTL text direction
        if (isArabicText(textView.getText().toString())) {
            textView.setTextDirection(View.TEXT_DIRECTION_RTL);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        // Enable accessibility for links
        Linkify.addLinks(textView, Linkify.ALL);
    }

    /**
     * Setup accessibility for Quranic verses
     */
    public static void setupQuranicVerseAccessibility(TextView textView, String verseText, String translation) {
        if (textView == null) return;

        String description = "Quranic verse: " + verseText;
        if (translation != null && !translation.trim().isEmpty()) {
            description += ". Translation: " + translation;
        }

        textView.setContentDescription(description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        }

        // Set Arabic text direction
        if (isArabicText(verseText)) {
            textView.setTextDirection(View.TEXT_DIRECTION_RTL);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        // Mark as important for accessibility
        textView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    /**
     * Setup accessibility for Hadith text
     */
    public static void setupHadithAccessibility(TextView textView, String hadithText, String narrator) {
        if (textView == null) return;

        String description = "Hadith";
        if (narrator != null && !narrator.trim().isEmpty()) {
            description += " narrated by " + narrator + ": ";
        } else {
            description += ": ";
        }
        description += hadithText;

        textView.setContentDescription(description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        }

        // Set Arabic text direction
        if (isArabicText(hadithText)) {
            textView.setTextDirection(View.TEXT_DIRECTION_RTL);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        textView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    /**
     * Check if screen reader is enabled
     */
    public static boolean isScreenReaderEnabled(Context context) {
        if (context == null) return false;

        AccessibilityManager accessibilityManager = (AccessibilityManager)
            context.getSystemService(Context.ACCESSIBILITY_SERVICE);

        return accessibilityManager != null &&
               accessibilityManager.isEnabled() &&
               accessibilityManager.isTouchExplorationEnabled();
    }

    /**
     * Announce content for screen readers
     */
    public static void announceForAccessibility(View view, String message) {
        if (view == null || message == null || message.trim().isEmpty()) return;

        view.announceForAccessibility(message);

        // Also send accessibility event
        AccessibilityEvent event = AccessibilityEvent.obtain();
        event.setEventType(AccessibilityEvent.TYPE_ANNOUNCEMENT);
        event.getText().add(message);
        view.sendAccessibilityEventUnchecked(event);
    }

    /**
     * Setup accessibility for navigation elements
     */
    public static void setupNavigationAccessibility(View view, String label, String description) {
        if (view == null) return;

        view.setContentDescription(label + (description != null ? ": " + description : ""));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setAccessibilityHeading(false);
        }

        view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);

        // Make it clickable for accessibility
        view.setClickable(true);
        view.setFocusable(true);
    }

    /**
     * Setup accessibility for list items
     */
    public static void setupListItemAccessibility(View itemView, String title, String description, int position) {
        if (itemView == null) return;

        String contentDescription = "Item " + (position + 1) + ": " + title;
        if (description != null && !description.trim().isEmpty()) {
            contentDescription += ". " + description;
        }

        itemView.setContentDescription(contentDescription);
        itemView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            itemView.setAccessibilityCollectionItemInfo(
                new AccessibilityNodeInfo.CollectionItemInfo.Builder()
                    .setRowIndex(position)
                    .setColumnIndex(0)
                    .setRowSpan(1)
                    .setColumnSpan(1)
                    .setHeading(false)
                    .setSelected(false)
                    .build()
            );
        }
    }

    /**
     * Check if text contains Arabic characters
     */
    public static boolean isArabicText(String text) {
        if (text == null || text.trim().isEmpty()) return false;

        // Arabic Unicode range
        return text.matches(".*[\\u0600-\\u06FF].*");
    }

    /**
     * Format Arabic numbers
     */
    public static String formatArabicNumbers(String text) {
        if (text == null) return null;

        char[] arabicNumbers = {'٠','١','٢','٣','٤','٥','٦','٧','٨','٩'};
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= '0' && c <= '9') {
                result.append(arabicNumbers[c - '0']);
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Remove Arabic diacritics for accessibility
     */
    public static String removeArabicDiacritics(String text) {
        if (text == null) return null;

        // Remove Arabic diacritical marks
        return text.replaceAll("[\\u064B-\\u0652]", "");
    }

    /**
     * Get text direction for content
     */
    public static int getTextDirection(String text, Context context) {
        if (isArabicText(text) || isArabicLocale(context)) {
            return View.TEXT_DIRECTION_RTL;
        }
        return View.TEXT_DIRECTION_LTR;
    }

    /**
     * Get text alignment for content
     */
    public static int getTextAlignment(String text, Context context) {
        if (isArabicText(text) || isArabicLocale(context)) {
            return View.TEXT_ALIGNMENT_TEXT_START;
        }
        return View.TEXT_ALIGNMENT_TEXT_START;
    }

    /**
     * Check if device locale is Arabic
     */
    public static boolean isArabicLocale(Context context) {
        if (context == null) return false;

        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getLanguage().equals("ar") ||
               locale.getLanguage().equals("ar-SA") ||
               locale.getLanguage().equals("ar-EG");
    }

    /**
     * Setup high contrast mode support
     */
    public static void setupHighContrastSupport(View view) {
        if (view == null) return;

        // Check if high contrast mode is enabled
        if (isHighContrastModeEnabled(view.getContext())) {
            // Apply high contrast styles
            applyHighContrastStyles(view);
        }
    }

    /**
     * Check if high contrast mode is enabled
     */
    public static boolean isHighContrastModeEnabled(Context context) {
        if (context == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return false;
        }

        return context.getResources().getConfiguration().isScreenRound();
    }

    /**
     * Apply high contrast styles to view
     */
    private static void applyHighContrastStyles(View view) {
        // Implementation would depend on your app's theme system
        // This is a placeholder for high contrast styling
    }

    /**
     * Setup accessibility for religious content categories
     */
    public static void setupCategoryAccessibility(View view, String categoryName, String itemCount) {
        if (view == null) return;

        String description = categoryName + " category";
        if (itemCount != null && !itemCount.trim().isEmpty()) {
            description += " with " + itemCount + " items";
        }

        view.setContentDescription(description);
        view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);

        if (view instanceof ViewGroup) {
            setupAccessibilityForChildren((ViewGroup) view);
        }
    }

    /**
     * Recursively setup accessibility for child views
     */
    private static void setupAccessibilityForChildren(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);

            // Set appropriate importance for child views
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                if (textView.getText().length() > 0) {
                    child.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);

                    // Set text direction for Arabic content
                    if (isArabicText(textView.getText().toString())) {
                        textView.setTextDirection(View.TEXT_DIRECTION_RTL);
                    }
                }
            }

            if (child instanceof ViewGroup) {
                setupAccessibilityForChildren((ViewGroup) child);
            }
        }
    }

    /**
     * Get accessibility status information
     */
    public static String getAccessibilityStatus(Context context) {
        StringBuilder status = new StringBuilder();

        status.append("Accessibility Status:\n");
        status.append("Screen Reader: ").append(isScreenReaderEnabled(context) ? "Enabled" : "Disabled").append("\n");
        status.append("Arabic Locale: ").append(isArabicLocale(context) ? "Yes" : "No").append("\n");
        status.append("High Contrast: ").append(isHighContrastModeEnabled(context) ? "Enabled" : "Disabled").append("\n");

        return status.toString();
    }
}