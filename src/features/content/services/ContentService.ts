import AsyncStorage from '@react-native-async-storage/async-storage';
import {
  HajjData,
  Category,
  Rule,
  DataServiceInterface,
  AppSettings,
} from '../../../core/types';
import { CATEGORIES, APP_CONFIG } from '../../../core/constants';
import {
  getLocalizedText,
  getLocalizedTextWithFallback,
  generateId,
  measurePerformance,
  SimpleCache,
} from '../../../core/utils';

// Legacy data import
const legacyHajjData = require('../../../assets/data/hajj_rules.json');

/**
 * Enhanced Content Service with multilingual support and caching
 */
export class ContentService implements DataServiceInterface {
  private static instance: ContentService;
  private hajjData: HajjData | null = null;
  private cache = new SimpleCache<any>(10 * 60 * 1000); // 10 minutes cache
  private currentSettings: AppSettings | null = null;

  private constructor() {}

  public static getInstance(): ContentService {
    if (!ContentService.instance) {
      ContentService.instance = new ContentService();
    }
    return ContentService.instance;
  }

  /**
   * Initialize the service with settings
   */
  public async initialize(settings: AppSettings): Promise<void> {
    this.currentSettings = settings;
    await this.loadHajjData();
  }

  /**
   * Load and transform Hajj data from legacy format to new multilingual format
   */
  private async loadHajjData(): Promise<void> {
    const cacheKey = 'hajj_data';

    // Try to get from cache first
    const cachedData = this.cache.get(cacheKey);
    if (cachedData) {
      this.hajjData = cachedData;
      return;
    }

    try {
      // Try to load from AsyncStorage
      const storedData = await AsyncStorage.getItem(APP_CONFIG.storage.hajjDataKey);
      if (storedData) {
        const parsedData = JSON.parse(storedData);
        this.hajjData = this.transformLegacyData(parsedData);
        this.cache.set(cacheKey, this.hajjData);
        return;
      }
    } catch (error) {
      console.warn('Failed to load data from storage, using legacy data:', error);
    }

    // Fallback to legacy data and transform it
    this.hajjData = this.transformLegacyData(legacyHajjData);
    this.cache.set(cacheKey, this.hajjData);

    // Save transformed data to storage
    try {
      await AsyncStorage.setItem(APP_CONFIG.storage.hajjDataKey, JSON.stringify(this.hajjData));
    } catch (error) {
      console.warn('Failed to save data to storage:', error);
    }
  }

