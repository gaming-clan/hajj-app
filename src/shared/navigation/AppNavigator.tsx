import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createStackNavigator } from '@react-navigation/stack';
import Icon from 'react-native-vector-icons/MaterialIcons';
import {
  AppSettings,
  MainTabParamList,
  RootStackParamList,
} from '../../core/types';
import { getLocalizedText, shouldUseRTL } from '../../core/utils';
import { APP_CONFIG } from '../../core/constants';

// Import screens (will be updated as we create them)
import SplashScreen from '../../screens/SplashScreen';
import CategoriesScreen from '../../screens/CategoriesScreen';
import ContentScreen from '../../screens/ContentScreen';
import MapScreen from '../../screens/MapScreen';
import AboutScreen from '../../screens/AboutScreen';
import DetailScreen from '../../screens/DetailScreen';
import SearchScreen from '../../screens/SearchScreen';
import SettingsScreen from '../../features/settings/screens/SettingsScreen';
import EnhancedHomeScreen from '../../features/content/screens/EnhancedHomeScreen';

// Navigation Context
interface NavigationContextType {
  settings: AppSettings;
  updateSettings: (settings: Partial<AppSettings>) => void;
  isRTL: boolean;
}

const NavigationContext = createContext<NavigationContextType | undefined>(
  undefined,
);

export const useNavigationContext = () => {
  const context = useContext(NavigationContext);
  if (!context) {
    throw new Error(
      'useNavigationContext must be used within NavigationProvider',
    );
  }
  return context;
};

// Navigation Provider
interface NavigationProviderProps {
  children: ReactNode;
  initialSettings?: AppSettings;
}

export const NavigationProvider: React.FC<NavigationProviderProps> = ({
  children,
  initialSettings,
}) => {
  const [settings, setSettings] = useState<AppSettings>(
    initialSettings || {
      language: 'albanian',
      theme: 'light',
      fontSize: 'medium',
      showArabicText: false,
      showTransliteration: false,
      autoPlayAudio: false,
      notifications: true,
      rtl: false,
    },
  );

  const [isRTL, setIsRTL] = useState(false);

  useEffect(() => {
    const newRTL = shouldUseRTL(settings);
    setIsRTL(newRTL);
    AsyncStorage.setItem(
      APP_CONFIG.storage.settingsKey,
      JSON.stringify(settings),
    ).catch((error) => {
      console.warn('Failed to save settings:', error);
    });
  }, [settings]);

  const updateSettings = (newSettings: Partial<AppSettings>) => {
    setSettings((prev) => ({ ...prev, ...newSettings }));
  };

  return (
    <NavigationContext.Provider
      value={{
        settings,
        updateSettings,
        isRTL,
      }}
    >
      {children}
    </NavigationContext.Provider>
  );
};

// Stack Navigator
const Stack = createStackNavigator<RootStackParamList>();

// Tab Navigator
const Tab = createBottomTabNavigator<MainTabParamList>();

