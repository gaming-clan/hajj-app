package com.muslim.hajjrules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * Unit tests for Islamic content validation
 * Ensures data integrity and security for religious content
 */
@RunWith(JUnit4.class)
public class ValidationTest {

    @Test
    public void testQuranicReferenceValidation() {
        // Valid Quranic references
        assertTrue("2:255 should be valid", isValidQuranicReference("2:255"));
        assertTrue("1:1-7 should be valid", isValidQuranicReference("1:1-7"));
        assertTrue("112:1-4 should be valid", isValidQuranicReference("112:1-4"));
        assertTrue("3:102 should be valid", isValidQuranicReference("3:102"));

        // Invalid Quranic references
        assertFalse("2:255:300 should be invalid", isValidQuranicReference("2:255:300"));
        assertFalse("Al-Baqarah:255 should be invalid", isValidQuranicReference("Al-Baqarah:255"));
        assertFalse("2 should be invalid", isValidQuranicReference("2"));
        assertFalse(":255 should be invalid", isValidQuranicReference(":255"));
        assertFalse("0:1 should be invalid", isValidQuranicReference("0:1"));
        assertFalse("2:0 should be invalid", isValidQuranicReference("2:0"));
    }

    @Test
    public void testArabicTextValidation() {
        // Valid Arabic text
        assertTrue("Basmala should be valid", isValidArabicText("بسم الله الرحمن الرحيم"));
        assertTrue("Surah Al-Fatiha should be valid", isValidArabicText("الحمد لله رب العالمين"));
        assertTrue("Surah Al-Ikhlas should be valid", isValidArabicText("قل هو الله أحد"));
        assertTrue("Prayer text should be valid", isValidArabicText("الصلاة خير من النوم"));
        assertTrue("Empty text should be valid", isValidArabicText(""));

        // Invalid Arabic text
        assertFalse("Text with script tags should be invalid", isValidArabicText("نص مع <script>alert('xss')</script>"));
        assertFalse("Text with javascript should be invalid", isValidArabicText("javascript:alert('xss')"));
        assertFalse("Text with dangerous characters should be invalid", isValidArabicText("نص مع \" & < >"));
    }

    @Test
    public void testHadithReferenceValidation() {
        // Valid Hadith references
        assertTrue("Bukhari reference should be valid", isValidHadithReference("Sahih al-Bukhari 1:1"));
        assertTrue("Muslim reference should be valid", isValidHadithReference("Sahih Muslim 1:1"));
        assertTrue("Abu Dawud reference should be valid", isValidHadithReference("Sunan Abu Dawud 1:1"));
        assertTrue("Tirmidhi reference should be valid", isValidHadithReference("Jami at-Tirmidhi 1:1"));
        assertTrue("Empty reference should be valid", isValidHadithReference(""));

        // Invalid Hadith references
        assertFalse("Reference with script tags should be invalid", isValidHadithReference("Reference with <script>"));
        assertFalse("Reference with symbols should be invalid", isValidHadithReference("Reference with !@#$%"));
        assertFalse("Too long reference should be invalid", isValidHadithReference(createLongString(501)));
    }

    @Test
    public void testTitleValidation() {
        // Valid titles
        assertTrue("Albanian title should be valid", isValidTitle("Titull i Vlefshëm"));
        assertTrue("English title should be valid", isValidTitle("Valid Title"));
        assertTrue("Arabic title should be valid", isValidTitle("عنوان صالح"));
        assertTrue("Short title should be valid", isValidTitle("Short"));

        // Invalid titles
        assertFalse("Empty title should be invalid", isValidTitle(""));
        assertFalse("Whitespace title should be invalid", isValidTitle("   "));
        assertFalse("Too long title should be invalid", isValidTitle(createLongString(201)));
    }

    @Test
    public void testDescriptionValidation() {
        // Valid descriptions
        assertTrue("Albanian description should be valid", isValidDescription("Përshkrim i vlefshëm për përmbajtjen islamike"));
        assertTrue("English description should be valid", isValidDescription("Valid description for Islamic content"));
        assertTrue("Arabic description should be valid", isValidDescription("وصف صالح للمحتوى الإسلامي"));

        // Invalid descriptions
        assertFalse("Empty description should be invalid", isValidDescription(""));
        assertFalse("Script tag description should be invalid", isValidDescription("<script>alert('xss')</script>"));
        assertFalse("Too long description should be invalid", isValidDescription(createLongString(5001)));
    }

    @Test
    public void testContentSafety() {
        // Safe content
        assertTrue("Normal text should be safe", isSafeContent("Normal text content"));
        assertTrue("Text with punctuation should be safe", isSafeContent("Safe content with . and , punctuation"));
        assertTrue("Text with numbers should be safe", isSafeContent("Content with numbers 123 and symbols"));
        assertTrue("Arabic text should be safe", isSafeContent("نص عربي آمن"));
        assertTrue("Albanian text should be safe", isSafeContent("Përmbajtje e sigurt shqip"));

        // Unsafe content
        assertFalse("Script tag should be unsafe", isSafeContent("<script>alert('xss')</script>"));
        assertFalse("JavaScript URL should be unsafe", isSafeContent("javascript:alert('xss')"));
        assertFalse("Onclick attribute should be unsafe", isSafeContent("onclick=\"alert('xss')\""));
        assertFalse("Iframe tag should be unsafe", isSafeContent("<iframe src=\"malicious.com\"></iframe>"));
    }

