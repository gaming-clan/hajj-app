import React from 'react';
import { Text, TextProps, StyleSheet, Platform } from 'react-native';
import { useTheme } from '@react-navigation/native';

interface AccessibleTextProps extends TextProps {
  // Accessibility props
  accessibilityLabel?: string;
  accessibilityHint?: string;
  accessibilityRole?: 'header' | 'text' | 'link' | 'button' | 'search';
  isHeading?: boolean;

  // RTL support
  isArabic?: boolean;
  forceLTR?: boolean;

  // Visual customization
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | '2xl';
  weight?: 'normal' | 'medium' | 'semibold' | 'bold';
  color?: 'primary' | 'secondary' | 'accent' | 'error' | 'success';

  // Text direction
  textAlign?: 'auto' | 'left' | 'right' | 'center' | 'justify';
}

/**
 * Accessible text component with RTL support for Arabic content
 * Provides proper accessibility labels and visual styling
 */
export const AccessibleText: React.FC<AccessibleTextProps> = ({
  children,
  accessibilityLabel,
  accessibilityHint,
  accessibilityRole = 'text',
  isHeading = false,
  isArabic = false,
  forceLTR = false,
  size = 'md',
  weight = 'normal',
  color,
  textAlign,
  style,
  ...props
}) => {
  const theme = useTheme();

  // Determine text direction
  const getTextDirection = () => {
    if (forceLTR) return 'ltr';
    if (isArabic) return 'rtl';
    return 'auto';
  };

  // Get text alignment based on direction
  const getTextAlign = () => {
    if (textAlign) return textAlign;
    if (isArabic) return 'right';
    return 'left';
  };

  // Get font size
  const getFontSize = () => {
    const sizes = {
      xs: 12,
      sm: 14,
      md: 16,
      lg: 18,
      xl: 24,
      '2xl': 32,
    };
    return sizes[size];
  };

  // Get font weight
  const getFontWeight = () => {
    const weights = {
      normal: '400',
      medium: '500',
      semibold: '600',
      bold: '700',
    };
    return weights[weight];
  };

  // Get color based on theme and color prop
  const getColor = () => {
    const themeColors = theme.colors as any;
    if (color) {
      switch (color) {
        case 'primary': return themeColors.primary;
        case 'secondary': return themeColors.text;
        case 'accent': return themeColors.accent;
        case 'error': return themeColors.error;
        case 'success': return themeColors.success;
        default: return themeColors.text;
      }
    }
    return themeColors.text;
  };

  // Get font family for Arabic vs other languages
  const getFontFamily = () => {
    if (isArabic) {
      return Platform.select({
        ios: 'NotoSansArabic-Regular',
        android: 'Noto Sans Arabic',
      });
    }
    return Platform.select({
      ios: 'System',
      android: 'Roboto',
    });
  };

  // Generate accessibility properties
  const getAccessibilityProps = () => {
    const accessibilityProps: any = {
      accessible: true,
    };

    if (accessibilityLabel) {
      accessibilityProps.accessibilityLabel = accessibilityLabel;
    }

    if (accessibilityHint) {
      accessibilityProps.accessibilityHint = accessibilityHint;
    }

    if (isHeading) {
      accessibilityProps.accessibilityRole = 'header';
    } else if (accessibilityRole !== 'text') {
      accessibilityProps.accessibilityRole = accessibilityRole;
    }

    return accessibilityProps;
  };

  const computedStyle = StyleSheet.flatten([
    {
      fontSize: getFontSize(),
      fontWeight: getFontWeight(),
      color: getColor(),
      fontFamily: getFontFamily(),
      textAlign: getTextAlign(),
      writingDirection: getTextDirection() as any,
      lineHeight: getFontSize() * 1.5,
    },
    // RTL-specific styles
    isArabic && {
      letterSpacing: 0.5, // Better spacing for Arabic text
    },
    // Heading styles
    isHeading && {
      fontSize: getFontSize() * 1.5,
      fontWeight: 'bold',
      marginBottom: 8,
    },
    style,
  ]);

  return (
    <Text
      style={computedStyle}
      {...getAccessibilityProps()}
      {...props}
    >
      {children}
    </Text>
  );
};

// Specialized components for common use cases

export const ArabicText: React.FC<Omit<AccessibleTextProps, 'isArabic'>> = (props) => (
  <AccessibleText isArabic {...props} />
);

export const Heading: React.FC<Omit<AccessibleTextProps, 'isHeading'>> = (props) => (
  <AccessibleText isHeading {...props} />
);

export const QuranicVerse: React.FC<AccessibleTextProps> = ({
  children,
  ...props
}) => (
  <AccessibleText
    isArabic
    size="lg"
    weight="semibold"
    color="primary"
    accessibilityRole="text"
    accessibilityHint="Quranic verse"
    {...props}
  >
    {children}
  </AccessibleText>
);

export const HadithText: React.FC<AccessibleTextProps> = ({
  children,
  ...props
}) => (
  <AccessibleText
    size="md"
    weight="normal"
    accessibilityRole="text"
    accessibilityHint="Hadith text"
    {...props}
  >
    {children}
  </AccessibleText>
);

export default AccessibleText;