// Main Tabs Component
const MainTabs: React.FC = () => {
  const { settings } = useNavigationContext();

  const getTabBarIcon = (routeName: string, color: string, size: number) => {
    let iconName: string;

    switch (routeName) {
      case 'Home':
        iconName = 'home';
        break;
      case 'Categories':
        iconName = 'category';
        break;
      case 'Map':
        iconName = 'map';
        break;
      case 'Settings':
        iconName = 'settings';
        break;
      default:
        iconName = 'help';
    }

    return <Icon name={iconName} size={size} color={color} />;
  };

  const getTabBarLabel = (routeName: string) => {
    const labels = {
      Home: {
        albanian: 'Ballina',
        arabic: 'الرئيسية',
        english: 'Home',
      },
      Categories: {
        albanian: 'Kategoritë',
        arabic: 'الفئات',
        english: 'Categories',
      },
      Map: {
        albanian: 'Harta',
        arabic: 'الخريطة',
        english: 'Map',
      },
      Settings: {
        albanian: 'Cilësimet',
        arabic: 'الإعدادات',
        english: 'Settings',
      },
    };

    const label = labels[routeName as keyof typeof labels];
    return label ? getLocalizedText(label, settings.language) : routeName;
  };

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ color, size }) => getTabBarIcon(route.name, color, size),
        tabBarLabel: () => getTabBarLabel(route.name),
        tabBarActiveTintColor: APP_CONFIG.theme.primary,
        tabBarInactiveTintColor: 'gray',
        headerStyle: {
          backgroundColor: APP_CONFIG.theme.primary,
        },
        headerTintColor: '#fff',
        headerTitleStyle: {
          fontWeight: 'bold',
        },
        tabBarStyle: {
          backgroundColor: APP_CONFIG.theme.surface,
          borderTopColor: APP_CONFIG.theme.background,
        },
        // RTL support
        tabBarDirection: settings.rtl ? 'rtl' : 'ltr',
      })}
    >
      <Tab.Screen
        name="Home"
        component={EnhancedHomeScreen}
        options={{
          headerShown: false,
        }}
      />
      <Tab.Screen
        name="Categories"
        component={CategoriesScreen}
        options={({ navigation }) => ({
          title: getTabBarLabel('Categories'),
          headerTitleStyle: {
            writingDirection: settings.rtl ? 'rtl' : 'ltr',
          },
        })}
      />
      <Tab.Screen
        name="Map"
        component={MapScreen}
        options={{
          title: getTabBarLabel('Map'),
          headerTitleStyle: {
            writingDirection: settings.rtl ? 'rtl' : 'ltr',
          },
        }}
      />
      <Tab.Screen
        name="Settings"
        component={SettingsScreen}
        options={{
          title: getTabBarLabel('Settings'),
          headerTitleStyle: {
            writingDirection: settings.rtl ? 'rtl' : 'ltr',
          },
        }}
      />
    </Tab.Navigator>
  );
};

// Main App Navigator
const AppNavigator: React.FC = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="Splash"
        screenOptions={{
          headerStyle: {
            backgroundColor: APP_CONFIG.theme.primary,
          },
          headerTintColor: '#fff',
          headerTitleStyle: {
            fontWeight: 'bold',
          },
        }}
      >
        <Stack.Screen
          name="Splash"
          component={SplashScreen}
          options={{ headerShown: false }}
        />
        <Stack.Screen
          name="Main"
          component={MainTabs}
          options={{ headerShown: false }}
        />
        <Stack.Screen
          name="Detail"
          component={DetailScreen}
          options={({ route }) => ({
            title:
              route.params.type === 'category'
                ? 'Category Details'
                : 'Rule Details',
            headerTitleStyle: {
              writingDirection: 'rtl', // Will be updated based on settings
            },
          })}
        />
        <Stack.Screen
          name="Search"
          component={SearchScreen}
          options={{
            title: 'Search',
            headerTitleStyle: {
              writingDirection: 'rtl', // Will be updated based on settings
            },
          }}
        />
        <Stack.Screen
          name="Content"
          component={ContentScreen}
          options={({ route }) => ({
            title: getLocalizedText(route.params.title, 'albanian'), // Will be updated based on settings
            headerTitleStyle: {
              writingDirection: 'rtl', // Will be updated based on settings
            },
          })}
        />
        <Stack.Screen
          name="Map"
          component={MapScreen}
          options={{
            title: 'Map',
            headerTitleStyle: {
              writingDirection: 'rtl', // Will be updated based on settings
            },
          }}
        />
        <Stack.Screen
          name="About"
          component={AboutScreen}
          options={{
            title: 'About',
            headerTitleStyle: {
              writingDirection: 'rtl', // Will be updated based on settings
            },
          }}
        />
        <Stack.Screen
          name="Settings"
          component={SettingsScreen}
          options={{
            title: 'Settings',
            headerTitleStyle: {
              writingDirection: 'rtl', // Will be updated based on settings
            },
          }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator;
