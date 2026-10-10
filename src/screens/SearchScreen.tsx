import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  FlatList,
  TouchableOpacity,
  StyleSheet,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../core/types';
import DataService from '../services/DataService';
import { HajjRule } from '../types';

const SearchScreen: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<HajjRule[]>([]);
  const navigation = useNavigation<StackNavigationProp<RootStackParamList>>();

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    if (query.trim().length > 0) {
      const results = DataService.searchRules(query.trim());
      setSearchResults(results);
    } else {
      setSearchResults([]);
    }
  };

  const handleRulePress = (rule: HajjRule) => {
    navigation.navigate('Detail', {
      rule,
      title: rule.rule,
    });
  };

  const renderRule = ({ item }: { item: HajjRule }) => (
    <TouchableOpacity
      style={styles.ruleCard}
      onPress={() => handleRulePress(item)}
    >
      <Text style={styles.ruleTitle}>{item.rule}</Text>
      <Text style={styles.ruleDescription} numberOfLines={2}>
        {item.description}
      </Text>
      {item.category && <Text style={styles.categoryTag}>{item.category}</Text>}
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.searchContainer}>
        <TextInput
          style={styles.searchInput}
          placeholder="Kërkoni për rregulla..."
          value={searchQuery}
          onChangeText={handleSearch}
          autoFocus
        />
      </View>

      {searchQuery.length === 0 ? (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>
            Shkruani një fjalë për të kërkuar rregullat e Haxhit
          </Text>
        </View>
      ) : searchResults.length === 0 ? (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>
            Nuk u gjetën rezultate për "{searchQuery}"
          </Text>
        </View>
      ) : (
        <FlatList
          data={searchResults}
          renderItem={renderRule}
          keyExtractor={(item) => `${item.category}-${item.id}`}
          contentContainerStyle={styles.listContainer}
          showsVerticalScrollIndicator={false}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  searchContainer: {
    padding: 16,
    backgroundColor: '#FFFFFF',
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  searchInput: {
    backgroundColor: '#F5F5F5',
    borderRadius: 25,
    paddingHorizontal: 20,
    paddingVertical: 12,
    fontSize: 16,
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
    marginBottom: 8,
  },
  categoryTag: {
    fontSize: 12,
    color: '#666',
    backgroundColor: '#E8F5E8',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
    alignSelf: 'flex-start',
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
  },
  emptyText: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
    lineHeight: 24,
  },
});

export default SearchScreen;
