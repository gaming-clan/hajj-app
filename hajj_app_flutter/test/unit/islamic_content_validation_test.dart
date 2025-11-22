import 'package:flutter_test/flutter_test.dart';
import 'package:hajj_app_flutter/features/content/models/hajj_content_models.dart';
import 'package:hajj_app_flutter/shared/widgets/accessible_text.dart';

void main() {
  group('Islamic Content Validation Tests', () {
    testWidgets('QuranicVerse widget renders correctly', (WidgetTester tester) async {
      const verseText = 'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ';
      const translation = 'In the name of Allah, the Most Gracious, the Most Merciful';

      await tester.pumpWidget(
        QuranicVerse(
          verseText: verseText,
          translation: translation,
        ),
      );

      // Verify the verse text is rendered
      expect(find.text(verseText), findsOneWidget);

      // Verify the translation is rendered
      expect(find.text(translation), findsOneWidget);

      // Check for proper semantic labels
      expect(find.bySemanticsLabel('Quranic verse'), findsOneWidget);
      expect(find.bySemanticsLabel('Translation of Quranic verse'), findsOneWidget);
    });

    testWidgets('HadithText widget renders correctly', (WidgetTester tester) async {
      const hadithText = 'إِنَّمَا الْأَعْمَالُ بِالنِّيَّاتِ';
      const narrator = 'Narrated by Bukhari and Muslim';
      const translation = 'Actions are judged by intentions';

      await tester.pumpWidget(
        HadithText(
          hadithText: hadithText,
          narrator: narrator,
          translation: translation,
        ),
      );

      // Verify all components are rendered
      expect(find.text(narrator), findsOneWidget);
      expect(find.text(hadithText), findsOneWidget);
      expect(find.text(translation), findsOneWidget);

      // Check for proper semantic labels
      expect(find.bySemanticsLabel('Hadith narrator'), findsOneWidget);
      expect(find.bySemanticsLabel('Hadith text'), findsOneWidget);
      expect(find.bySemanticsLabel('Hadith translation'), findsOneWidget);
    });

    testWidgets('AccessibleText handles Arabic RTL correctly', (WidgetTester tester) async {
      const arabicText = 'الصلاة عمود الدين';

      await tester.pumpWidget(
        AccessibleText(
          text: arabicText,
          isArabic: true,
          isHeading: true,
        ),
      );

      expect(find.text(arabicText), findsOneWidget);

      // Verify semantic header property
      expect(find.bySemanticsLabel(arabicText), findsOneWidget);
      expect(find.bySemanticsFlag(SemanticsFlag.isHeader), findsOneWidget);
    });

    testWidgets('AccessibleHeading renders correct heading level', (WidgetTester tester) async {
      const headingText = 'Hajj Rules';

      await tester.pumpWidget(
        AccessibleHeading(
          text: headingText,
          level: 2,
        ),
      );

      expect(find.text(headingText), findsOneWidget);
      expect(find.bySemanticsLabel(headingText), findsOneWidget);
      expect(find.bySemanticsFlag(SemanticsFlag.isHeader), findsOneWidget);
    });
  });

  group('Content Model Validation', () {
    test('HajjRule model creates correctly with valid data', () {
      const rule = HajjRule(
        id: 1,
        categoryId: 1,
        title: MultilingualContent(
          albanian: 'Titull',
          arabic: 'العنوان',
          english: 'Title',
        ),
        description: MultilingualContent(
          albanian: 'Përshkrim',
          arabic: 'الوصف',
          english: 'Description',
        ),
        orderIndex: 1,
        isFavorite: false,
        createdAt: DateTime.now(),
      );

      expect(rule.id, equals(1));
      expect(rule.categoryId, equals(1));
      expect(rule.title.albanian, equals('Titull'));
      expect(rule.title.arabic, equals('العنوان'));
      expect(rule.title.english, equals('Title'));
      expect(rule.isFavorite, isFalse);
    });

    test('MultilingualContent returns correct content for different languages', () {
      const content = MultilingualContent(
        albanian: 'Përmbajtja në shqip',
        arabic: 'محتوى باللغة العربية',
        english: 'Content in English',
      );

      expect(content.getContent('sq'), equals('Përmbajtja në shqip'));
      expect(content.getContent('ar'), equals('محتوى باللغة العربية'));
      expect(content.getContent('en'), equals('Content in English'));

      // Test fallback to Albanian for unknown language
      expect(content.getContent('fr'), equals('Përmbajtja në shqip'));
      expect(content.getContent(''), equals('Përmbajtja në shqip'));
    });

    test('Category model creates correctly', () {
      final category = Category(
        id: 1,
        title: 'Pillars of Islam',
        description: 'The five pillars of Islam',
        iconPath: 'assets/icons/pillars.png',
        orderIndex: 1,
        createdAt: DateTime.now(),
      );

      expect(category.id, equals(1));
      expect(category.title, equals('Pillars of Islam'));
      expect(category.orderIndex, equals(1));
    });

    test('UserProgress model handles completion status', () {
      final progress = UserProgress(
        id: 1,
        ruleId: 1,
        completed: true,
        completedAt: DateTime.now(),
        notes: 'Successfully completed',
      );

      expect(progress.completed, isTrue);
      expect(progress.ruleId, equals(1));
      expect(progress.notes, equals('Successfully completed'));
      expect(progress.completedAt, isNotNull);
    });
  });

  group('RitualCategory Enum Tests', () {
    test('RitualCategory converts correctly from string', () {
      expect(RitualCategory.fromString('pre_hajj'), equals(RitualCategory.preHajj));
      expect(RitualCategory.fromString('ihram'), equals(RitualCategory.ihram));
      expect(RitualCategory.fromString('tawaf'), equals(RitualCategory.tawaf));
      expect(RitualCategory.fromString('invalid'), equals(RitualCategory.preHajj)); // fallback
    });

    test('RitualCategory has correct display names', () {
      expect(RitualCategory.preHajj.displayName, equals('Before Hajj'));
      expect(RitualCategory.ihram.displayName, equals('Ihram'));
      expect(RitualCategory.tawaf.displayName, equals('Tawaf'));
      expect(RitualCategory.sai.displayName, equals('Sa\'i'));
      expect(RitualCategory.hajjDays.displayName, equals('Hajj Days'));
    });
  });

  group('VerificationStatus Enum Tests', () {
    test('VerificationStatus converts correctly from string', () {
      expect(VerificationStatus.fromString('verified'), equals(VerificationStatus.verified));
      expect(VerificationStatus.fromString('pending'), equals(VerificationStatus.pending));
      expect(VerificationStatus.fromString('needs_review'), equals(VerificationStatus.needsReview));
      expect(VerificationStatus.fromString('invalid'), equals(VerificationStatus.pending)); // fallback
    });

    test('VerificationStatus has correct descriptions', () {
      expect(VerificationStatus.verified.description, equals('Verified by Islamic Scholars'));
      expect(VerificationStatus.pending.description, equals('Pending Verification'));
      expect(VerificationStatus.needsReview.description, equals('Needs Review'));
    });
  });

  group('Accessibility Utility Tests', () {
    test('AccessibilityUtils detects Arabic text correctly', () {
      expect(AccessibilityUtils.isArabicText('بسم الله'), isTrue);
      expect(AccessibilityUtils.isArabicText('English text'), isFalse);
      expect(AccessibilityUtils.isArabicText('Tekst shqip'), isFalse);
      expect(AccessibilityUtils.isArabicText('Arabic: بسم الله'), isTrue);
    });

    test('AccessibilityUtils formats Arabic numbers', () {
      expect(AccessibilityUtils.formatArabicNumbers('123'), equals('١٢٣'));
      expect(AccessibilityUtils.formatArabicNumbers('Year 2024'), equals('Year ٢٠٢٤'));
      expect(AccessibilityUtils.formatArabicNumbers(''), equals(''));
    });

    test('AccessibilityUtils removes Arabic diacritics', () {
      expect(AccessibilityUtils.removeArabicDiacritics('الْحَمْدُ لِلَّهِ'), equals('الحمد لله'));
      expect(AccessibilityUtils.removeArabicDiacritics('الرحمن'), equals('الرحمن'));
    });

    test('AccessibilityUtils determines text direction correctly', () {
      expect(AccessibilityUtils.getTextDirection('English text', const Locale('en')), equals(TextDirection.ltr));
      expect(AccessibilityUtils.getTextDirection('بسم الله', const Locale('ar')), equals(TextDirection.rtl));
      expect(AccessibilityUtils.getTextDirection('بسم الله', const Locale('en')), equals(TextDirection.rtl));
    });

    test('AccessibilityUtils determines text alignment correctly', () {
      expect(AccessibilityUtils.getTextAlignment('English text', const Locale('en')), equals(TextAlign.left));
      expect(AccessibilityUtils.getTextAlignment('بسم الله', const Locale('ar')), equals(TextAlign.right));
      expect(AccessibilityUtils.getTextAlignment('بسم الله', const Locale('en')), equals(TextAlign.right));
    });
  });

  group('Islamic Content Safety Tests', () {
    test('Safe Arabic content should pass validation', () {
      const safeContent = [
        'بسم الله الرحمن الرحيم',
        'الحمد لله رب العالمين',
        'الصلاة عمود الدين',
        'الحج فرض على كل مسلم قادر',
      ];

      for (final content in safeContent) {
        expect(AccessibilityUtils.isArabicText(content), isTrue);
        expect(AccessibilityUtils.isSafeContent(content), isTrue);
      }
    });

    test('Content with dangerous patterns should be detected', () {
      final unsafeContent = [
        'نص مع <script>alert("xss")</script>',
        'javascript:alert("xss")',
        'onclick="alert(\'xss\')"',
        'نص مع " & < >',
      ];

      for (final content in unsafeContent) {
        expect(AccessibilityUtils.isSafeContent(content), isFalse);
      }
    });
  });

  group('Data Model Serialization Tests', () {
    test('HajjRule serializes and deserializes correctly', () {
      final originalRule = HajjRule(
        id: 1,
        categoryId: 1,
        title: MultilingualContent(albanian: 'Titull', arabic: 'العنوان', english: 'Title'),
        description: MultilingualContent(albanian: 'Përshkrim', arabic: 'الوصف', english: 'Description'),
        orderIndex: 1,
        isFavorite: true,
        quranicReference: '2:255',
        hadithReference: 'Bukhari 1:1',
        createdAt: DateTime.now(),
      );

      final map = originalRule.toMap();
      final deserializedRule = HajjRule.fromMap(map);

      expect(deserializedRule.id, equals(originalRule.id));
      expect(deserializedRule.categoryId, equals(originalRule.categoryId));
      expect(deserializedRule.title.albanian, equals(originalRule.title.albanian));
      expect(deserializedRule.title.arabic, equals(originalRule.title.arabic));
      expect(deserializedRule.title.english, equals(originalRule.title.english));
      expect(deserializedRule.isFavorite, equals(originalRule.isFavorite));
      expect(deserializedRule.quranicReference, equals(originalRule.quranicReference));
    });

    test('Category serializes and deserializes correctly', () {
      final originalCategory = Category(
        id: 1,
        title: 'Test Category',
        description: 'Test Description',
        iconPath: 'test.png',
        orderIndex: 1,
        createdAt: DateTime.now(),
      );

      final map = originalCategory.toMap();
      final deserializedCategory = Category.fromMap(map);

      expect(deserializedCategory.id, equals(originalCategory.id));
      expect(deserializedCategory.title, equals(originalCategory.title));
      expect(deserializedCategory.description, equals(originalCategory.description));
      expect(deserializedCategory.iconPath, equals(originalCategory.iconPath));
    });

    test('UserProgress serializes and deserializes correctly', () {
      final originalProgress = UserProgress(
        id: 1,
        ruleId: 1,
        completed: true,
        completedAt: DateTime.now(),
        notes: 'Test notes',
      );

      final map = originalProgress.toMap();
      final deserializedProgress = UserProgress.fromMap(map);

      expect(deserializedProgress.id, equals(originalProgress.id));
      expect(deserializedProgress.ruleId, equals(originalProgress.ruleId));
      expect(deserializedProgress.completed, equals(originalProgress.completed));
      expect(deserializedProgress.notes, equals(originalProgress.notes));
    });
  });
}