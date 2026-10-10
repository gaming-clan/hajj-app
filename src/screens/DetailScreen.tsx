import React from 'react';
import { View, Text, FlatList, StyleSheet, ScrollView } from 'react-native';
import { useRoute } from '@react-navigation/native';
import { HajjRule, Category } from '../types';

interface RouteParams {
  rule?: HajjRule;
  category?: Category;
  title: string;
}

const DetailScreen: React.FC = () => {
  const route = useRoute();
  const { rule, category, title } = route.params as RouteParams;

  const renderRule = ({ item }: { item: HajjRule }) => (
    <View style={styles.ruleCard}>
      <Text style={styles.ruleTitle}>{item.rule}</Text>
      <Text style={styles.ruleDescription}>{item.description}</Text>
    </View>
  );

  if (rule) {
    // Single rule view
    return (
      <ScrollView style={styles.container}>
        <View style={styles.ruleCard}>
          <Text style={styles.ruleTitle}>{rule.rule}</Text>
          <Text style={styles.ruleDescription}>{rule.description}</Text>
        </View>
      </ScrollView>
    );
  }

  if (category && category.rules) {
    // Category rules view
    return (
      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>{category.title}</Text>
          <Text style={styles.headerDescription}>{category.description}</Text>
        </View>
        <FlatList
          data={category.rules}
          renderItem={renderRule}
          keyExtractor={(item) => `${item.category}-${item.id}`}
          contentContainerStyle={styles.listContainer}
          showsVerticalScrollIndicator={false}
        />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.emptyText}>Nuk ka të dhëna për të shfaqur</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  header: {
    backgroundColor: '#2E7D32',
    padding: 20,
    margin: 16,
    marginBottom: 0,
    borderRadius: 12,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 8,
  },
  headerDescription: {
    fontSize: 14,
    color: '#E8F5E8',
    lineHeight: 20,
  },
  listContainer: {
    padding: 16,
  },
  ruleCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  ruleTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#2E7D32',
    marginBottom: 8,
  },
  ruleDescription: {
    fontSize: 14,
    color: '#333',
    lineHeight: 20,
  },
  emptyText: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
    marginTop: 50,
  },
});

export default DetailScreen;