  /**
   * Transform legacy data format to new multilingual format
   */
  private transformLegacyData(legacyData: any): HajjData {
    return measurePerformance('transformLegacyData', () => {
      const transformed: HajjData = {
        title: {
          albanian: legacyData.title || 'Rregullat e Haxhit',
          arabic: 'أحكام الحج',
          english: 'Rules of Hajj',
        },
        introduction: {
          description: {
            albanian: legacyData.introduction?.description || '',
            arabic: 'الحج هو أحد أركان الإسلام الخمسة، وهو فرض على كل مسلم قادر على أدائه مرة واحدة في العمر على الأقل',
            english: 'Hajj is one of the five pillars of Islam and is obligatory for every Muslim who is able to perform it at least once in a lifetime',
            quranic_reference: 'وَلِلَّهِ عَلَى ٱلنَّاسِ حِجُّ ٱلْبَيْتِ مَنِ ٱسْتَطَاعَ إِلَيْهِ سَبِيلًا',
          },
          qabja: {
            albanian: legacyData.introduction?.qabja || '',
            arabic: 'الكعبة هي المكان الذي تجذب إليه قلوب المسلمين من جميع أنحاء العالم، وهي أول بيت بني للعبادة على الأرض',
            english: 'The Kaaba is the place that attracts the hearts of Muslims from all over the world, and it is the first house built for worship on earth',
            quranic_reference: 'إِنَّ أَوَّلَ بَيْتٍ وُضِعَ لِلنَّاسِ لَلَّذِي بِبَكَّةَ مُبَارَكًا وَهُدًى لِّلْعَٰلَمِينَ',
          },
        },
        shtyllat_e_islamit: this.transformPillars(legacyData.shtyllat_e_islamit || []),
        detyrimi_i_haxhit: this.transformHajjObligation(legacyData.detyrimi_i_haxhit || {}),
        edukata_e_udhetimit: this.transformTravelEtiquette(legacyData.edukata_e_udhetimit || []),
        ihrami: this.transformIhramInfo(legacyData.ihrami || {}),
        ndalesat_gjate_ihramit: this.transformProhibitions(legacyData.ndalesat_gjate_ihramit || []),
        vendcaktimet: this.transformMiqat(legacyData.vendcaktimet || []),
        qabja: {
          description: {
            albanian: legacyData.qabja?.description || '',
            arabic: 'الكعبة هي المكان الذي تجذب إليه قلوب المسلمين من جميع أنحاء العالم، وهي أول بيت بني للعبادة على الأرض',
            english: 'The Kaaba is the place that attracts the hearts of Muslims from all over the world, and it is the first house built for worship on earth',
          },
          rendesia: {
            albanian: legacyData.qabja?.rendesia || '',
            arabic: 'يتوجه المسلمون نحو الكعبة خمس مرات في اليوم لأداء الصلاة ويطوفون حولها لأداء مناسك الحج',
            english: 'Muslims face the Kaaba five times a day for prayer and circumambulate it during Hajj rites',
          },
        },
        rendesia_e_haxhit: {
          description: {
            albanian: legacyData.rendesia_e_haxhit?.description || '',
            arabic: 'الحج هو أحد أركان الإسلام وهو فرض على كل مسلم قادر على أدائه',
            english: 'Hajj is one of the pillars of Islam and is obligatory for every Muslim who is able to perform it',
          },
          hadith: {
            albanian: legacyData.rendesia_e_haxhit?.hadith || '',
            arabic: 'الحج فرض مرة واحدة في العمر، ومن زاد فهو نافلة',
            english: 'Hajj is obligatory once in a lifetime, and whoever does more than that is voluntary',
          },
          keshilla: {
            albanian: legacyData.rendesia_e_haxhit?.keshilla || '',
            arabic: 'لمزيد من التفاصيل والإرشادات المحددة، يوصى بالاستشارة بمصادر متخصصة وعلماء إسلاميين',
            english: 'For more details and specific guidance, it is recommended to consult specialized sources and Islamic scholars',
          },
        },
      };

      return transformed;
    });
  }

  private transformPillars(legacyPillars: any[]) {
    return legacyPillars.map((pillar, index) => ({
      id: pillar.id || index + 1,
      name: {
        albanian: pillar.name,
        arabic: this.getPillarArabicName(pillar.name),
        english: this.getPillarEnglishName(pillar.name),
      },
      description: {
        albanian: pillar.description,
        arabic: this.getPillarArabicDescription(pillar.name),
        english: this.getPillarEnglishDescription(pillar.name),
        quranic_reference: this.getPillarQuranicReference(pillar.name),
      },
      order: index + 1,
    }));
  }

  private transformHajjObligation(legacyObligation: any) {
    return {
      description: {
        albanian: legacyObligation.description,
        arabic: 'الحج فرض مرة واحدة في العمر',
        english: 'Hajj is obligatory only once in a lifetime',
      },
      hadith: {
        albanian: legacyObligation.hadith,
        arabic: 'الحج مرة واحدة، فمن زاد فهو نافلة',
        english: 'Hajj is once in a lifetime. Whoever does more than that is voluntary.',
        reference: 'Sahih al-Bukhari, Book 26, Hadith 1513',
        full_arabic_hadith: 'عن أبي هريرة رضي الله عنه قال: خطبنا رسول الله صلى الله عليه وسلم فقال: يا أيها الناس، قد فرض الله عليكم الحج فحجوا. فقال رجل: أكل عام يا رسول الله؟ فسكت، حتى قالها ثلاثا. فقال رسول الله صلى الله عليه وسلم: لو قلت نعم لوجبت، ولما استطعتم. الحج مرة واحدة، فمن زاد فهو نافلة',
      },
      kushtet: {
        albanian: legacyObligation.kushtet,
        arabic: 'الحج يجب أن يكون خالصاً لله، ويؤدى على سنة رسول الله صلى الله عليه وسلم',
        english: 'Hajj must be purely for Allah and performed according to the Sunnah of the Messenger of Allah',
        quranic_reference: 'وَمَا أُمِرُوا۟ إِلَّا لِيَعْبُدُوا۟ ٱللَّهَ مُخْلِصِينَ لَهُ ٱلدِّينَ حُنَفَآءَ',
      },
    };
  }

