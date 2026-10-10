export interface HajjRule {
  id: number;
  rule: string;
  description: string;
  category?: string;
  image?: string;
}

export interface Category {
  id: number;
  name: string;
  title: string;
  description: string;
  image?: string;
  rules?: HajjRule[];
}

export interface PillarOfIslam {
  id: number;
  name: string;
  description: string;
}

export interface TravelEtiquette {
  id: number;
  rule: string;
  description: string;
}

export interface IhramType {
  lloji: string;
  nijeti: string;
}

export interface PreIhramAction {
  id: number;
  veprim: string;
  description: string;
}

export interface IhramProhibition {
  id: number;
  ndalesa: string;
  description: string;
}

export interface Miqat {
  id: number;
  emri: string;
  per_ke: string;
  largesia: string;
  image?: string;
}

export interface HajjData {
  title: string;
  introduction: {
    description: string;
    qabja: string;
  };
  shtyllat_e_islamit: PillarOfIslam[];
  detyrimi_i_haxhit: {
    description: string;
    hadith: string;
    kushtet: string;
  };
  edukata_e_udhetimit: TravelEtiquette[];
  ihrami: {
    description: string;
    koha: string;
    llojet_e_nijetit: IhramType[];
    para_veshjes: PreIhramAction[];
  };
  ndalesat_gjate_ihramit: IhramProhibition[];
  vendcaktimet: Miqat[];
}

export interface NavigationStackParamList {
  Splash: undefined;
  Main: undefined;
  Detail: {
    rule?: HajjRule;
    category?: Category;
    title: string;
  };
  Search: undefined;
}

export interface TabParamList {
  Home: undefined;
  Categories: undefined;
  Map: undefined;
  About: undefined;
}
