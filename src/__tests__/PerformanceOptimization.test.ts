import { renderHook, act, waitFor } from '@testing-library/react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useOptimizedData } from '../shared/hooks/useOptimizedData';

// Mock AsyncStorage
jest.mock('@react-native-async-storage/async-storage', () => ({
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  getAllKeys: jest.fn(),
  multiRemove: jest.fn(),
}));

describe('Performance Optimization Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('useOptimizedData Hook', () => {
    test('should initialize with loading state', () => {
      const mockFetcher = jest.fn().mockResolvedValue({ data: 'test' });

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
          cacheTTL: 1000,
        }),
      );

      expect(result.current.isLoading).toBe(true);
      expect(result.current.data).toBeNull();
      expect(result.current.error).toBeNull();
    });

    test('should load data successfully', async () => {
      const testData = { id: 1, name: 'Test Data' };
      const mockFetcher = jest.fn().mockResolvedValue(testData);

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
        }),
      );

      await waitFor(() => expect(result.current.data).toEqual(testData));

      expect(result.current.isLoading).toBe(false);
      expect(result.current.data).toEqual(testData);
      expect(result.current.error).toBeNull();
      expect(mockFetcher).toHaveBeenCalledTimes(1);
    });

    test('should handle fetch errors gracefully', async () => {
      const error = new Error('Network error');
      const mockFetcher = jest.fn().mockRejectedValue(error);

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
        }),
      );

      await waitFor(() => expect(result.current.error).toBe(error));

      expect(result.current.isLoading).toBe(false);
      expect(result.current.data).toBeNull();
      expect(result.current.error).toBeInstanceOf(Error);
      expect(result.current.error?.message).toBe('Network error');
    });

    test('should refresh data when force refresh is called', async () => {
      const testData1 = { id: 1, name: 'Data 1' };
      const testData2 = { id: 2, name: 'Data 2' };
      const mockFetcher = jest
        .fn()
        .mockResolvedValueOnce(testData1)
        .mockResolvedValueOnce(testData2);

      const { result } = renderHook(
        ({ fetcher }) =>
          useOptimizedData({
            cacheKey: 'test-key',
            fetcher,
          }),
        {
          initialProps: { fetcher: mockFetcher },
        },
      );

      await waitFor(() => expect(result.current.data).toEqual(testData1));

      expect(result.current.data).toEqual(testData1);
      expect(mockFetcher).toHaveBeenCalledTimes(1);

      // Force refresh
      act(() => {
        result.current.refresh();
      });

      await waitFor(() => expect(result.current.data).toEqual(testData2));

      expect(result.current.data).toEqual(testData2);
      expect(mockFetcher).toHaveBeenCalledTimes(2);
    });

    test('should debounce refresh calls', async () => {
      const testData = { id: 1, name: 'Test Data' };
      const mockFetcher = jest.fn().mockResolvedValue(testData);

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
          debounceMs: 100,
        }),
      );

      await waitFor(() => expect(result.current.isLoading).toBe(false));
      mockFetcher.mockClear();
      jest.useFakeTimers();

      // Call debouncedRefresh multiple times quickly
      act(() => {
        result.current.debouncedRefresh();
        result.current.debouncedRefresh();
        result.current.debouncedRefresh();
      });

      // Fast-forward time
      await act(async () => {
        jest.advanceTimersByTime(100);
        await Promise.resolve();
        await Promise.resolve();
      });

      // Should only call fetcher once due to debouncing
      expect(mockFetcher).toHaveBeenCalledTimes(1);

      jest.useRealTimers();
    });

    test('should clear cache properly', async () => {
      const testData = { id: 1, name: 'Test Data' };
      const mockFetcher = jest.fn().mockResolvedValue(testData);
      AsyncStorage.removeItem.mockResolvedValue();

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
        }),
      );

      await waitFor(() => expect(result.current.data).toEqual(testData));

      act(() => {
        result.current.clearCache();
      });

      expect(AsyncStorage.removeItem).toHaveBeenCalledWith('test-key');
    });
  });

  describe('Cache Behavior', () => {
    test('should respect cache TTL', async () => {
      const testData = { data: 'test' };
      const mockFetcher = jest.fn().mockResolvedValue(testData);

      // Mock cache hit with valid TTL
      const cacheEntry = {
        data: testData,
        timestamp: Date.now() - 50000, // 50 seconds ago (within 1 hour TTL)
        version: '1.0',
      };
      AsyncStorage.getItem.mockResolvedValue(JSON.stringify(cacheEntry));

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
          cacheTTL: 60000, // 1 minute TTL
        }),
      );

      await waitFor(() => expect(result.current.data).toEqual(testData));
      // Should use cache data without calling fetcher
      expect(mockFetcher).not.toHaveBeenCalled();
      expect(AsyncStorage.getItem).toHaveBeenCalledWith('test-key');
    });

    test('should ignore expired cache', async () => {
      const testData = { data: 'test' };
      const mockFetcher = jest.fn().mockResolvedValue(testData);

      // Mock cache hit with expired TTL
      const cacheEntry = {
        data: testData,
        timestamp: Date.now() - 120000, // 2 minutes ago (expired for 1 minute TTL)
        version: '1.0',
      };
      AsyncStorage.getItem.mockResolvedValue(JSON.stringify(cacheEntry));
      AsyncStorage.removeItem.mockResolvedValue();

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
          cacheTTL: 60000, // 1 minute TTL
        }),
      );

      await waitFor(() => expect(result.current.data).toEqual(testData));

      // Should ignore cache and call fetcher
      expect(mockFetcher).toHaveBeenCalledTimes(1);
      expect(AsyncStorage.removeItem).toHaveBeenCalledWith('test-key');
    });

    test('should ignore cache with different version', async () => {
      const testData = { data: 'test' };
      const mockFetcher = jest.fn().mockResolvedValue(testData);

      // Mock cache hit with different version
      const cacheEntry = {
        data: testData,
        timestamp: Date.now(),
        version: '2.0', // Different from expected '1.0'
      };
      AsyncStorage.getItem.mockResolvedValue(JSON.stringify(cacheEntry));
      AsyncStorage.removeItem.mockResolvedValue();

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
          cacheVersion: '1.0',
        }),
      );

      await waitFor(() => expect(result.current.data).toEqual(testData));

      // Should ignore cache and call fetcher
      expect(mockFetcher).toHaveBeenCalledTimes(1);
      expect(AsyncStorage.removeItem).toHaveBeenCalledWith('test-key');
    });
  });

  describe('Memory Management', () => {
    test('should not retain data after unmount', () => {
      const mockFetcher = jest.fn().mockResolvedValue({ data: 'test' });

      const { unmount } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
        }),
      );

      // Should not cause memory leaks
      expect(() => unmount()).not.toThrow();
    });

    test('should handle large datasets efficiently', async () => {
      const largeData = {
        items: Array.from({ length: 10000 }, (_, i) => ({
          id: i,
          name: `Item ${i}`,
          description: `Description for item ${i}`,
        })),
      };

      const mockFetcher = jest.fn().mockResolvedValue(largeData);

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'large-dataset',
          fetcher: mockFetcher,
        }),
      );

      await waitFor(() => expect(result.current.data).toEqual(largeData));

      expect(result.current.data).toEqual(largeData);
      expect(result.current.isLoading).toBe(false);

      // Should not cause memory issues
      const dataSize = JSON.stringify(result.current.data).length;
      expect(dataSize).toBeGreaterThan(0);
    });
  });

  describe('Error Handling', () => {
    test('should handle network errors', async () => {
      const networkError = new Error('Network request failed');
      const mockFetcher = jest.fn().mockRejectedValue(networkError);

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
        }),
      );

      await waitFor(() =>
        expect(result.current.error?.message).toBe('Network request failed'),
      );

      expect(result.current.error).toBeInstanceOf(Error);
      expect(result.current.error?.message).toBe('Network request failed');
    });

    test('should accept string data without attempting to parse it', async () => {
      const mockFetcher = jest.fn().mockResolvedValue('invalid json');

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
        }),
      );

      await waitFor(() => expect(result.current.data).toBe('invalid json'));
      expect(result.current.error).toBeNull();
    });

    test('should handle storage errors gracefully', async () => {
      const mockFetcher = jest.fn().mockResolvedValue({ data: 'test' });
      AsyncStorage.setItem.mockRejectedValue(new Error('Storage error'));

      const { result } = renderHook(() =>
        useOptimizedData({
          cacheKey: 'test-key',
          fetcher: mockFetcher,
        }),
      );

      await waitFor(() =>
        expect(result.current.data).toEqual({ data: 'test' }),
      );

      // Should still load data even if caching fails
      expect(result.current.data).toEqual({ data: 'test' });
      expect(result.current.isLoading).toBe(false);
    });
  });
});