  private transformTravelEtiquette(legacyEtiquette: any[]) {
    return legacyEtiquette.map((item, index) => ({
      id: item.id || index + 1,
      rule: {
        albanian: item.rule,
        arabic: this.getTravelEtiquetteArabic(item.rule),
        english: this.getTravelEtiquetteEnglish(item.rule),
      },
      description: {
        albanian: item.description,
        arabic: this.getTravelEtiquetteDescriptionArabic(item.rule),
        english: this.getTravelEtiquetteDescriptionEnglish(item.rule),
      },
      order: index + 1,
    }));
  }

  private transformIhramInfo(legacyIhram: any) {
    return {
      description: {
        albanian: legacyIhram.description,
        arabic: 'الإحرام هو نية المسلمان لأداء مناسك الحج أو العمرة',
        english: 'Ihram is the intention of a Muslim to perform the rites of Hajj or Umrah',
      },
      koha: {
        albanian: legacyIhram.koha,
        arabic: 'نية الحج تكون في أشهر الحج: شوال، وذو القعدة، وذو الحجة',
        english: 'The intention for Hajj is made in the months of Hajj: Shawwal, Dhul-Qa\'dah, and Dhul-Hijjah',
        quranic_reference: 'ٱلْحَجُّ أَشْهُرٌ مَّعْلُومَٰتٌ',
      },
      llojet_e_nijetit: (legacyIhram.llojet_e_nijetit || []).map((item: any) => ({
        lloji: {
          albanian: item.lloji,
          arabic: this.getIntentionTypeArabic(item.lloji),
          english: this.getIntentionTypeEnglish(item.lloji),
        },
        nijeti: {
          albanian: item.nijeti,
          arabic: this.getIntentionArabic(item.nijeti),
          english: this.getIntentionEnglish(item.nijeti),
          transliteration: this.getIntentionTransliteration(item.nijeti),
        },
      })),
      talbiyyah_complete: {
        albanian: 'Talbiyyah e plotë',
        arabic: 'التلبية الكاملة',
        english: 'Complete Talbiyah',
        arabic_text: 'لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ، لَبَّيْكَ لَا شَرِيكَ لَكَ لَبَّيْكَ، إِنَّ الْحَمْدَ وَالنِّعْمَةَ لَكَ وَالْمُلْكَ، لَا شَرِيكَ لَكَ',
        transliteration: 'Labbayk Allahumma labbayk, labbayka la sharika laka labbayk, innal-hamda wan-ni\'mata laka wal-mulk, la sharika lak',
        english_translation: 'Here I am at Your service, O Allah, here I am at Your service. Here I am at Your service, You have no partner, here I am at Your service. Verily all praise and blessings are Yours, and all sovereignty. You have no partner.',
        hadith_reference: 'Sahih al-Bukhari, Book 26, Hadith 1549',
      },
      para_veshjes: (legacyIhram.para_veshjes || []).map((item: any, index: number) => ({
        id: item.id || index + 1,
        veprim: {
          albanian: item.veprim,
          arabic: this.getPreIhramActionArabic(item.veprim),
          english: this.getPreIhramActionEnglish(item.veprim),
        },
        description: {
          albanian: item.description,
          arabic: this.getPreIhramDescriptionArabic(item.veprim),
          english: this.getPreIhramDescriptionEnglish(item.veprim),
        },
        order: index + 1,
      })),
    };
  }

