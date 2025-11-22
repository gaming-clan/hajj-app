export interface MultilingualString {
  albanian: string;
  arabic: string;
  english: string;
  quranic_reference?: string;
  hadith_reference?: string;
  transliteration?: string;
}

export interface MultilingualTitle {
  albanian: string;
  arabic: string;
  english: string;
}

export interface MultilingualContent {
  albanian: string;
  arabic: string;
  english: string;
}

export interface MultilingualDescription {
  albanian: string;
  arabic: string;
  english: string;
  quranic_reference?: string;
}

// Enhanced Hajj-related interfaces with multilingual support
export interface PillarOfIslam {
  id: number;
  name: MultilingualTitle;
  description: MultilingualDescription;
  order: number;
}

export interface TravelEtiquette {
  id: number;
  rule: MultilingualString;
  description: MultilingualString;
  order: number;
}

export interface IhramIntention {
  lloji: MultilingualTitle; // type
  nijeti: {
    albanian: string;
    arabic: string;
    english: string;
    transliteration: string;
  };
}

export interface PreIhramAction {
  id: number;
  veprim: MultilingualTitle; // action
  description: MultilingualContent;
  order: number;
}

export interface IhramProhibition {
  id: number;
  ndalesa: MultilingualTitle; // prohibition
  description: MultilingualContent;
  evidence?: {
    quranic?: string;
    hadith?: string;
  };
  order: number;
}

export interface Miqat {
  id: number;
  emri: {
    albanian: string;
    arabic: string;
    english: string;
  };
  per_ke: MultilingualContent; // for whom
  largesia: {
    albanian: string;
    arabic: string;
    english: string;
  };
  coordinates?: {
    latitude: number;
    longitude: number;
  };
  image?: string;
  order: number;
}

export interface Introduction {
  description: MultilingualDescription;
  qabja: MultilingualDescription;
}

export interface HajjObligation {
  description: MultilingualContent;
  hadith: {
    albanian: string;
    arabic: string;
    english: string;
    reference: string;
    full_arabic_hadith?: string;
  };
  kushtet: MultilingualDescription;
}

export interface IhramInfo {
  description: MultilingualContent;
  koha: {
    albanian: string;
    arabic: string;
    english: string;
    quranic_reference?: string;
  };
  llojet_e_nijetit: IhramIntention[];
  talbiyyah_complete?: {
    albanian: string;
    arabic: string;
    english: string;
    arabic_text: string;
    transliteration: string;
    english_translation: string;
    hadith_reference: string;
  };
  para_veshjes: PreIhramAction[];
}

export interface KaabaInfo {
  description: MultilingualDescription;
  rendesia: MultilingualContent;
}

export interface HajjImportance {
  description: MultilingualContent;
  hadith: {
    albanian: string;
    arabic: string;
    english: string;
  };
  keshilla: MultilingualContent;
}

// Main data structure
export interface HajjData {
  title: MultilingualTitle;
  introduction: Introduction;
  shtyllat_e_islamit: PillarOfIslam[];
  detyrimi_i_haxhit: HajjObligation;
  edukata_e_udhetimit: TravelEtiquette[];
  ihrami: IhramInfo;
  ndalesat_gjate_ihramit: IhramProhibition[];
  vendcaktimet: Miqat[];
  qabja: KaabaInfo;
  rendesia_e_haxhit: HajjImportance;
}

// Enhanced category interface
export interface Category {
  id: number;
  name: string; // This will be the key for data access
  title: MultilingualTitle;
  description: MultilingualDescription;
  image?: string;
  icon?: string;
  order: number;
  color?: string;
  featured?: boolean;
}

export interface Rule {
  id: string;
  title: MultilingualTitle;
  description: MultilingualDescription;
  category: string;
  image?: string;
  order: number;
  evidence?: {
    quranic?: string[];
    hadith?: string[];
  };
}

// App state and settings
export interface AppSettings {
  language: 'albanian' | 'arabic' | 'english';
  theme: 'light' | 'dark';
  fontSize: 'small' | 'medium' | 'large';
  showArabicText: boolean;
  showTransliteration: boolean;
  autoPlayAudio: boolean;
  notifications: boolean;
  rtl: boolean;
}

// Navigation types
export interface RootStackParamList {
  Splash: undefined;
  Main: undefined;
  Detail: {
    item: Rule | Category;
    type: 'rule' | 'category';
  };
  Search: undefined;
  Settings: undefined;
  Content: {
    category: string;
    title: MultilingualTitle;
  };
  Qibla: undefined;
  Tracker: undefined;
}

export interface MainTabParamList {
  Home: undefined;
  Categories: undefined;
  Content: undefined;
  Qibla: undefined;
  Settings: undefined;
}

// UI Component Props
export interface ContentCardProps {
  item: Rule | Category;
  onPress: (item: Rule | Category) => void;
  language?: AppSettings['language'];
  showArabic?: boolean;
  showTransliteration?: boolean;
}

export interface SearchResultProps {
  query: string;
  results: Rule[];
  onResultPress: (rule: Rule) => void;
  language?: AppSettings['language'];
}

// API and Service types
export interface DataServiceInterface {
  getCategories(): Promise<Category[]>;
  getCategory(id: string): Promise<Category | undefined>;
  getRulesByCategory(categoryName: string): Promise<Rule[]>;
  getAllRules(): Promise<Rule[]>;
  searchRules(query: string): Promise<Rule[]>;
  getHajjData(): Promise<HajjData>;
}

// Validation types
export interface ValidationError {
  field: string;
  message: string;
  code: string;
}

export interface ValidationResult {
  isValid: boolean;
  errors: ValidationError[];
}

// Analytics types
export interface AnalyticsEvent {
  event: string;
  properties?: Record<string, any>;
  timestamp: Date;
}

export interface UserInteraction {
  type: 'view' | 'search' | 'share' | 'favorite';
  itemId: string;
  itemType: 'rule' | 'category';
  timestamp: Date;
  duration?: number;
}

// Export all types for convenience
export type {
  MultilingualString,
  MultilingualTitle,
  MultilingualContent,
  MultilingualDescription,
  RootStackParamList,
  MainTabParamList,
};