    @Test
    public void testSearchQueryValidation() {
        // Valid search queries
        assertTrue("Simple search query should be valid", isValidSearchQuery("salat"));
        assertTrue("Arabic search query should be valid", isValidSearchQuery("صلاة"));
        assertTrue("English search query should be valid", isValidSearchQuery("hajj"));
        assertTrue("Arabic hajj query should be valid", isValidSearchQuery("حج"));
        assertTrue("Query with spaces should be valid", isValidSearchQuery("query with spaces"));
        assertTrue("Empty query should be valid", isValidSearchQuery(""));

        // Invalid search queries
        assertFalse("Script tag query should be invalid", isValidSearchQuery("<script>alert('xss')</script>"));
        assertFalse("Too long query should be invalid", isValidSearchQuery(createLongString(101)));
        assertFalse("HTML tag query should be invalid", isValidSearchQuery("Query with <HTML> tags"));
    }

    @Test
    public void testInputSanitization() {
        // Test HTML sanitization
        String dangerousInput = "<script>alert('xss')</script>Test content";
        String sanitized = sanitizeInput(dangerousInput);
        assertFalse("Sanitized input should not contain script tags", sanitized.contains("<script>"));
        assertTrue("Sanitized input should preserve safe content", sanitized.contains("Test content"));

        // Test entity encoding
        String entityInput = "Test & \"quoted\" text";
        String entitySanitized = sanitizeInput(entityInput);
        assertTrue("Ampersand should be encoded", entitySanitized.contains("&amp;"));
        assertTrue("Quotes should be encoded", entitySanitized.contains("&quot;"));
    }

    @Test
    public void testMaxLengthValidation() {
        // Test different length limits
        assertTrue("Text within limit should be valid", isValidLength("Test", 10));
        assertFalse("Text exceeding limit should be invalid", isValidLength("Very long text that exceeds limit", 10));
        assertTrue("Empty text should be valid for any limit", isValidLength("", 10));
    }

    @Test
    public void testArabicCharacterDetection() {
        // Test Arabic character detection
        assertTrue("Should detect Arabic characters", containsArabicCharacters("بسم الله"));
        assertFalse("Should not detect Arabic in English", containsArabicCharacters("English text"));
        assertFalse("Should not detect Arabic in Albanian", containsArabicCharacters("Tekst shqip"));
        assertTrue("Should detect Arabic in mixed text", containsArabicCharacters("Arabic: بسم الله"));
    }

    @Test
    public void testMultilingualContentValidation() {
        // Valid multilingual content
        MultilingualContent validContent = new MultilingualContent(
            "Përmbajtja në shqip",
            "محتوى باللغة العربية",
            "Content in English"
        );
        assertTrue("Valid multilingual content should pass", isValidMultilingualContent(validContent));

        // Invalid multilingual content with dangerous script
        MultilingualContent invalidContent = new MultilingualContent(
            "Përmbajtje me <script>",
            "محتوى آمن",
            "Safe content"
        );
        assertFalse("Invalid multilingual content should fail", isValidMultilingualContent(invalidContent));
    }

    // Helper methods for validation
    private boolean isValidQuranicReference(String reference) {
        if (reference == null || reference.trim().isEmpty()) return true;

        // Pattern: Surah:Ayah (e.g., 2:255 or 2:255-256)
        return reference.matches("^\\d+:\\d+(?:-\\d+)?$");
    }

    private boolean isValidArabicText(String text) {
        if (text == null || text.trim().isEmpty()) return true;

        // Check for dangerous patterns
        if (text.contains("<script>") || text.contains("javascript:") ||
            text.contains("&") || text.contains("<") || text.contains(">")) {
            return false;
        }

        // Check length limit
        return text.length() <= 10000;
    }

    private boolean isValidHadithReference(String reference) {
        if (reference == null || reference.trim().isEmpty()) return true;

        // Check for dangerous patterns
        if (reference.contains("<script>") || reference.contains("!@#$%")) {
            return false;
        }

        // Check length limit
        return reference.length() <= 500;
    }

    private boolean isValidTitle(String title) {
        if (title == null || title.trim().isEmpty()) return false;
        return title.trim().length() > 0 && title.length() <= 200;
    }

    private boolean isValidDescription(String description) {
        if (description == null || description.trim().isEmpty()) return false;

        // Check for dangerous patterns
        if (description.contains("<script>") || description.contains("javascript:")) {
            return false;
        }

        // Check length limit
        return description.length() <= 5000;
    }

    private boolean isSafeContent(String content) {
        if (content == null) return false;

        // Check for dangerous patterns
        String[] dangerousPatterns = {
            "<script[^>]*>.*?</script>",
            "javascript:",
            "on\\w+\\s*=",
            "<iframe[^>]*>",
            "<object[^>]*>",
            "<embed[^>]*>"
        };

        for (String pattern : dangerousPatterns) {
            if (content.toLowerCase().matches(".*" + pattern + ".*")) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) return true;
        return query.length() <= 100 && !query.contains("<script>");
    }

    private String sanitizeInput(String input) {
        if (input == null) return null;

        return input
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("&", "&amp;")
            .replace("/", "&#x2F;");
    }

    private boolean isValidLength(String text, int maxLength) {
        return text != null && text.length() <= maxLength;
    }

    private boolean containsArabicCharacters(String text) {
        if (text == null) return false;
        return text.matches(".*[\\u0600-\\u06FF].*");
    }

    private boolean isValidMultilingualContent(MultilingualContent content) {
        if (content == null) return false;

        return isValidTitle(content.albanian) &&
               isValidArabicText(content.arabic) &&
               isValidTitle(content.english);
    }

    private String createLongString(int length) {
        return "A".repeat(length);
    }

    // Helper class for multilingual content
    private static class MultilingualContent {
        String albanian;
        String arabic;
        String english;

        public MultilingualContent(String albanian, String arabic, String english) {
            this.albanian = albanian;
            this.arabic = arabic;
            this.english = english;
        }
    }
}