  private transformProhibitions(legacyProhibitions: any[]) {
    return legacyProhibitions.map((item, index) => ({
      id: item.id || index + 1,
      ndalesa: {
        albanian: item.ndalesa,
        arabic: this.getProhibitionArabic(item.ndalesa),
        english: this.getProhibitionEnglish(item.ndalesa),
      },
      description: {
        albanian: item.description,
        arabic: this.getProhibitionDescriptionArabic(item.ndalesa),
        english: this.getProhibitionDescriptionEnglish(item.ndalesa),
      },
      evidence: {
        quranic: 'يَا أَيُّهَا ٱلَّذِينَ ءَامَنُوا۟ لَا تُحِلُّوا۟ شَيْـَٰٓئَاتِ ٱلصَّيْدِ وَأَنتُمْ حُرُمٌ',
        hadith: 'Sahih al-Bukhari, Book 26, Hadith 615-617',
      },
      order: index + 1,
    }));
  }

  private transformMiqat(legacyMiqat: any[]) {
    return legacyMiqat.map((item, index) => ({
      id: item.id || index + 1,
      emri: {
        albanian: item.emri,
        arabic: this.getMiqatArabicName(item.emri),
        english: this.getMiqatEnglishName(item.emri),
      },
      per_ke: {
        albanian: item.per_ke,
        arabic: this.getMiqatForWhomArabic(item.per_ke),
        english: this.getMiqatForWhomEnglish(item.per_ke),
      },
      largesia: {
        albanian: item.largesia,
        arabic: this.getMiqatDistanceArabic(item.largesia),
        english: this.getMiqatDistanceEnglish(item.largesia),
      },
      coordinates: this.getMiqatCoordinates(item.emri),
      image: item.image,
      order: index + 1,
    }));
  }

  // Helper methods for translations (these would ideally come from a translation service)
  private getPillarArabicName(name: string): string {
    const translations: Record<string, string> = {
      'Dëshmia': 'الشهادتان',
      'Falja e namazit': 'الصلاة',
      'Dhënja e Zekatit': 'الزكاة',
      'Agjërimi i Ramazanit': 'صوم رمضان',
      'Haxhi në Qabe': 'حج البيت',
    };
    return translations[name] || name;
  }

  private getPillarEnglishName(name: string): string {
    const translations: Record<string, string> = {
      'Dëshmia': 'The Testimony',
      'Falja e namazit': 'Prayer',
      'Dhënja e Zekatit': 'Zakat',
      'Agjërimi i Ramazanit': 'Fasting in Ramadan',
      'Haxhi në Qabe': 'Hajj to the House',
    };
    return translations[name] || name;
  }

  private getPillarArabicDescription(name: string): string {
    const descriptions: Record<string, string> = {
      'Dëshmia': 'لا إله إلا الله، محمد رسول الله',
      'Falja e namazit': 'أداء الصلوات الخمس المفروضة في اليوم والليلة',
      'Dhënja e Zekatit': 'إخراج جزء من المال للمحتاجين والمساكين',
      'Agjërimi i Ramazanit': 'الإمساك عن الطعام والشراب والشهوة من الفجر إلى الغروب في شهر رمضان',
      'Haxhi në Qabe': 'زيارة الكعبة الشريفة مرة واحدة في العمر على الأقل للقادرين',
    };
    return descriptions[name] || '';
  }

  private getPillarEnglishDescription(name: string): string {
    const descriptions: Record<string, string> = {
      'Dëshmia': 'There is no god but Allah, and Muhammad is the Messenger of Allah.',
      'Falja e namazit': 'Performing the five obligatory prayers daily and nightly.',
      'Dhënja e Zekatit': 'Giving a portion of wealth to the needy and poor.',
      'Agjërimi i Ramazanit': 'Abstaining from food, drink, and desires from dawn to sunset in Ramadan.',
      'Haxhi në Qabe': 'Visiting the Holy Kaaba at least once in a lifetime for those who are able.',
    };
    return descriptions[name] || '';
  }

