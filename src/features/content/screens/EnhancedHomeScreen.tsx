import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Image,
  Dimensions,
  RefreshControl,
  Animated,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';

import { RootStackParamList } from '../../../shared/navigation/AppNavigator';
import { useNavigationContext } from '../../../shared/navigation/AppNavigator';
import {
  HajjData,
  Category,
  AppSettings,
} from '../../../core/types';
import {
  getLocalizedTextWithFallback,
  formatText,
  shouldUseRTL,
} from '../../../core/utils';
import { APP_CONFIG } from '../../../core/constants';
import ContentService from '../services/ContentService';

type HomeScreenNavigationProp = StackNavigationProp<RootStackParamList, 'Main'>;

interface FeatureCardProps {
  icon: string;
  title: any; // MultilingualTitle
  description: any; // MultilingualDescription
  onPress: () => void;
  color?: string;
  settings: AppSettings;
}

const { width: screenWidth } = Dimensions.get('window');

const FeatureCard: React.FC<FeatureCardProps> = ({
  icon,
  title,
  description,
  onPress,
  color = APP_CONFIG.theme.primary,
  settings,
}) => {
  const animatedValue = React.useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.spring(animatedValue, {
      toValue: 1,
      useNativeDriver: true,
      tension: 100,
      friction: 8,
    }).start();
  }, []);

  const isCardRTL = shouldUseRTL(settings);
  const titleText = getLocalizedTextWithFallback(title, settings);
  const descriptionText = getLocalizedTextWithFallback(description, settings);

  return (
    <Animated.View
      style={[
        styles.featureCard,
        {
          transform: [
            {
              scale: animatedValue.interpolate({
                inputRange: [0, 1],
                outputRange: [0.95, 1],
              }),
            },
            {
              translateY: animatedValue.interpolate({
                inputRange: [0, 1],
                outputRange: [50, 0],
              }),
            },
          ],
        },
      ]}
    >
      <TouchableOpacity
        style={[styles.cardContainer, { borderLeftColor: color }]}
        onPress={onPress}
        activeOpacity={0.7}
      >
        <View style={styles.cardContent}>
          <View style={[styles.iconContainer, { backgroundColor: `${color}20` }]}>
            <Icon name={icon} size={24} color={color} />
          </View>

          <View style={styles.textContainer}>
            <Text
              style={[
                styles.cardTitle,
                {
                  color,
                  textAlign: isCardRTL ? 'right' : 'left',
                  writingDirection: isCardRTL ? 'rtl' : 'ltr',
                },
              ]}
            >
              {titleText}
            </Text>
            <Text
              style={[
                styles.cardDescription,
                {
                  textAlign: isCardRTL ? 'right' : 'left',
                  writingDirection: isCardRTL ? 'rtl' : 'ltr',
                },
              ]}
              numberOfLines={2}
            >
              {descriptionText}
            </Text>
          </View>

          <Icon
            name="chevron-right"
            size={20}
            color={APP_CONFIG.theme.textSecondary}
            style={[
              styles.chevron,
              { transform: [{ rotate: isCardRTL ? '180deg' : '0deg' }] }
            ]}
          />
        </View>
      </TouchableOpacity>
    </Animated.View>
  );
};

