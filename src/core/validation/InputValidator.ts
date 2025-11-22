/**
 * Input validation and sanitization for Islamic content
 * Prevents XSS, injection attacks, and ensures data integrity
 */

// Type for validation results
interface ValidationResult {
  isValid: boolean;
  sanitizedValue?: string;
  error?: string;
}

// Regular expressions for validation
const REGEX_PATTERNS = {
  // Allowed characters for Islamic content (Arabic, English, Albanian characters, numbers, basic punctuation)
  ISLAMIC_CONTENT: /^[\w\s\u0600-\u06FF\u0750-\u077F\uFB50-\uFDFF\uFE70-\uFEFF\u00C0-\u017F.,;:!?'"()\-\/@#%&*+=]*$/,

  // Arabic text only (for religious content)
  ARABIC_TEXT: /^[\u0600-\u06FF\u0750-\u077F\uFB50-\uFDFF\uFE70-\uFEFF\s\W]*$/,

  // Quranic verse reference pattern
  QURANIC_REFERENCE: /^(\d+):(\d+)(-\d+)?$/,

  // Hadith reference pattern
  HADITH_REFERENCE: /^[A-Za-z\s,.\d]+$/,

  // Email pattern
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,

  // Phone number pattern
  PHONE: /^\+?[\d\s\-\(\)]+$/,

  // General safe text (prevents XSS)
  SAFE_TEXT: /^[^<>\"'&]*$/,
};

// Maximum lengths for different types of content
const MAX_LENGTHS = {
  TITLE: 200,
  DESCRIPTION: 5000,
  ARABIC_TEXT: 10000,
  REFERENCE: 500,
  NOTE: 1000,
  SEARCH_QUERY: 100,
};

export class InputValidator {
  /**
   * Validate and sanitize Islamic content titles
   */
  static validateTitle(input: string): ValidationResult {
    if (!input || typeof input !== 'string') {
      return { isValid: false, error: 'Title is required' };
    }

    const trimmed = input.trim();

    if (trimmed.length === 0) {
      return { isValid: false, error: 'Title cannot be empty' };
    }

    if (trimmed.length > MAX_LENGTHS.TITLE) {
      return { isValid: false, error: `Title must be less than ${MAX_LENGTHS.TITLE} characters` };
    }

    if (!REGEX_PATTERNS.ISLAMIC_CONTENT.test(trimmed)) {
      return { isValid: false, error: 'Title contains invalid characters' };
    }

    return { isValid: true, sanitizedValue: this.sanitizeHtml(trimmed) };
  }

  /**
   * Validate and sanitize Arabic religious text
   */
  static validateArabicText(input: string): ValidationResult {
    if (!input || typeof input !== 'string') {
      return { isValid: true, sanitizedValue: '' };
    }

    const trimmed = input.trim();

    if (trimmed.length === 0) {
      return { isValid: true, sanitizedValue: '' };
    }

    if (trimmed.length > MAX_LENGTHS.ARABIC_TEXT) {
      return { isValid: false, error: `Arabic text must be less than ${MAX_LENGTHS.ARABIC_TEXT} characters` };
    }

    // Allow Arabic text with basic punctuation
    if (!REGEX_PATTERNS.ARABIC_TEXT.test(trimmed)) {
      return { isValid: false, error: 'Arabic text contains invalid characters' };
    }

    return { isValid: true, sanitizedValue: trimmed };
  }

  /**
   * Validate and sanitize content descriptions
   */
  static validateDescription(input: string): ValidationResult {
    if (!input || typeof input !== 'string') {
      return { isValid: false, error: 'Description is required' };
    }

    const trimmed = input.trim();

    if (trimmed.length === 0) {
      return { isValid: false, error: 'Description cannot be empty' };
    }

    if (trimmed.length > MAX_LENGTHS.DESCRIPTION) {
      return { isValid: false, error: `Description must be less than ${MAX_LENGTHS.DESCRIPTION} characters` };
    }

    if (!REGEX_PATTERNS.ISLAMIC_CONTENT.test(trimmed)) {
      return { isValid: false, error: 'Description contains invalid characters' };
    }

    return { isValid: true, sanitizedValue: this.sanitizeHtml(trimmed) };
  }

  /**
   * Validate Quranic verse reference
   */
  static validateQuranicReference(input: string): ValidationResult {
    if (!input || typeof input !== 'string') {
      return { isValid: true, sanitizedValue: '' };
    }

    const trimmed = input.trim();

    if (trimmed.length === 0) {
      return { isValid: true, sanitizedValue: '' };
    }

    // Pattern: Surah:Ayah (e.g., 2:255 or 2:255-256)
    if (!REGEX_PATTERNS.QURANIC_REFERENCE.test(trimmed)) {
      return { isValid: false, error: 'Invalid Quranic reference format. Use format: Surah:Ayah (e.g., 2:255)' };
    }

    return { isValid: true, sanitizedValue: trimmed };
  }

  /**
   * Validate Hadith reference
   */
  static validateHadithReference(input: string): ValidationResult {
    if (!input || typeof input !== 'string') {
      return { isValid: true, sanitizedValue: '' };
    }

    const trimmed = input.trim();

    if (trimmed.length === 0) {
      return { isValid: true, sanitizedValue: '' };
    }

    if (trimmed.length > MAX_LENGTHS.REFERENCE) {
      return { isValid: false, error: `Reference must be less than ${MAX_LENGTHS.REFERENCE} characters` };
    }

    if (!REGEX_PATTERNS.HADITH_REFERENCE.test(trimmed)) {
      return { isValid: false, error: 'Invalid Hadith reference format' };
    }

    return { isValid: true, sanitizedValue: this.sanitizeHtml(trimmed) };
  }

  /**
   * Validate search queries
   */
  static validateSearchQuery(input: string): ValidationResult {
    if (!input || typeof input !== 'string') {
      return { isValid: true, sanitizedValue: '' };
    }

    const trimmed = input.trim();

    if (trimmed.length === 0) {
      return { isValid: true, sanitizedValue: '' };
    }

    if (trimmed.length > MAX_LENGTHS.SEARCH_QUERY) {
      return { isValid: false, error: `Search query must be less than ${MAX_LENGTHS.SEARCH_QUERY} characters` };
    }

    // Allow safe characters only
    if (!REGEX_PATTERNS.SAFE_TEXT.test(trimmed)) {
      return { isValid: false, error: 'Search query contains invalid characters' };
    }

    return { isValid: true, sanitizedValue: trimmed };
  }

  /**
   * Validate email addresses
   */
  static validateEmail(input: string): ValidationResult {
    if (!input || typeof input !== 'string') {
      return { isValid: false, error: 'Email is required' };
    }

    const trimmed = input.trim();

    if (trimmed.length === 0) {
      return { isValid: false, error: 'Email cannot be empty' };
    }

    if (!REGEX_PATTERNS.EMAIL.test(trimmed)) {
      return { isValid: false, error: 'Invalid email format' };
    }

    return { isValid: true, sanitizedValue: trimmed.toLowerCase() };
  }

  /**
   * Validate phone numbers
   */
  static validatePhone(input: string): ValidationResult {
    if (!input || typeof input !== 'string') {
      return { isValid: true, sanitizedValue: '' };
    }

    const trimmed = input.trim();

    if (trimmed.length === 0) {
      return { isValid: true, sanitizedValue: '' };
    }

    if (!REGEX_PATTERNS.PHONE.test(trimmed)) {
      return { isValid: false, error: 'Invalid phone number format' };
    }

    return { isValid: true, sanitizedValue: trimmed };
  }

  /**
   * Validate user notes
   */
  static validateNote(input: string): ValidationResult {
    if (!input || typeof input !== 'string') {
      return { isValid: true, sanitizedValue: '' };
    }

    const trimmed = input.trim();

    if (trimmed.length === 0) {
      return { isValid: true, sanitizedValue: '' };
    }

    if (trimmed.length > MAX_LENGTHS.NOTE) {
      return { isValid: false, error: `Note must be less than ${MAX_LENGTHS.NOTE} characters` };
    }

    if (!REGEX_PATTERNS.SAFE_TEXT.test(trimmed)) {
      return { isValid: false, error: 'Note contains invalid characters' };
    }

    return { isValid: true, sanitizedValue: this.sanitizeHtml(trimmed) };
  }

  /**
   * Sanitize HTML content to prevent XSS
   */
  private static sanitizeHtml(input: string): string {
    return input
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#x27;')
      .replace(/&/g, '&amp;')
      .replace(/\//g, '&#x2F;');
  }

  /**
   * Validate multilingual content
   */
  static validateMultilingualContent(content: {
    albanian?: string;
    arabic?: string;
    english?: string;
  }): { isValid: boolean; errors: string[]; sanitizedContent?: any } {
    const errors: string[] = [];
    const sanitizedContent: any = {};

    // Validate Albanian content
    if (content.albanian !== undefined) {
      const albanianResult = this.validateDescription(content.albanian);
      if (albanianResult.isValid) {
        sanitizedContent.albanian = albanianResult.sanitizedValue;
      } else {
        errors.push(`Albanian content: ${albanianResult.error}`);
      }
    }

    // Validate Arabic content
    if (content.arabic !== undefined) {
      const arabicResult = this.validateArabicText(content.arabic);
      if (arabicResult.isValid) {
        sanitizedContent.arabic = arabicResult.sanitizedValue;
      } else {
        errors.push(`Arabic content: ${arabicResult.error}`);
      }
    }

    // Validate English content
    if (content.english !== undefined) {
      const englishResult = this.validateDescription(content.english);
      if (englishResult.isValid) {
        sanitizedContent.english = englishResult.sanitizedValue;
      } else {
        errors.push(`English content: ${englishResult.error}`);
      }
    }

    return {
      isValid: errors.length === 0,
      errors,
      sanitizedContent: errors.length === 0 ? sanitizedContent : undefined,
    };
  }

  /**
   * Check if content is safe for display
   */
  static isSafeContent(input: string): boolean {
    if (!input || typeof input !== 'string') {
      return false;
    }

    // Check for dangerous patterns
    const dangerousPatterns = [
      /<script[^>]*>.*?<\/script>/gi,
      /javascript:/gi,
      /on\w+\s*=/gi,
      /<iframe[^>]*>/gi,
      /<object[^>]*>/gi,
      /<embed[^>]*>/gi,
    ];

    return !dangerousPatterns.some(pattern => pattern.test(input));
  }

  /**
   * Sanitize filename for security
   */
  static sanitizeFilename(input: string): string {
    return input
      .replace(/[^a-zA-Z0-9.-]/g, '_')
      .replace(/_{2,}/g, '_')
      .toLowerCase();
  }
}