  private getPillarQuranicReference(name: string): string {
    const references: Record<string, string> = {
      'Dëshmia': 'شَهِدَ ٱللَّهُ أَنَّهُۥ لَا إِلَٰهَ إِلَّا هُوَ',
      'Falja e namazit': 'إِنَّ ٱلصَّلَوٰةَ كَانَتْ عَلَى ٱلْمُؤْمِنِينَ كِتَٰبًا مَّوْقُوتًا',
      'Dhënja e Zekatit': 'خُذْ مِنْ أَمْوَٰلِهِمْ صَدَقَةً تُطَهِّرُهُمْ وَتُزَكِّيهِم بِهَا',
      'Agjërimi i Ramazanit': 'يَا أَيُّهَا ٱلَّذِينَ ءَامَنُوا۟ كُتِبَ عَلَيْكُمُ ٱلصِّيَامُ',
      'Haxhi në Qabe': 'وَلِلَّهِ عَلَى ٱلنَّاسِ حِجُّ ٱلْبَيْتِ مَنِ ٱسْتَطَاعَ إِلَيْهِ سَبِيلًا',
    };
    return references[name] || '';
  }

  // Add more translation helper methods as needed...
  private getTravelEtiquetteArabic(rule: string): string {
    // This would be implemented with actual translations
    return 'يجب أن تكون النية خالصة لله';
  }

  private getTravelEtiquetteEnglish(rule: string): string {
    // This would be implemented with actual translations
    return 'The intention must be purely for Allah';
  }

  private getTravelEtiquetteDescriptionArabic(rule: string): string {
    return 'لا لإظهار أو لأغراض أخرى';
  }

  private getTravelEtiquetteDescriptionEnglish(rule: string): string {
    return 'Not for showing off or for other purposes';
  }

  private getIntentionsTypeArabic(type: string): string {
    return type === 'Umra' ? 'العمرة' : 'الحج';
  }

  private getIntentionsTypeEnglish(type: string): string {
    return type === 'Umra' ? 'Umrah' : 'Hajj';
  }

  private getIntentionsArabic(nijeti: string): string {
    return 'لَبَّيْكَ عُمْرَةً';
  }

  private getIntentionsEnglish(nijeti: string): string {
    return 'Labbayk Umratan';
  }

  private getIntentionsTransliteration(nijeti: string): string {
    return 'Labbayk Allahumma Umratan';
  }

  private getPreIhramActionArabic(action: string): string {
    return 'تقليم الأظافر وتقصير الشارب';
  }

  private getPreIhramActionEnglish(action: string): string {
    return 'Trimming nails and mustache';
  }

  private getPreIhramDescriptionArabic(action: string): string {
    return 'تقليم الأظافر، وتقصير الشارب، وحلق شعر الإبطين والعانة';
  }

  private getPreIhramDescriptionEnglish(action: string): string {
    return 'Trimming nails, cutting mustache, removing underarm and pubic hair';
  }

  private getProhibitionArabic(prohibition: string): string {
    return 'تغطية الرأس';
  }

  private getProhibitionEnglish(prohibition: string): string {
    return 'Covering the head';
  }

  private getProhibitionDescriptionArabic(prohibition: string): string {
    return 'لا يجوز للرجال تغطية الرأس';
  }

  private getProhibitionDescriptionEnglish(prohibition: string): string {
    return 'It is not permissible for men to cover their heads';
  }

  private getMiqatArabicName(emri: string): string {
    return 'ذو الحليفة';
  }

  private getMiqatEnglishName(emri: string): string {
    return 'Dhul Hulaifah';
  }

  private getMiqatForWhomArabic(per_ke: string): string {
    return 'لسكان المدينة والقادمين من طريقها';
  }

  private getMiqatForWhomEnglish(per_ke: string): string {
    return 'For residents of Medina and those coming from that route';
  }

  private getMiqatDistanceArabic(largesia: string): string {
    return '450 كم من مكة';
  }

