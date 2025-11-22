import { MultilingualString, MultilingualTitle, MultilingualContent, MultilingualDescription, AppSettings } from '../types';
import { SUPPORTED_LANGUAGES } from '../constants';

export type Language = keyof typeof SUPPORTED_LANGUAGES;

/**
 * Gets text in the specified language from a multilingual string
 */
export function getLocalizedText(
  text: MultilingualString | MultilingualTitle | MultilingualContent | MultilingualDescription,
  language: Language = 'albanian'
): string {
  return text[language] || text.albanian || text.english || '';
}

/**
 * Gets the primary language text with fallbacks
 */
export function getLocalizedTextWithFallback(
  text: MultilingualString | MultilingualTitle | MultilingualContent | MultilingualDescription,
  settings: AppSettings
): string {
  const { language, showArabicText, showTransliteration } = settings;

  // If Arabic display is enabled and Arabic text exists, prioritize it
  if (showArabicText && language === 'arabic' && 'arabic' in text && text.arabic) {
    return text.arabic;
  }

  // Get text in the user's preferred language
  let result = getLocalizedText(text, language);

  // If transliteration is enabled and we're showing Arabic, add transliteration
  if (showTransliteration && 'transliteration' in text && text.transliteration && result === text.arabic) {
    result = `${result}\n${text.transliteration}`;
  }

  return result;
}

/**
 * Checks if text contains RTL content (Arabic)
 */
export function isRTL(text: string): boolean {
  // Check for Arabic characters
  const arabicRegex = /[\u0600-\u06FF]/;
  return arabicRegex.test(text);
}

/**
 * Determines if the app should use RTL layout based on language and content
 */
export function shouldUseRTL(settings: AppSettings, content?: string): boolean {
  if (settings.rtl) return true;
  if (settings.language === 'arabic') return true;
  if (content && isRTL(content)) return true;
  return false;
}

/**
 * Formats text with proper RTL/LTR handling
 */
export function formatText(text: string, isRTL: boolean): {
  text: string;
  style: {
    writingDirection: 'ltr' | 'rtl';
    textAlign: 'auto' | 'left' | 'right';
  };
} {
  return {
    text,
    style: {
      writingDirection: isRTL ? 'rtl' : 'ltr',
      textAlign: isRTL ? 'right' : 'left',
    },
  };
}

/**
 * Validates multilingual content structure
 */
export function validateMultilingualContent(
  content: any
): content is MultilingualString | MultilingualTitle | MultilingualContent | MultilingualDescription {
  if (!content || typeof content !== 'object') return false;

  return (
    typeof content.albanian === 'string' &&
    typeof content.arabic === 'string' &&
    typeof content.english === 'string'
  );
}

/**
 * Creates a proper error message for missing translations
 */
export function getMissingTranslationError(key: string, language: Language): string {
  return `Missing translation for key "${key}" in language "${language}"`;
}

/**
 * Sanitizes and normalizes text for search purposes
 */
export function normalizeTextForSearch(text: string): string {
  return text
    .toLowerCase()
    .trim()
    .replace(/[^\w\s]/g, '')
    .replace(/\s+/g, ' ');
}

/**
 * Enhanced search that works with multilingual content
 */
export function searchMultilingualContent<T extends Record<string, any>>(
  items: T[],
  query: string,
  settings: AppSettings,
  fields: (keyof T)[] = []
): T[] {
  if (!query.trim()) return items;

  const normalizedQuery = normalizeTextForSearch(query);

  return items.filter(item => {
    // If no specific fields provided, search all string fields
    if (fields.length === 0) {
      return Object.values(item).some(value => {
        if (typeof value === 'string') {
          return normalizeTextForSearch(value).includes(normalizedQuery);
        }

        if (validateMultilingualContent(value)) {
          const localizedText = getLocalizedTextWithFallback(value, settings);
          return normalizeTextForSearch(localizedText).includes(normalizedQuery);
        }

        return false;
      });
    }

    // Search only in specified fields
    return fields.some(field => {
      const value = item[field];

      if (typeof value === 'string') {
        return normalizeTextForSearch(value).includes(normalizedQuery);
      }

      if (validateMultilingualContent(value)) {
        const localizedText = getLocalizedTextWithFallback(value, settings);
        return normalizeTextForSearch(localizedText).includes(normalizedQuery);
      }

      return false;
    });
  });
}

/**
 * Formats dates according to language and region
 */
export function formatDate(
  date: Date,
  language: Language,
  format: 'short' | 'medium' | 'long' = 'medium'
): string {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: format === 'short' ? 'short' : 'long',
    day: 'numeric',
  };

  if (format === 'long') {
    options.weekday = 'long';
  }

  try {
    // For Arabic, use RTL locale
    if (language === 'arabic') {
      return date.toLocaleDateString('ar-SA', options);
    }

    // For Albanian
    if (language === 'albanian') {
      return date.toLocaleDateString('sq-AL', options);
    }

    // Default to English
    return date.toLocaleDateString('en-US', options);
  } catch (error) {
    // Fallback to ISO format
    return date.toISOString().split('T')[0];
  }
}

/**
 * Generates a unique ID for content items
 */
export function generateId(prefix: string = ''): string {
  const timestamp = Date.now();
  const random = Math.random().toString(36).substring(2, 8);
  return `${prefix}${timestamp}_${random}`;
}

/**
 * Debounce utility for search functionality
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timeoutId: NodeJS.Timeout;

  return (...args: Parameters<T>) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func.apply(null, args), delay);
  };
}

/**
 * Deep clone utility for immutable state updates
 */
export function deepClone<T>(obj: T): T {
  if (obj === null || typeof obj !== 'object') return obj;
  if (obj instanceof Date) return new Date(obj.getTime()) as unknown as T;
  if (obj instanceof Array) return obj.map(item => deepClone(item)) as unknown as T;

  const cloned = {} as T;
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      cloned[key] = deepClone(obj[key]);
    }
  }

  return cloned;
}

/**
 * Validates if a string is a valid Arabic text
 */
export function isValidArabic(text: string): boolean {
  const arabicRegex = /^[\u0600-\u06FF\s\p{P}\p{N}]*$/u;
  return arabicRegex.test(text.trim());
}

/**
 * Extracts Arabic text from mixed content
 */
export function extractArabicText(text: string): string[] {
  const arabicRegex = /[\u0600-\u06FF]+/g;
  return text.match(arabicRegex) || [];
}

/**
 * Performance monitoring utility
 */
export function measurePerformance<T>(name: string, fn: () => T): T {
  const start = performance.now();
  const result = fn();
  const end = performance.now();

  console.log(`[Performance] ${name}: ${end - start}ms`);

  return result;
}

/**
 * Cache utility for expensive operations
 */
export class SimpleCache<T> {
  private cache = new Map<string, { value: T; timestamp: number }>();
  private ttl: number;

  constructor(ttl: number = 5 * 60 * 1000) { // 5 minutes default TTL
    this.ttl = ttl;
  }

  set(key: string, value: T): void {
    this.cache.set(key, {
      value,
      timestamp: Date.now(),
    });
  }

  get(key: string): T | null {
    const item = this.cache.get(key);

    if (!item) return null;

    if (Date.now() - item.timestamp > this.ttl) {
      this.cache.delete(key);
      return null;
    }

    return item.value;
  }

  clear(): void {
    this.cache.clear();
  }

  size(): number {
    return this.cache.size;
  }
}