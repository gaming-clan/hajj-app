import AsyncStorage from '@react-native-async-storage/async-storage';
import { Platform } from 'react-native';
import CryptoJS from 'crypto-js';

/**
 * Secure storage implementation with encryption
 * Protects sensitive Islamic content and user data
 */
export class SecureStorage {
  private static readonly ENCRYPTION_KEY = 'hajj_app_secure_key_v1';
  private static readonly IV_LENGTH = 16;
  private static readonly PREFIX = 'secure_';

  /**
   * Store encrypted data
   */
  static async setItem(key: string, value: any): Promise<void> {
    try {
      const jsonValue = JSON.stringify(value);
      const encryptedValue = this.encrypt(jsonValue);
      await AsyncStorage.setItem(`${this.PREFIX}${key}`, encryptedValue);
    } catch (error) {
      throw new Error(`Failed to store secure data: ${error}`);
    }
  }

  /**
   * Retrieve and decrypt data
   */
  static async getItem<T>(key: string): Promise<T | null> {
    try {
      const encryptedValue = await AsyncStorage.getItem(`${this.PREFIX}${key}`);
      if (!encryptedValue) {
        return null;
      }

      const decryptedValue = this.decrypt(encryptedValue);
      return JSON.parse(decryptedValue) as T;
    } catch (error) {
      console.warn('Failed to retrieve secure data:', error);
      return null;
    }
  }

  /**
   * Remove secure data
   */
  static async removeItem(key: string): Promise<void> {
    try {
      await AsyncStorage.removeItem(`${this.PREFIX}${key}`);
    } catch (error) {
      throw new Error(`Failed to remove secure data: ${error}`);
    }
  }

  /**
   * Check if key exists
   */
  static async hasKey(key: string): Promise<boolean> {
    try {
      const value = await AsyncStorage.getItem(`${this.PREFIX}${key}`);
      return value !== null;
    } catch (error) {
      return false;
    }
  }

  /**
   * Clear all secure data
   */
  static async clear(): Promise<void> {
    try {
      const keys = await AsyncStorage.getAllKeys();
      const secureKeys = keys.filter(key => key.startsWith(this.PREFIX));
      await AsyncStorage.multiRemove(secureKeys);
    } catch (error) {
      throw new Error(`Failed to clear secure data: ${error}`);
    }
  }

  /**
   * Encrypt data using AES-256
   */
  private static encrypt(data: string): string {
    try {
      const encrypted = CryptoJS.AES.encrypt(data, this.ENCRYPTION_KEY);
      return encrypted.toString();
    } catch (error) {
      throw new Error('Encryption failed');
    }
  }

  /**
   * Decrypt data using AES-256
   */
  private static decrypt(encryptedData: string): string {
    try {
      const decrypted = CryptoJS.AES.decrypt(encryptedData, this.ENCRYPTION_KEY);
      const decryptedString = decrypted.toString(CryptoJS.enc.Utf8);

      if (!decryptedString) {
        throw new Error('Decryption resulted in empty string');
      }

      return decryptedString;
    } catch (error) {
      throw new Error('Decryption failed');
    }
  }

  /**
   * Store user preferences securely
   */
  static async setUserPreferences(preferences: {
    language?: string;
    theme?: 'light' | 'dark' | 'system';
    notifications?: boolean;
    autoBackup?: boolean;
  }): Promise<void> {
    await this.setItem('user_preferences', preferences);
  }

  /**
   * Get user preferences
   */
  static async getUserPreferences(): Promise<{
    language?: string;
    theme?: 'light' | 'dark' | 'system';
    notifications?: boolean;
    autoBackup?: boolean;
  }> {
    const defaultPreferences = {
      language: 'en',
      theme: 'system' as const,
      notifications: true,
      autoBackup: true,
    };

    const preferences = await this.getItem('user_preferences');
    return { ...defaultPreferences, ...preferences };
  }

  /**
   * Store app settings securely
   */
  static async setAppSettings(settings: {
    contentVersion?: string;
    lastSync?: number;
    securityLevel?: 'low' | 'medium' | 'high';
  }): Promise<void> {
    await this.setItem('app_settings', settings);
  }

  /**
   * Get app settings
   */
  static async getAppSettings(): Promise<{
    contentVersion?: string;
    lastSync?: number;
    securityLevel?: 'low' | 'medium' | 'high';
  }> {
    const defaultSettings = {
      contentVersion: '1.0.0',
      securityLevel: 'medium' as const,
    };

    const settings = await this.getItem('app_settings');
    return { ...defaultSettings, ...settings };
  }

  /**
   * Store user progress with encryption
   */
  static async setUserProgress(progress: {
    completedRules: string[];
    favoriteRules: string[];
    userNotes: Record<string, string>;
    lastAccess: number;
  }): Promise<void> {
    await this.setItem('user_progress', progress);
  }

  /**
   * Get user progress
   */
  static async getUserProgress(): Promise<{
    completedRules: string[];
    favoriteRules: string[];
    userNotes: Record<string, string>;
    lastAccess: number;
  }> {
    const defaultProgress = {
      completedRules: [],
      favoriteRules: [],
      userNotes: {},
      lastAccess: Date.now(),
    };

    const progress = await this.getItem('user_progress');
    return { ...defaultProgress, ...progress };
  }

  /**
   * Backup secure data
   */
  static async exportBackup(): Promise<string> {
    try {
      const keys = await AsyncStorage.getAllKeys();
      const secureKeys = keys.filter(key => key.startsWith(this.PREFIX));
      const backup: Record<string, string> = {};

      for (const key of secureKeys) {
        const value = await AsyncStorage.getItem(key);
        if (value) {
          backup[key] = value;
        }
      }

      const backupJson = JSON.stringify({
        version: '1.0',
        timestamp: Date.now(),
        platform: Platform.OS,
        data: backup,
      });

      return btoa(backupJson); // Base64 encode for transport
    } catch (error) {
      throw new Error(`Failed to create backup: ${error}`);
    }
  }

  /**
   * Restore secure data from backup
   */
  static async importBackup(backupData: string): Promise<void> {
    try {
      const backupJson = atob(backupData); // Decode Base64
      const backup = JSON.parse(backupJson);

      if (!backup.version || !backup.data) {
        throw new Error('Invalid backup format');
      }

      // Clear existing data
      await this.clear();

      // Restore backup data
      for (const [key, value] of Object.entries(backup.data)) {
        await AsyncStorage.setItem(key, value as string);
      }
    } catch (error) {
      throw new Error(`Failed to restore backup: ${error}`);
    }
  }
}