  private getMiqatDistanceEnglish(largesia: string): string {
    return '450 km from Mecca';
  }

  private getMiqatCoordinates(emri: string): { latitude: number; longitude: number } | undefined {
    const coordinates: Record<string, { latitude: number; longitude: number }> = {
      'DHUL HULEJFEH': { latitude: 24.4475, longitude: 39.5853 },
      'XHUHFEH': { latitude: 23.3206, longitude: 38.2007 },
      'KARNUL MENAZIL': { latitude: 21.3808, longitude: 40.4169 },
      'JELEMLEM': { latitude: 20.9424, longitude: 41.0186 },
      'DHATE IRK': { latitude: 22.0472, longitude: 42.2977 },
    };
    return coordinates[emri];
  }

  // Public API methods
  public async getCategories(): Promise<Category[]> {
    if (!this.hajjData) {
      await this.loadHajjData();
    }

    const categories: Category[] = [
      {
        id: 1,
        name: CATEGORIES.PILLARS_OF_ISLAM,
        title: {
          albanian: 'Shtyllat e Islamit',
          arabic: 'أركان الإسلام',
          english: 'Pillars of Islam',
        },
        description: {
          albanian: 'Pesë shtyllat themelore të fesë islame',
          arabic: 'الأركان الخمسة الأساسية للإسلام',
          english: 'The five fundamental pillars of Islam',
        },
        image: 'pillars_of_islam.png',
        icon: 'account-balance',
        order: 1,
        color: '#2E7D32',
        featured: true,
      },
      {
        id: 2,
        name: CATEGORIES.TRAVEL_ETIQUETTE,
        title: {
          albanian: 'Edukata e Udhëtimit',
          arabic: 'آداب السفر',
          english: 'Travel Etiquette',
        },
        description: {
          albanian: 'Rregullat dhe edukativa para dhe gjatë udhëtimit për Haxh',
          arabic: 'القواعد والآداب قبل وأثناء السفر للحج',
          english: 'Rules and etiquette before and during travel for Hajj',
        },
        image: 'travel_etiquette.png',
        icon: 'luggage',
        order: 2,
        color: '#1976D2',
      },
      {
        id: 3,
        name: CATEGORIES.IHRAM,
        title: {
          albanian: 'Ihrami',
          arabic: 'الإحرام',
          english: 'Ihram',
        },
        description: {
          albanian: 'Rregullat dhe kërkesat për ihramin',
          arabic: 'قواعد ومتطلبات الإحرام',
          english: 'Rules and requirements for Ihram',
        },
        image: 'ihram.png',
        icon: 'verified',
        order: 3,
        color: '#F57C00',
      },
      {
        id: 4,
        name: CATEGORIES.IHRAM_PROHIBITIONS,
        title: {
          albanian: 'Ndalesat gjatë Ihramit',
          arabic: 'محظورات الإحرام',
          english: 'Ihram Prohibitions',
        },
        description: {
          albanian: 'Gjërat që janë të ndaluara gjatë gjendjes së ihramit',
          arabic: 'الأشياء المحظورة خلال حالة الإحرام',
          english: 'Things that are forbidden during the state of Ihram',
        },
        image: 'prohibitions.png',
        icon: 'block',
        order: 4,
        color: '#D32F2F',
      },
      {
        id: 5,
        name: CATEGORIES.MIQAT,
        title: {
          albanian: 'Vendcaktimet (Miqat)',
          arabic: 'المواقيت',
          english: 'Miqat (Boundaries)',
        },
        description: {
          albanian: 'Vendcaktimet ku bëhet ihram-i',
          arabic: 'الأماكن التي يتم فيها الإحرام',
          english: 'The designated places where Ihram is made',
        },
        image: 'miqats.jpg',
        icon: 'place',
        order: 5,
        color: '#7B1FA2',
      },
    ];

    return categories;
  }

  public async getCategory(id: string): Promise<Category | undefined> {
    const categories = await this.getCategories();
    return categories.find(cat => cat.name === id);
  }

