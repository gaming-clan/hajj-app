import { useState, useEffect } from 'react';
import { Platform, AccessibilityInfo, I18nManager } from 'react-native';

interface AccessibilitySettings {
  reduceMotion: boolean;
  screenReaderEnabled: boolean;
  highContrastMode: boolean;
  largeTextSize: boolean;
  rtlLayout: boolean;
  fontSizeScale: number;
}

/**
 * Hook for accessibility settings and utilities
 * Provides support for screen readers, high contrast, and RTL layouts
 */
export const useAccessibility = () => {
  const [settings, setSettings] = useState<AccessibilitySettings>({
    reduceMotion: false,
    screenReaderEnabled: false,
    highContrastMode: false,
    largeTextSize: false,
    rtlLayout: I18nManager.isRTL,
    fontSizeScale: 1.0,
  });

  useEffect(() => {
    checkAccessibilitySettings();

    const listeners = [
      AccessibilityInfo.addEventListener(
        'reduceMotionChanged',
        (enabled) => updateSetting('reduceMotion', enabled)
      ),
      AccessibilityInfo.addEventListener(
        'screenReaderChanged',
        (enabled) => updateSetting('screenReaderEnabled', enabled)
      ),
      AccessibilityInfo.addEventListener(
        'highContrastChanged',
        (enabled) => updateSetting('highContrastMode', enabled)
      ),
    ];

    return () => {
      listeners.forEach(listener => listener.remove());
    };
  }, []);

  const checkAccessibilitySettings = async () => {
    try {
      const [
        reduceMotion,
        screenReader,
        highContrast,
        fontSizeScale,
      ] = await Promise.all([
        AccessibilityInfo.isReduceMotionEnabled(),
        AccessibilityInfo.isScreenReaderEnabled(),
        AccessibilityInfo.isHighContrastEnabled(),
        AccessibilityInfo.preferredFontScale(),
      ]);

      setSettings(prev => ({
        ...prev,
        reduceMotion,
        screenReaderEnabled: screenReader,
        highContrastMode: highContrast,
        fontSizeScale,
        largeTextSize: fontSizeScale > 1.0,
      }));
    } catch (error) {
      console.warn('Failed to check accessibility settings:', error);
    }
  };

  const updateSetting = (key: keyof AccessibilitySettings, value: boolean | number) => {
    setSettings(prev => ({
      ...prev,
      [key]: value,
    }));
  };

  const toggleRTL = () => {
    const newRTLState = !settings.rtlLayout;
    I18nManager.forceRTL(newRTLState);
    updateSetting('rtlLayout', newRTLState);
  };

  const announceForAccessibility = (message: string) => {
    if (settings.screenReaderEnabled) {
      AccessibilityInfo.announceForAccessibility(message);
    }
  };

  const getAccessibilityStyles = () => {
    return {
      // Reduce animations if requested
      animationDuration: settings.reduceMotion ? 0 : undefined,

      // High contrast colors
      colorScheme: settings.highContrastMode ? 'highContrast' : 'default',

      // Font size scaling
      fontSizeMultiplier: settings.fontSizeScale,

      // Text direction
      textAlign: settings.rtlLayout ? 'right' : 'left',

      // RTL layout
      flexDirection: settings.rtlLayout ? 'row-reverse' : 'row',
    };
  };

  const getAccessibleProps = (props: {
    label?: string;
    hint?: string;
    role?: string;
    isButton?: boolean;
    isHeader?: boolean;
  }) => {
    const accessibilityProps: any = {
      accessible: true,
    };

    if (props.label) {
      accessibilityProps.accessibilityLabel = props.label;
    }

    if (props.hint) {
      accessibilityProps.accessibilityHint = props.hint;
    }

    if (props.role) {
      accessibilityProps.accessibilityRole = props.role;
    } else if (props.isButton) {
      accessibilityProps.accessibilityRole = 'button';
    } else if (props.isHeader) {
      accessibilityProps.accessibilityRole = 'header';
    }

    return accessibilityProps;
  };

  const isRTL = () => settings.rtlLayout;

  const shouldReduceMotion = () => settings.reduceMotion;

  const isScreenReaderActive = () => settings.screenReaderEnabled;

  const isHighContrastMode = () => settings.highContrastMode;

  const getFontScale = () => settings.fontSizeScale;

  return {
    settings,
    toggleRTL,
    announceForAccessibility,
    getAccessibilityStyles,
    getAccessibleProps,
    isRTL,
    shouldReduceMotion,
    isScreenReaderActive,
    isHighContrastMode,
    getFontScale,
  };
};

