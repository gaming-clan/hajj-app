import { InputValidator } from '../core/validation/InputValidator';

describe('Islamic Content Validation', () => {
  describe('Quranic Reference Validation', () => {
    test('should accept valid Quranic references', () => {
      const validReferences = [
        '2:255',
        '1:1-7',
        '112:1-4',
        '3:102',
      ];

      validReferences.forEach(reference => {
        const result = InputValidator.validateQuranicReference(reference);
        expect(result.isValid).toBe(true);
        expect(result.sanitizedValue).toBe(reference);
      });
    });

    test('should reject invalid Quranic references', () => {
      const invalidReferences = [
        '2:255:300', // Too many colons
        'Al-Baqarah:255', // Text instead of numbers
        '2', // Missing verse number
        ':255', // Missing surah number
        '0:1', // Invalid surah number
        '2:0', // Invalid verse number
      ];

      invalidReferences.forEach(reference => {
        const result = InputValidator.validateQuranicReference(reference);
        expect(result.isValid).toBe(false);
        expect(result.error).toBeDefined();
      });
    });
  });

  describe('Arabic Text Validation', () => {
    test('should accept valid Arabic text', () => {
      const validArabicTexts = [
        'بسم الله الرحمن الرحيم',
        'الحمد لله رب العالمين',
        'قل هو الله أحد',
        'الصلاة خير من النوم',
        '',
      ];

      validArabicTexts.forEach(text => {
        const result = InputValidator.validateArabicText(text);
        expect(result.isValid).toBe(true);
      });
    });

    test('should reject text with dangerous characters', () => {
      const dangerousTexts = [
        'نص مع <script>alert("xss")</script>',
        'javascript:alert("xss")',
        'نص مع " & < >',
      ];

      dangerousTexts.forEach(text => {
        const result = InputValidator.validateArabicText(text);
        expect(result.isValid).toBe(false);
      });
    });

    test('should handle text length limits', () => {
      const longText = 'أ'.repeat(10001); // Exceeds 10,000 character limit

      const result = InputValidator.validateArabicText(longText);
      expect(result.isValid).toBe(false);
      expect(result.error).toContain('less than 10000 characters');
    });
  });

  describe('Hadith Reference Validation', () => {
    test('should accept valid Hadith references', () => {
      const validHadithReferences = [
        'Sahih al-Bukhari 1:1',
        'Sahih Muslim 1:1',
        'Sunan Abu Dawud 1:1',
        'Jami at-Tirmidhi 1:1',
        '',
      ];

      validHadithReferences.forEach(reference => {
        const result = InputValidator.validateHadithReference(reference);
        expect(result.isValid).toBe(true);
      });
    });

    test('should reject invalid Hadith references', () => {
      const invalidHadithReferences = [
        'Invalid Reference <script>',
        'Reference with symbols !@#$%',
        'A'.repeat(501), // Exceeds 500 character limit
      ];

      invalidHadithReferences.forEach(reference => {
        const result = InputValidator.validateHadithReference(reference);
        expect(result.isValid).toBe(false);
      });
    });
  });

  describe('Multilingual Content Validation', () => {
    test('should validate complete multilingual content', () => {
      const content = {
        albanian: 'Përmbajtja në shqip',
        arabic: 'محتوى باللغة العربية',
        english: 'Content in English',
      };

      const result = InputValidator.validateMultilingualContent(content);

      expect(result.isValid).toBe(true);
      expect(result.errors).toHaveLength(0);
      expect(result.sanitizedContent).toBeDefined();
      expect(result.sanitizedContent?.albanian).toBe('Përmbajtja në shqip');
      expect(result.sanitizedContent?.arabic).toBe('محتوى باللغة العربية');
      expect(result.sanitizedContent?.english).toBe('Content in English');
    });

    test('should reject content with invalid parts', () => {
      const content = {
        albanian: 'Përmbajtja me <script>',
        arabic: 'محتوى آمن',
        english: 'Safe content',
      };

      const result = InputValidator.validateMultilingualContent(content);

      expect(result.isValid).toBe(false);
      expect(result.errors).toHaveLength(1);
      expect(result.errors[0]).toContain('Albanian content');
    });
  });

  describe('Title Validation', () => {
    test('should accept valid titles', () => {
      const validTitles = [
        'Titull i Vlefshëm',
        'Valid Title',
        'عنوان صالح',
        'Short',
      ];

      validTitles.forEach(title => {
        const result = InputValidator.validateTitle(title);
        expect(result.isValid).toBe(true);
      });
    });

    test('should reject empty or too long titles', () => {
      const invalidTitles = [
        '',
        '   ', // Only whitespace
        'A'.repeat(201), // Exceeds 200 character limit
        null as any,
      ];

      invalidTitles.forEach(title => {
        const result = InputValidator.validateTitle(title);
        expect(result.isValid).toBe(false);
      });
    });
  });

  describe('Description Validation', () => {
    test('should accept valid descriptions', () => {
      const validDescriptions = [
        'Përshkrim i vlefshëm për përmbajtjen islamike',
        'Valid description for Islamic content',
        'وصف صالح للمحتوى الإسلامي',
      ];

      validDescriptions.forEach(description => {
        const result = InputValidator.validateDescription(description);
        expect(result.isValid).toBe(true);
      });
    });

    test('should reject invalid descriptions', () => {
      const invalidDescriptions = [
        '',
        '<script>alert("xss")</script>',
        'A'.repeat(5001), // Exceeds 5000 character limit
        null as any,
      ];

      invalidDescriptions.forEach(description => {
        const result = InputValidator.validateDescription(description);
        expect(result.isValid).toBe(false);
      });
    });
  });

  describe('Safety Checks', () => {
    test('should detect unsafe content', () => {
      const unsafeContent = [
        '<script>alert("xss")</script>',
        'javascript:alert("xss")',
        'onclick="alert(\'xss\')"',
        '<iframe src="malicious.com"></iframe>',
      ];

      unsafeContent.forEach(content => {
        expect(InputValidator.isSafeContent(content)).toBe(false);
      });
    });

    test('should allow safe content', () => {
      const safeContent = [
        'Normal text content',
        'Safe content with . and , punctuation',
        'Content with numbers 123 and symbols',
        'نص عربي آمن',
        'Përmbajtje e sigurt shqip',
      ];

      safeContent.forEach(content => {
        expect(InputValidator.isSafeContent(content)).toBe(true);
      });
    });
  });

  describe('Search Query Validation', () => {
    test('should accept valid search queries', () => {
      const validQueries = [
        'salat',
        'صلاة',
        'hajj',
        'حج',
        'query with spaces',
        '',
      ];

      validQueries.forEach(query => {
        const result = InputValidator.validateSearchQuery(query);
        expect(result.isValid).toBe(true);
      });
    });

    test('should reject invalid search queries', () => {
      const invalidQueries = [
        '<script>alert("xss")</script>',
        'A'.repeat(101), // Exceeds 100 character limit
        'Query with <HTML> tags',
      ];

      invalidQueries.forEach(query => {
        const result = InputValidator.validateSearchQuery(query);
        expect(result.isValid).toBe(false);
      });
    });
  });
});