  public async getRulesByCategory(categoryName: string): Promise<Rule[]> {
    if (!this.hajjData) {
      await this.loadHajjData();
    }

    const cacheKey = `rules_${categoryName}`;
    const cached = this.cache.get(cacheKey);
    if (cached) return cached;

    let rules: Rule[] = [];

    switch (categoryName) {
      case CATEGORIES.PILLARS_OF_ISLAM:
        rules = this.hajjData!.shtyllat_e_islamit.map((pillar, index) => ({
          id: generateId('pillar_'),
          title: pillar.name,
          description: pillar.description,
          category: categoryName,
          order: pillar.order,
          evidence: {
            quranic: [pillar.description.quranic_reference].filter(Boolean),
            hadith: pillar.description.hadith_reference ? [pillar.description.hadith_reference] : [],
          },
        }));
        break;

      case CATEGORIES.TRAVEL_ETIQUETTE:
        rules = this.hajjData!.edukata_e_udhetimit.map((item, index) => ({
          id: generateId('travel_'),
          title: item.rule,
          description: item.rule,
          category: categoryName,
          order: item.order,
        }));
        break;

      case CATEGORIES.IHRAM:
        rules = this.hajjData!.ihrami.para_veshjes.map((action, index) => ({
          id: generateId('ihram_'),
          title: action.veprim,
          description: action.description,
          category: categoryName,
          order: action.order,
        }));
        break;

      case CATEGORIES.IHRAM_PROHIBITIONS:
        rules = this.hajjData!.ndalesat_gjate_ihramit.map((prohibition, index) => ({
          id: generateId('prohibition_'),
          title: prohibition.ndalesa,
          description: prohibition.description,
          category: categoryName,
          order: prohibition.order,
          evidence: prohibition.evidence ? {
            quranic: prohibition.evidence.quranic ? [prohibition.evidence.quranic] : [],
            hadith: prohibition.evidence.hadith ? [prohibition.evidence.hadith] : [],
          } : undefined,
        }));
        break;

      case CATEGORIES.MIQAT:
        rules = this.hajjData!.vendcaktimet.map((miqat, index) => ({
          id: generateId('miqat_'),
          title: miqat.emri,
          description: {
            albanian: `${miqat.per_ke.albanian} - ${miqat.largesia.albanian}`,
            arabic: `${miqat.per_ke.arabic} - ${miqat.largesia.arabic}`,
            english: `${miqat.per_ke.english} - ${miqat.largesia.english}`,
          },
          category: categoryName,
          order: miqat.order,
          image: miqat.image,
        }));
        break;
    }

    this.cache.set(cacheKey, rules);
    return rules;
  }

  public async getAllRules(): Promise<Rule[]> {
    const categories = await this.getCategories();
    const allRules: Rule[] = [];

    for (const category of categories) {
      const rules = await this.getRulesByCategory(category.name);
      allRules.push(...rules);
    }

    return allRules.sort((a, b) => a.order - b.order);
  }

  public async searchRules(query: string): Promise<Rule[]> {
    if (!this.currentSettings) {
      throw new Error('ContentService not initialized with settings');
    }

    const allRules = await this.getAllRules();

    return allRules.filter(rule => {
      const titleText = getLocalizedTextWithFallback(rule.title, this.currentSettings!);
      const descText = getLocalizedTextWithFallback(rule.description, this.currentSettings!);

      const normalizedQuery = query.toLowerCase().trim();

      return (
        titleText.toLowerCase().includes(normalizedQuery) ||
        descText.toLowerCase().includes(normalizedQuery)
      );
    });
  }

  public async getHajjData(): Promise<HajjData> {
    if (!this.hajjData) {
      await this.loadHajjData();
    }
    return this.hajjData!;
  }

  /**
   * Clear all cached data
   */
  public clearCache(): void {
    this.cache.clear();
  }

  /**
   * Force refresh of Hajj data from storage
   */
  public async refresh(): Promise<void> {
    this.cache.clear();
    this.hajjData = null;
    await this.loadHajjData();
  }
}

export default ContentService.getInstance();