/**
 * Hook for RTL layout utilities
 */
export const useRTL = () => {
  const { isRTL } = useAccessibility();

  const getRTLStyle = (ltrStyle: any, rtlStyle?: any) => {
    return isRTL() ? rtlStyle || ltrStyle : ltrStyle;
  };

  const getFlexDirection = (defaultDirection: 'row' | 'column' = 'row') => {
    if (defaultDirection === 'row') {
      return isRTL() ? 'row-reverse' : 'row';
    }
    return defaultDirection;
  };

  const getTextAlign = (defaultAlign: 'left' | 'right' | 'center' = 'left') => {
    if (defaultAlign === 'left') {
      return isRTL() ? 'right' : 'left';
    }
    if (defaultAlign === 'right') {
      return isRTL() ? 'left' : 'right';
    }
    return defaultAlign;
  };

  const getMarginDirection = (margin: number) => {
    return isRTL() ? { marginLeft: margin, marginRight: 0 } : { marginLeft: 0, marginRight: margin };
  };

  const getPaddingDirection = (padding: number) => {
    return isRTL() ? { paddingLeft: padding, paddingRight: 0 } : { paddingLeft: 0, paddingRight: padding };
  };

  const getPositionDirection = (position: number) => {
    return isRTL() ? { left: 0, right: position } : { left: position, right: 0 };
  };

  return {
    isRTL: isRTL(),
    getRTLStyle,
    getFlexDirection,
    getTextAlign,
    getMarginDirection,
    getPaddingDirection,
    getPositionDirection,
  };
};

/**
 * Hook for Arabic text utilities
 */
export const useArabicText = () => {
  const { isRTL } = useAccessibility();

  const isArabicText = (text: string): boolean => {
    const arabicRegex = /[\u0600-\u06FF]/;
    return arabicRegex.test(text);
  };

  const containsArabic = (text: string): boolean => {
    return isArabicText(text);
  };

  const getArabicFontFamily = () => {
    return Platform.select({
      ios: 'NotoSansArabic-Regular',
      android: 'Noto Sans Arabic',
    });
  };

  const getArabicTextProps = (text: string) => {
    const hasArabic = containsArabic(text);

    return {
      isRTL: hasArabic || isRTL(),
      fontFamily: hasArabic ? getArabicFontFamily() : undefined,
      textAlign: hasArabic ? 'right' : 'left',
      writingDirection: hasArabic ? 'rtl' : 'ltr',
    };
  };

  const formatArabicNumbers = (text: string): string => {
    const arabicNumbers = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];
    return text.replace(/[0-9]/g, (digit) => arabicNumbers[parseInt(digit)]);
  };

  const removeDiacritics = (text: string): string => {
    const diacriticsRegex = /[\u064B-\u0652]/g;
    return text.replace(diacriticsRegex, '');
  };

  return {
    isArabicText,
    containsArabic,
    getArabicFontFamily,
    getArabicTextProps,
    formatArabicNumbers,
    removeDiacritics,
  };
};

/**
 * Hook for screen reader announcements
 */
export const useScreenReader = () => {
  const { isScreenReaderActive, announceForAccessibility } = useAccessibility();

  const announce = (message: string, delay: number = 0) => {
    if (isScreenReaderActive()) {
      if (delay > 0) {
        setTimeout(() => {
          announceForAccessibility(message);
        }, delay);
      } else {
        announceForAccessibility(message);
      }
    }
  };

  const announceNavigation = (screenName: string) => {
    announce(`Navigated to $screenName`);
  };

  const announceError = (error: string) => {
    announce(`Error: $error`);
  };

  const announceSuccess = (success: string) => {
    announce(`Success: $success`);
  };

  const announceLoading = () => {
    announce('Loading content');
  };

  const announceCompletion = (task: string) => {
    announce(`$task completed`);
  };

  return {
    isActive: isScreenReaderActive(),
    announce,
    announceNavigation,
    announceError,
    announceSuccess,
    announceLoading,
    announceCompletion,
  };
};