import { useState, useEffect, useCallback, useMemo } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

interface CacheEntry<T> {
  data: T;
  timestamp: number;
  version: string;
}

interface UseOptimizedDataOptions<T> {
  cacheKey: string;
  fetcher: () => Promise<T>;
  cacheVersion?: string;
  cacheTTL?: number; // in milliseconds, default 1 hour
  enableCache?: boolean;
  debounceMs?: number;
}

/**
 * Hook for optimized data loading with caching and debouncing
 * Prevents unnecessary network requests and improves performance
 */
export function useOptimizedData<T>({
  cacheKey,
  fetcher,
  cacheVersion = '1.0',
  cacheTTL = 60 * 60 * 1000, // 1 hour
  enableCache = true,
  debounceMs = 300,
}: UseOptimizedDataOptions<T>) {
  const [data, setData] = useState<T | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const [lastFetched, setLastFetched] = useState<number | null>(null);

  // Memoized cache check function
  const isCacheValid = useCallback((cacheEntry: CacheEntry<T>): boolean => {
    const now = Date.now();
    const isExpired = (now - cacheEntry.timestamp) > cacheTTL;
    const isVersionMatch = cacheEntry.version === cacheVersion;
    return !isExpired && isVersionMatch;
  }, [cacheTTL, cacheVersion]);

  // Load data from cache if available and valid
  const loadFromCache = useCallback(async (): Promise<T | null> => {
    if (!enableCache) return null;

    try {
      const cached = await AsyncStorage.getItem(cacheKey);
      if (cached) {
        const cacheEntry: CacheEntry<T> = JSON.parse(cached);

        if (isCacheValid(cacheEntry)) {
          return cacheEntry.data;
        } else {
          // Remove expired cache
          await AsyncStorage.removeItem(cacheKey);
        }
      }
    } catch (error) {
      console.warn('Cache read error:', error);
    }

    return null;
  }, [cacheKey, enableCache, isCacheValid]);

  // Save data to cache
  const saveToCache = useCallback(async (dataToCache: T): Promise<void> => {
    if (!enableCache) return;

    try {
      const cacheEntry: CacheEntry<T> = {
        data: dataToCache,
        timestamp: Date.now(),
        version: cacheVersion,
      };
      await AsyncStorage.setItem(cacheKey, JSON.stringify(cacheEntry));
    } catch (error) {
      console.warn('Cache write error:', error);
    }
  }, [cacheKey, enableCache, cacheVersion]);

  // Main data fetching function with debouncing
  const fetchData = useCallback(async (forceRefresh = false) => {
    // Try to load from cache first (unless force refresh)
    if (!forceRefresh) {
      const cachedData = await loadFromCache();
      if (cachedData) {
        setData(cachedData);
        setIsLoading(false);
        setLastFetched(Date.now());
        return;
      }
    }

    setIsLoading(true);
    setError(null);

    try {
      const freshData = await fetcher();
      setData(freshData);
      setLastFetched(Date.now());

      // Cache the fresh data
      await saveToCache(freshData);
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Unknown error occurred');
      setError(error);
    } finally {
      setIsLoading(false);
    }
  }, [fetcher, forceRefresh, loadFromCache, saveToCache]);

  // Debounced refresh function
  const debouncedRefresh = useMemo(
    () => debounce(fetchData, debounceMs),
    [fetchData, debounceMs]
  );

  // Initial data load
  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // Memoized return value
  const result = useMemo(() => ({
    data,
    isLoading,
    error,
    lastFetched,
    refresh: () => fetchData(true),
    debouncedRefresh,
    clearCache: async () => {
      await AsyncStorage.removeItem(cacheKey);
    },
  }), [data, isLoading, error, lastFetched, fetchData, debouncedRefresh, cacheKey]);

  return result;
}

// Simple debounce utility
function debounce<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: NodeJS.Timeout;

  return (...args: Parameters<T>) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), wait);
  };
}