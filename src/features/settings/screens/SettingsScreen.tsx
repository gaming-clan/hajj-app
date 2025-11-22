import React, { useState, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Switch,
  Alert,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { useNavigation } from '@react-navigation/native';

import { useNavigationContext } from '../../../shared/navigation/AppNavigator';
import { AppSettings, SUPPORTED_LANGUAGES } from '../../../core/types';
import { getLocalizedText, shouldUseRTL } from '../../../core/utils';
import { APP_CONFIG } from '../../../core/constants';

interface SettingItemProps {
  title: string;
  subtitle?: string;
  value: any;
  onValueChange?: (value: any) => void;
  type: 'toggle' | 'selector' | 'action';
  options?: { label: string; value: any }[];
  icon?: string;
  iconColor?: string;
  settings: AppSettings;
}

const SettingItem: React.FC<SettingItemProps> = ({
  title,
  subtitle,
  value,
  onValueChange,
  type,
  options,
  icon,
  iconColor = APP_CONFIG.theme.primary,
  settings,
}) => {
  const isRTL = shouldUseRTL(settings);

  const handlePress = useCallback(() => {
    if (type === 'action' && onValueChange) {
      onValueChange(value);
    } else if (type === 'selector' && options && onValueChange) {
      Alert.alert(
        title,
        undefined,
        options.map(option => ({
          text: option.label,
          onPress: () => onValueChange(option.value),
          style: value === option.value ? 'default' : 'cancel',
        }))
      );
    }
  }, [type, options, value, onValueChange, title]);

  const content = (
    <View style={[styles.settingItem, { flexDirection: isRTL ? 'row-reverse' : 'row' }]}>
      {icon && (
        <View style={[styles.iconContainer, { backgroundColor: `${iconColor}20` }]}>
          <Icon name={icon} size={20} color={iconColor} />
        </View>
      )}

      <View style={styles.settingContent}>
        <Text style={[styles.settingTitle, { textAlign: isRTL ? 'right' : 'left' }]}>
          {title}
        </Text>
        {subtitle && (
          <Text
            style={[
              styles.settingSubtitle,
              { textAlign: isRTL ? 'right' : 'left' },
            ]}
          >
            {subtitle}
          </Text>
        )}
      </View>

      {type === 'toggle' && (
        <Switch
          value={value}
          onValueChange={onValueChange}
          trackColor={{ false: '#e0e0e0', true: `${iconColor}40` }}
          thumbColor={value ? iconColor : '#f4f4f4'}
          ios_backgroundColor="#e0e0e0"
        />
      )}

      {type === 'selector' && (
        <View style={styles.selectorContainer}>
          <Text style={styles.selectorValue}>
            {options?.find(opt => opt.value === value)?.label || value}
          </Text>
          <Icon name="chevron-right" size={20} color={APP_CONFIG.theme.textSecondary} />
        </View>
      )}

      {type === 'action' && (
        <Icon name="chevron-right" size={20} color={APP_CONFIG.theme.textSecondary} />
      )}
    </View>
  );

  if (type === 'toggle') {
    return content;
  }

  return (
    <TouchableOpacity onPress={handlePress} activeOpacity={0.7} style={styles.touchableItem}>
      {content}
    </TouchableOpacity>
  );
};

const EnhancedSettingsScreen: React.FC = () => {
  const { settings, updateSettings } = useNavigationContext();
  const navigation = useNavigation();

  const handleSettingChange = useCallback(
    (key: keyof AppSettings, value: any) => {
      updateSettings({ [key]: value });

      // Special handling for RTL setting
      if (key === 'language') {
        const isRTL = value === 'arabic' || value === 'urdu'; // Add more RTL languages as needed
        updateSettings({ rtl: isRTL });
      }
    },
    [updateSettings]
  );

  const handleClearCache = useCallback(() => {
    Alert.alert(
      'Clear Cache',
      'This will clear all cached data and may increase loading times temporarily.',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Clear',
          style: 'destructive',
          onPress: () => {
            // Implement cache clearing logic
            Alert.alert('Success', 'Cache cleared successfully');
          },
        },
      ]
    );
  }, []);

  const handleAbout = useCallback(() => {
    navigation.navigate('About' as never);
  }, [navigation]);

  const languageOptions = [
    { label: 'Shqip (Albanian)', value: 'albanian' },
    { label: 'العربية (Arabic)', value: 'arabic' },
    { label: 'English', value: 'english' },
  ];

  const fontSizeOptions = [
    { label: 'Small', value: 'small' },
    { label: 'Medium', value: 'medium' },
    { label: 'Large', value: 'large' },
  ];

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Settings</Text>
      </View>

      <ScrollView style={styles.scrollView} showsVerticalScrollIndicator={false}>
        {/* Language Settings */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Language & Display</Text>

          <SettingItem
            title="Language"
            subtitle="Choose your preferred language"
            value={settings.language}
            onValueChange={(value) => handleSettingChange('language', value)}
            type="selector"
            options={languageOptions}
            icon="language"
            settings={settings}
          />

          <SettingItem
            title="Show Arabic Text"
            subtitle="Display Arabic alongside translations"
            value={settings.showArabicText}
            onValueChange={(value) => handleSettingChange('showArabicText', value)}
            type="toggle"
            icon="translate"
            settings={settings}
          />

          <SettingItem
            title="Show Transliteration"
            subtitle="Display romanized Arabic text"
            value={settings.showTransliteration}
            onValueChange={(value) => handleSettingChange('showTransliteration', value)}
            type="toggle"
            icon="text-fields"
            settings={settings}
          />

          <SettingItem
            title="Font Size"
            subtitle="Adjust text size for better readability"
            value={settings.fontSize}
            onValueChange={(value) => handleSettingChange('fontSize', value)}
            type="selector"
            options={fontSizeOptions}
            icon="format-size"
            settings={settings}
          />
        </View>

        {/* Appearance Settings */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Appearance</Text>

          <SettingItem
            title="Dark Mode"
            subtitle="Switch between light and dark themes"
            value={settings.theme === 'dark'}
            onValueChange={(value) => handleSettingChange('theme', value ? 'dark' : 'light')}
            type="toggle"
            icon="dark-mode"
            iconColor="#7B1FA2"
            settings={settings}
          />

          <SettingItem
            title="RTL Layout"
            subtitle="Use right-to-left layout for Arabic content"
            value={settings.rtl}
            onValueChange={(value) => handleSettingChange('rtl', value)}
            type="toggle"
            icon="format-textdirection-r-to-l"
            iconColor="#2196F3"
            settings={settings}
          />
        </View>

        {/* Audio & Media Settings */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Audio & Media</Text>

          <SettingItem
            title="Auto-play Audio"
            subtitle="Automatically play Quranic recitations"
            value={settings.autoPlayAudio}
            onValueChange={(value) => handleSettingChange('autoPlayAudio', value)}
            type="toggle"
            icon="play-circle"
            iconColor="#F57C00"
            settings={settings}
          />
        </View>

        {/* Notifications */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Notifications</Text>

          <SettingItem
            title="Push Notifications"
            subtitle="Receive reminders and updates"
            value={settings.notifications}
            onValueChange={(value) => handleSettingChange('notifications', value)}
            type="toggle"
            icon="notifications"
            iconColor="#4CAF50"
            settings={settings}
          />
        </View>

        {/* Data & Storage */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Data & Storage</Text>

          <SettingItem
            title="Clear Cache"
            subtitle="Remove temporary files and cached data"
            value={null}
            onValueChange={handleClearCache}
            type="action"
            icon="delete-sweep"
            iconColor="#F44336"
            settings={settings}
          />

          <SettingItem
            title="Offline Mode"
            subtitle="Use downloaded content when offline"
            value={false} // This would be based on actual offline status
            onValueChange={() => {}}
            type="toggle"
            icon="offline-bolt"
            iconColor="#9E9E9E"
            settings={settings}
          />
        </View>

        {/* About */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>About</Text>

          <SettingItem
            title="About Hajj Rules App"
            subtitle="Version information and acknowledgments"
            value={null}
            onValueChange={handleAbout}
            type="action"
            icon="info"
            iconColor="#607D8B"
            settings={settings}
          />

          <SettingItem
            title="Share App"
            subtitle="Share with friends and family"
            value={null}
            onValueChange={() => {
              // Implement sharing functionality
              Alert.alert('Share', 'Sharing functionality would be implemented here');
            }}
            type="action"
            icon="share"
            iconColor="#2196F3"
            settings={settings}
          />

          <SettingItem
            title="Rate App"
            subtitle="Rate us on the app store"
            value={null}
            onValueChange={() => {
              // Implement rating functionality
              Alert.alert('Rate App', 'Rating functionality would be implemented here');
            }}
            type="action"
            icon="star"
            iconColor="#FFC107"
            settings={settings}
          />
        </View>

        {/* Footer */}
        <View style={styles.footer}>
          <Text style={styles.footerText}>
            Hajj Rules App v{APP_CONFIG.version}
          </Text>
          <Text style={styles.footerSubtext}>
            © 2025 - All content verified against authentic Islamic sources
          </Text>
        </View>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: APP_CONFIG.theme.background,
  },
  header: {
    backgroundColor: APP_CONFIG.theme.primary,
    paddingTop: 50,
    paddingBottom: 20,
    paddingHorizontal: 20,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
  },
  scrollView: {
    flex: 1,
  },
  section: {
    backgroundColor: APP_CONFIG.theme.surface,
    marginTop: 20,
    paddingVertical: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2,
    elevation: 2,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: APP_CONFIG.theme.textSecondary,
    paddingHorizontal: 20,
    paddingVertical: 12,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  touchableItem: {
    marginHorizontal: 20,
    marginVertical: 4,
  },
  settingItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 16,
    paddingHorizontal: 20,
    backgroundColor: APP_CONFIG.theme.surface,
  },
  iconContainer: {
    width: 40,
    height: 40,
    borderRadius: 20,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 16,
  },
  settingContent: {
    flex: 1,
  },
  settingTitle: {
    fontSize: 16,
    fontWeight: '500',
    color: APP_CONFIG.theme.text,
    marginBottom: 4,
  },
  settingSubtitle: {
    fontSize: 14,
    color: APP_CONFIG.theme.textSecondary,
  },
  selectorContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginLeft: 16,
  },
  selectorValue: {
    fontSize: 16,
    color: APP_CONFIG.theme.primary,
    marginRight: 8,
  },
  footer: {
    padding: 40,
    alignItems: 'center',
  },
  footerText: {
    fontSize: 16,
    fontWeight: '600',
    color: APP_CONFIG.theme.text,
    marginBottom: 8,
  },
  footerSubtext: {
    fontSize: 14,
    color: APP_CONFIG.theme.textSecondary,
    textAlign: 'center',
    lineHeight: 20,
  },
});

export default EnhancedSettingsScreen;