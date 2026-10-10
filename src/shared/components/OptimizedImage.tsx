import React, { memo, useState } from 'react';
import {
  View,
  Image,
  ImageStyle,
  ActivityIndicator,
  StyleSheet,
  ImageResizeMode,
} from 'react-native';

interface OptimizedImageProps {
  source: { uri: string } | number;
  style: ImageStyle;
  resizeMode?: ImageResizeMode;
  fallbackSource?: { uri: string } | number;
  showLoading?: boolean;
  onLoadStart?: () => void;
  onLoadEnd?: () => void;
  onError?: (error: any) => void;
}

/**
 * Optimized image component with lazy loading and caching
 * Uses React Native's built-in image loader to avoid a native dependency.
 */
export const OptimizedImage = memo<OptimizedImageProps>(
  ({
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

    return (
      <View style={style}>
        {hasError && fallbackSource ? (
          <OptimizedImage
            source={fallbackSource}
            style={StyleSheet.absoluteFillObject}
            resizeMode={resizeMode}
          />
        ) : (
          <Image
            style={StyleSheet.absoluteFillObject}
            source={source}
            resizeMode={resizeMode}
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
  },
);

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
