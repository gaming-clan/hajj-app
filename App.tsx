import React from 'react';
import { NavigationProvider, NavigationContext } from './src/shared/navigation/AppNavigator';
import AppNavigator from './src/shared/navigation/AppNavigator';
import { AppSettings } from './src/core/types';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { APP_CONFIG } from './src/core/constants';

// Import screens for backward compatibility
// These will be gradually replaced with enhanced versions
import SplashScreen from './src/screens/SplashScreen';
import DetailScreen from './src/screens/DetailScreen';
import SearchScreen from './src/screens/SearchScreen';
import MapScreen from './src/screens/MapScreen';
import AboutScreen from './src/screens/AboutScreen';

// Enhanced screens
import EnhancedHomeScreen from './src/features/content/screens/EnhancedHomeScreen';
import CategoriesScreen from './src/screens/CategoriesScreen';
import EnhancedSettingsScreen from './src/features/settings/screens/SettingsScreen';

// Load settings from storage
const loadSettings = async (): Promise<AppSettings> => {
  try {
    const storedSettings = await AsyncStorage.getItem(APP_CONFIG.storage.settingsKey);
    if (storedSettings) {
      return JSON.parse(storedSettings);
    }
  } catch (error) {
    console.warn('Failed to load settings from storage:', error);
  }

  // Default settings
  return {
    language: 'albanian',
    theme: 'light',
    fontSize: 'medium',
    showArabicText: false,
    showTransliteration: false,
    autoPlayAudio: false,
    notifications: true,
    rtl: false,
  };
};

// Save settings to storage
const saveSettings = async (settings: AppSettings): Promise<void> => {
  try {
    await AsyncStorage.setItem(APP_CONFIG.storage.settingsKey, JSON.stringify(settings));
  } catch (error) {
    console.warn('Failed to save settings to storage:', error);
  }
};

// Main App Component
const AppContent: React.FC = () => {
  const [settings, setSettings] = React.useState<AppSettings | null>(null);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    const initializeApp = async () => {
      try {
        const loadedSettings = await loadSettings();
        setSettings(loadedSettings);
      } catch (error) {
        console.error('Failed to initialize app:', error);
      } finally {
        setLoading(false);
      }
    };

    initializeApp();
  }, []);

  const updateSettings = React.useCallback(
    async (newSettings: Partial<AppSettings>) => {
      if (!settings) return;

      const updatedSettings = { ...settings, ...newSettings };
      setSettings(updatedSettings);
      await saveSettings(updatedSettings);
    },
    [settings]
  );

  if (loading || !settings) {
    return <SplashScreen />;
  }

  return (
    <NavigationProvider initialSettings={settings}>
      <AppNavigator />
    </NavigationProvider>
  );
};

// Main App Export
function App(): JSX.Element {
  return <AppContent />;
}

export default App;