const EnhancedHomeScreen: React.FC = () => {
  const navigation = useNavigation<HomeScreenNavigationProp>();
  const { settings, updateSettings } = useNavigationContext();
  const [hajjData, setHajjData] = useState<HajjData | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const contentService = useMemo(() => ContentService.getInstance(), []);
  const isRTL = shouldUseRTL(settings);

  // Load data
  const loadData = useCallback(async () => {
    try {
      setError(null);
      await contentService.initialize(settings);

      const [data, cats] = await Promise.all([
        contentService.getHajjData(),
        contentService.getCategories(),
      ]);

      setHajjData(data);
      setCategories(cats);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load data');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [contentService, settings]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  // Handle refresh
  const handleRefresh = useCallback(() => {
    setRefreshing(true);
    contentService.clearCache();
    loadData();
  }, [contentService, loadData]);

  // Navigation handlers
  const handleCategoryPress = useCallback((category: Category) => {
    navigation.navigate('Content', {
      category: category.name,
      title: category.title,
    });
  }, [navigation]);

  const handleSearchPress = useCallback(() => {
    navigation.navigate('Search');
  }, [navigation]);

  const handleMapPress = useCallback(() => {
    navigation.navigate('Map');
  }, [navigation]);

  const handleSettingsPress = useCallback(() => {
    navigation.navigate('Settings');
  }, [navigation]);

  // Featured categories for quick access
  const featuredCategories = useMemo(() => {
    return categories.filter(cat => cat.featured).slice(0, 3);
  }, [categories]);

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <Text style={styles.loadingText}>Loading...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.errorContainer}>
        <Icon name="error" size={48} color={APP_CONFIG.theme.primary} />
        <Text style={styles.errorText}>Error loading content</Text>
        <Text style={styles.errorSubtext}>{error}</Text>
        <TouchableOpacity style={styles.retryButton} onPress={handleRefresh}>
          <Text style={styles.retryButtonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={[styles.container, { backgroundColor: APP_CONFIG.theme.background }]}>
      {/* Header */}
      <View style={[styles.header, { backgroundColor: APP_CONFIG.theme.primary }]}>
        <View style={styles.headerContent}>
          <View>
            <Text style={styles.welcomeText}>
              {getLocalizedTextWithFallback(
                { albanian: 'Mirësevini', arabic: 'أهلاً بك', english: 'Welcome' },
                settings
              )}
            </Text>
            <Text style={styles.appTitle}>
              {hajjData ? getLocalizedTextWithFallback(hajjData.title, settings) : 'Hajj Rules'}
            </Text>
          </View>

          <View style={styles.headerActions}>
            <TouchableOpacity
              style={styles.headerButton}
              onPress={handleSearchPress}
            >
              <Icon name="search" size={24} color="#fff" />
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.headerButton}
              onPress={handleSettingsPress}
            >
              <Icon name="settings" size={24} color="#fff" />
            </TouchableOpacity>
          </View>
        </View>
      </View>

      {/* Content */}
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={handleRefresh}
            colors={[APP_CONFIG.theme.primary]}
            tintColor={APP_CONFIG.theme.primary}
          />
        }
        showsVerticalScrollIndicator={false}
      >
        {/* Introduction Card */}
        {hajjData && (
          <View style={[styles.introductionCard, styles.cardMargin]}>
            <View style={styles.introductionContent}>
              <Text style={styles.introductionText}>
                {getLocalizedTextWithFallback(hajjData.introduction.description, settings)}
              </Text>
            </View>
          </View>
        )}

        {/* Quick Access Features */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>
            {getLocalizedTextWithFallback(
              { albanian: 'Aksesi i Shpejtë', arabic: 'الوصول السريع', english: 'Quick Access' },
              settings
            )}
          </Text>

          <FeatureCard
            icon="menu-book"
            title={{ albanian: 'Kategoritë', arabic: 'الفئات', english: 'Categories' }}
            description={{
              albanian: 'Shfletoni të gjitha rregullat e Haxhit të organizuara për kategori',
              arabic: 'تصفح جميع قواعد الحج المنظمة حسب الفئة',
              english: 'Browse all Hajj rules organized by category'
            }}
            onPress={() => navigation.navigate('Categories')}
            settings={settings}
          />

          <FeatureCard
            icon="map"
            title={{ albanian: 'Harta e Miqat', arabic: 'خريطة المواقيت', english: 'Miqat Map' }}
            description={{
              albanian: 'Vendndodhjet e Miqat-ve ku fillon Ihrami',
              arabic: 'مواقع المواقيت حيث يبدأ الإحرام',
              english: 'Locations of Miqat where Ihram begins'
            }}
            onPress={handleMapPress}
            color="#F57C00"
            settings={settings}
          />

          <FeatureCard
            icon="help"
            title={{ albanian: 'Rreth Aplikacionit', arabic: 'حول التطبيق', english: 'About App' }}
            description={{
              albanian: 'Mësoni më shumë rreth aplikacionit dhe burimeve islame',
              arabic: 'اعرف المزيد عن التطبيق ومصادره الإسلامية',
              english: 'Learn more about the app and its Islamic sources'
            }}
            onPress={() => navigation.navigate('About')}
            color="#7B1FA2"
            settings={settings}
          />
        </View>

        {/* Featured Categories */}
        {featuredCategories.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>
              {getLocalizedTextWithFallback(
                { albanian: 'Kategoritë e Rekomanduara', arabic: 'الفئات الموصى بها', english: 'Featured Categories' },
                settings
              )}
            </Text>

            {featuredCategories.map((category) => (
              <FeatureCard
                key={category.id}
                icon={category.icon || 'category'}
                title={category.title}
                description={category.description}
                onPress={() => handleCategoryPress(category)}
                color={category.color}
                settings={settings}
              />
            ))}
          </View>
        )}

        {/* Hajj Obligation Info */}
        {hajjData && (
          <View style={[styles.infoCard, styles.cardMargin]}>
            <View style={styles.infoContent}>
              <Icon
                name="info"
                size={24}
                color={APP_CONFIG.theme.primary}
                style={styles.infoIcon}
              />
              <Text style={styles.infoText}>
                {getLocalizedTextWithFallback(hajjData.detyrimi_i_haxhit.description, settings)}
              </Text>
            </View>
          </View>
        )}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    fontSize: 16,
    color: APP_CONFIG.theme.text,
  },
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  errorText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: APP_CONFIG.theme.text,
    marginTop: 16,
    textAlign: 'center',
  },
  errorSubtext: {
    fontSize: 14,
    color: APP_CONFIG.theme.textSecondary,
    marginTop: 8,
    textAlign: 'center',
  },
  retryButton: {
    marginTop: 16,
    paddingHorizontal: 24,
    paddingVertical: 12,
    backgroundColor: APP_CONFIG.theme.primary,
    borderRadius: 8,
  },
  retryButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  header: {
    paddingTop: 50,
    paddingBottom: 20,
    paddingHorizontal: 20,
  },
  headerContent: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  welcomeText: {
    fontSize: 14,
    color: '#fff',
    opacity: 0.8,
  },
  appTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
    marginTop: 4,
  },
  headerActions: {
    flexDirection: 'row',
    gap: 16,
  },
  headerButton: {
    padding: 8,
    borderRadius: 20,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    padding: 20,
  },
  introductionCard: {
    backgroundColor: APP_CONFIG.theme.surface,
    borderRadius: 12,
    padding: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  introductionContent: {
    borderLeftWidth: 4,
    borderLeftColor: APP_CONFIG.theme.primary,
    paddingLeft: 16,
  },
  introductionText: {
    fontSize: 16,
    lineHeight: 24,
    color: APP_CONFIG.theme.text,
    textAlign: 'justify',
  },
  section: {
    marginTop: 24,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: APP_CONFIG.theme.text,
    marginBottom: 16,
  },
  featureCard: {
    marginBottom: 16,
  },
  cardContainer: {
    backgroundColor: APP_CONFIG.theme.surface,
    borderRadius: 12,
    padding: 16,
    borderLeftWidth: 4,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardContent: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  iconContainer: {
    width: 48,
    height: 48,
    borderRadius: 24,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 16,
  },
  textContainer: {
    flex: 1,
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 4,
  },
  cardDescription: {
    fontSize: 14,
    color: APP_CONFIG.theme.textSecondary,
    lineHeight: 20,
  },
  chevron: {
    marginLeft: 16,
  },
  cardMargin: {
    marginBottom: 16,
  },
  infoCard: {
    backgroundColor: APP_CONFIG.theme.surface,
    borderRadius: 12,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  infoContent: {
    flexDirection: 'row',
    alignItems: 'flex-start',
  },
  infoIcon: {
    marginRight: 12,
    marginTop: 2,
  },
  infoText: {
    flex: 1,
    fontSize: 14,
    color: APP_CONFIG.theme.text,
    lineHeight: 20,
  },
});

export default EnhancedHomeScreen;