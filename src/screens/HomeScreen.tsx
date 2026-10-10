import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  Image,
  Dimensions,
  ScrollView,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../core/types';
import DataService from '../services/DataService';
import { Category } from '../types';

const { width } = Dimensions.get('window');

const HomeScreen: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [introduction, setIntroduction] = useState<any>(null);
  const navigation = useNavigation<StackNavigationProp<RootStackParamList>>();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = () => {
    const categoriesData = DataService.getCategories();
    const introData = DataService.getIntroduction();
    setCategories(categoriesData);
    setIntroduction(introData);
  };

  const handleCategoryPress = (category: Category) => {
    navigation.navigate('Detail', {
      category,
      title: category.title,
    });
  };

  const renderCategory = ({ item }: { item: Category }) => (
    <TouchableOpacity
      style={styles.categoryCard}
      onPress={() => handleCategoryPress(item)}
    >
      <View style={styles.categoryContent}>
        <View style={styles.categoryIcon}>
          <Text style={styles.categoryIconText}>📖</Text>
        </View>
        <View style={styles.categoryTextContainer}>
          <Text style={styles.categoryTitle}>{item.title}</Text>
          <Text style={styles.categoryDescription} numberOfLines={2}>
            {item.description}
          </Text>
        </View>
      </View>
    </TouchableOpacity>
  );

  return (
    <ScrollView style={styles.container}>
      {introduction && (
        <View style={styles.introSection}>
          <Text style={styles.introTitle}>Mirë se vini në Haxh App</Text>
          <Text style={styles.introText}>{introduction.description}</Text>
          <Text style={styles.introSubtext}>{introduction.qabja}</Text>
        </View>
      )}

      <View style={styles.categoriesSection}>
        <Text style={styles.sectionTitle}>Kategoritë</Text>
        <FlatList
          data={categories}
          renderItem={renderCategory}
          keyExtractor={(item) => item.id.toString()}
          scrollEnabled={false}
          showsVerticalScrollIndicator={false}
        />
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  introSection: {
    backgroundColor: '#2E7D32',
    padding: 20,
    margin: 16,
    borderRadius: 12,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
  },
  introTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 12,
    textAlign: 'center',
  },
  introText: {
    fontSize: 16,
    color: '#E8F5E8',
    lineHeight: 24,
    marginBottom: 10,
    textAlign: 'justify',
  },
  introSubtext: {
    fontSize: 14,
    color: '#C8E6C9',
    lineHeight: 20,
    fontStyle: 'italic',
    textAlign: 'center',
  },
  categoriesSection: {
    padding: 16,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 16,
  },
  categoryCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    marginBottom: 12,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  categoryContent: {
    flexDirection: 'row',
    padding: 16,
    alignItems: 'center',
  },
  categoryIcon: {
    width: 50,
    height: 50,
    backgroundColor: '#E8F5E8',
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 16,
  },
  categoryIconText: {
    fontSize: 24,
  },
  categoryTextContainer: {
    flex: 1,
  },
  categoryTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  categoryDescription: {
    fontSize: 14,
    color: '#666',
    lineHeight: 20,
  },
});

export default HomeScreen;
