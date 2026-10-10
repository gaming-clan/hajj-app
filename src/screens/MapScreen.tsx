import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import DataService from '../services/DataService';

const MapScreen: React.FC = () => {
  const miqatData = DataService.getCategories().find(
    (cat) => cat.name === 'vendcaktimet',
  );

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Vendcaktimet (Miqat)</Text>
        <Text style={styles.headerDescription}>
          Vendcaktimet ku duhet të bëhet ihrami para se të hyjnë në territorin e
          shenjtë
        </Text>
      </View>

      {miqatData?.rules?.map((miqat, index) => (
        <View key={index} style={styles.miqatCard}>
          <Text style={styles.miqatName}>{miqat.rule}</Text>
          <Text style={styles.miqatDescription}>{miqat.description}</Text>
        </View>
      ))}

      <View style={styles.infoSection}>
        <Text style={styles.infoTitle}>Informacion i rëndësishëm</Text>
        <Text style={styles.infoText}>
          Vendcaktimet janë pika gjeografike të caktuara nga Profeti Muhamed
          (a.s.) ku haxhilerët dhe atyre që kryejnë umren duhet të bëjnë ihramin
          para se të vazhdojnë drejt Mekës.
        </Text>
        <Text style={styles.infoText}>
          Është e detyrueshme për çdo haxhiler të kalojë nëpër një nga këto
          vendcaktime dhe të bëjë ihramin para se të hyjë në territorin e
          shenjtë të Mekës.
        </Text>
      </View>
    </ScrollView>
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
    borderRadius: 12,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 8,
  },
  headerDescription: {
    fontSize: 14,
    color: '#E8F5E8',
    lineHeight: 20,
  },
  miqatCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    margin: 16,
    marginTop: 8,
    marginBottom: 8,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  miqatName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#2E7D32',
    marginBottom: 8,
  },
  miqatDescription: {
    fontSize: 14,
    color: '#333',
    lineHeight: 20,
  },
  infoSection: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    margin: 16,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  infoTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#2E7D32',
    marginBottom: 12,
  },
  infoText: {
    fontSize: 14,
    color: '#333',
    lineHeight: 20,
    marginBottom: 12,
    textAlign: 'justify',
  },
});

export default MapScreen;
