import React, { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { RouteProp, useRoute } from '@react-navigation/native';

import { useNavigationContext } from '../shared/navigation/AppNavigator';
import { RootStackParamList, Rule } from '../core/types';
import { getLocalizedTextWithFallback } from '../core/utils';
import ContentService from '../features/content/services/ContentService';

type ContentRoute = RouteProp<RootStackParamList, 'Content'>;

const ContentScreen: React.FC = () => {
  const { category } = useRoute<ContentRoute>().params;
  const { settings } = useNavigationContext();
  const [rules, setRules] = useState<Rule[]>([]);
  const [loading, setLoading] = useState(true);

  const loadRules = useCallback(async () => {
    setLoading(true);
    try {
      await ContentService.initialize(settings);
      setRules(await ContentService.getRulesByCategory(category));
    } finally {
      setLoading(false);
    }
  }, [category, settings]);

  useEffect(() => {
    loadRules();
  }, [loadRules]);

  if (loading) {
    return <ActivityIndicator style={styles.loading} size="large" />;
  }

  return (
    <FlatList
      contentContainerStyle={styles.list}
      data={rules}
      keyExtractor={(item) => item.id}
      ListEmptyComponent={
        <Text style={styles.empty}>
          No guidance is available in this category yet.
        </Text>
      }
      renderItem={({ item }) => (
        <View style={styles.card}>
          <Text style={styles.title}>
            {getLocalizedTextWithFallback(item.title, settings)}
          </Text>
          <Text style={styles.description}>
            {getLocalizedTextWithFallback(item.description, settings)}
          </Text>
        </View>
      )}
    />
  );
};

const styles = StyleSheet.create({
  loading: { flex: 1 },
  list: { padding: 16, flexGrow: 1, backgroundColor: '#F5F5F5' },
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
  },
  title: {
    fontSize: 17,
    fontWeight: 'bold',
    color: '#2E7D32',
    marginBottom: 8,
  },
  description: { fontSize: 15, color: '#333333', lineHeight: 22 },
  empty: { color: '#666666', textAlign: 'center', marginTop: 32 },
});

export default ContentScreen;
