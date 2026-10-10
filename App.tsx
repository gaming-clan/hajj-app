import React from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';
import { NavigationProvider } from './src/shared/navigation/AppNavigator';
import AppNavigator from './src/shared/navigation/AppNavigator';
import { AppSettings } from './src/core/types';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { APP_CONFIG } from './src/core/constants';

// Splash screen is also used while settings load.

// Load settings from storage
const loadSettings = async (): Promise<AppSettings> => {
  try {
    const storedSettings = await AsyncStorage.getItem(
      APP_CONFIG.storage.settingsKey,
    );
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

  if (loading || !settings) {
    return (
      <View style={styles.loading}>
        <ActivityIndicator size="large" color={APP_CONFIG.theme.primary} />
      </View>
    );
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

const styles = StyleSheet.create({
  loading: { flex: 1, alignItems: 'center', justifyContent: 'center' },
});

export default App;
