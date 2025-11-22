import React, { memo, useState } from 'react';
import { View, Image, ImageStyle, ActivityIndicator, StyleSheet } from 'react-native';
import FastImage, { FastImageProps, ImageStyle as FastImageStyle } from 'react-native-fast-image';

interface OptimizedImageProps {
  source: { uri: string } | number;
  style: ImageStyle | FastImageStyle;
  resizeMode?: 'contain' | 'cover' | 'stretch' | 'repeat' | 'center';
  fallbackSource?: { uri: string } | number;
  showLoading?: boolean;
  onLoadStart?: () => void;
  onLoadEnd?: () => void;
  onError?: (error: any) => void;
}

/**
 * Optimized image component with lazy loading and caching
 * Uses react-native-fast-image for better performance and memory management
 */
export const OptimizedImage = memo<OptimizedImageProps>(({
  source,
  style,
  resizeMode = 'cover',
  fallbackSource,
  showLoading = true,
  onLoadStart,
  onLoadEnd,
  onError,
}) => {
  const [isLoading, setIsLoading] = useState(false);
  const [hasError, setHasError] = useState(false);

  const handleLoadStart = () => {
    setIsLoading(true);
    setHasError(false);
    onLoadStart?.();
  };

  const handleLoadEnd = () => {
    setIsLoading(false);
    onLoadEnd?.();
  };

  const handleError = (error: any) => {
    setIsLoading(false);
    setHasError(true);
    onError?.(error);
  };

  // For local assets (numbers), use regular Image component
  if (typeof source === 'number') {
    return (
      <View style={style}>
        <Image
          source={source}
          style={StyleSheet.absoluteFillObject}
          resizeMode={resizeMode}
          onLoadStart={handleLoadStart}
          onLoadEnd={handleLoadEnd}
        />
        {showLoading && isLoading && (
          <View style={styles.loadingOverlay}>
            <ActivityIndicator size="small" color="#2E7D32" />
          </View>
        )}
      </View>
    );
  }

  // For remote images, use FastImage with caching
  return (
    <View style={style}>
      {hasError && fallbackSource ? (
        <OptimizedImage
          source={fallbackSource}
          style={StyleSheet.absoluteFillObject}
          resizeMode={resizeMode}
        />
      ) : (
        <FastImage
          style={StyleSheet.absoluteFillObject}
          source={{
            uri: (source as { uri: string }).uri,
            priority: FastImage.priority.normal,
            cache: FastImage.cacheControl.immutable,
          }}
          resizeMode={FastImage.resizeMode[resizeMode]}
          onLoadStart={handleLoadStart}
          onLoadEnd={handleLoadEnd}
          onError={handleError}
        />
      )}
      {showLoading && isLoading && (
        <View style={styles.loadingOverlay}>
          <ActivityIndicator size="small" color="#2E7D32" />
        </View>
      )}
    </View>
  );
});

OptimizedImage.displayName = 'OptimizedImage';

const styles = StyleSheet.create({
  loadingOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0, 0, 0, 0.1)',
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default OptimizedImage;