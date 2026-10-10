import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import DataService from '../services/DataService';

const AboutScreen: React.FC = () => {
  const introduction = DataService.getIntroduction();
  const hajjObligation = DataService.getHajjObligation();
  const ihramInfo = DataService.getIhramInfo();

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Rreth Aplikacionit</Text>
        <Text style={styles.headerDescription}>
          Udhëzues i plotë për rregullat e Haxhit
        </Text>
      </View>

      {introduction && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Hyrje</Text>
          <Text style={styles.sectionText}>{introduction.description}</Text>
          <Text style={styles.sectionText}>{introduction.qabja}</Text>
        </View>
      )}

      {hajjObligation && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Detyrimi i Haxhit</Text>
          <Text style={styles.sectionText}>{hajjObligation.description}</Text>

          <View style={styles.hadithContainer}>
            <Text style={styles.hadithTitle}>Hadithi:</Text>
            <Text style={styles.hadithText}>"{hajjObligation.hadith}"</Text>
          </View>

          <Text style={styles.sectionText}>{hajjObligation.kushtet}</Text>
        </View>
      )}

      {ihramInfo && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Ihrami</Text>
          <Text style={styles.sectionText}>{ihramInfo.description}</Text>
          <Text style={styles.sectionText}>
            <Text style={styles.bold}>Koha:</Text> {ihramInfo.koha}
          </Text>

          <Text style={styles.subsectionTitle}>Llojet e Nijetit:</Text>
          {ihramInfo.llojet_e_nijetit.map((nija, index) => (
            <View key={index} style={styles.nijetContainer}>
              <Text style={styles.nijetType}>{nija.lloji}:</Text>
              <Text style={styles.nijetText}>{nija.nijeti}</Text>
            </View>
          ))}
        </View>
      )}

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Aplikacioni</Text>
        <Text style={styles.sectionText}>
          Ky aplikacion është krijuar për të ndihmuar muslimanët në mësimin e
          rregullave të Haxhit dhe Umres. Përmban informacione të detajuara për
          të gjitha aspektet e rëndësishme të këtyre akteve të adhurimit.
        </Text>
        <Text style={styles.sectionText}>
          Informacionet janë bazuar në Kuran dhe Sunnah, dhe janë organizuar në
          mënyrë që të jenë të lehta për t'u kuptuar dhe zbatuar.
        </Text>
      </View>

      <View style={styles.footer}>
        <Text style={styles.footerText}>Hajj Rules App v1.0</Text>
        <Text style={styles.footerText}>
          © 2025 - Për më shumë informata kontaktoni zhvilluesit
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
  section: {
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
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#2E7D32',
    marginBottom: 12,
  },
  sectionText: {
    fontSize: 14,
    color: '#333',
    lineHeight: 20,
    marginBottom: 8,
    textAlign: 'justify',
  },
  subsectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2E7D32',
    marginTop: 12,
    marginBottom: 8,
  },
  hadithContainer: {
    backgroundColor: '#E8F5E8',
    padding: 12,
    borderRadius: 8,
    marginVertical: 8,
  },
  hadithTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#2E7D32',
    marginBottom: 4,
  },
  hadithText: {
    fontSize: 14,
    color: '#333',
    fontStyle: 'italic',
    lineHeight: 18,
  },
  nijetContainer: {
    marginBottom: 8,
  },
  nijetType: {
    fontSize: 14,
    fontWeight: '600',
    color: '#2E7D32',
  },
  nijetText: {
    fontSize: 14,
    color: '#333',
    marginLeft: 8,
    fontStyle: 'italic',
  },
  bold: {
    fontWeight: 'bold',
    color: '#2E7D32',
  },
  footer: {
    padding: 20,
    alignItems: 'center',
  },
  footerText: {
    fontSize: 12,
    color: '#666',
    textAlign: 'center',
    marginBottom: 4,
  },
});

export default AboutScreen;
