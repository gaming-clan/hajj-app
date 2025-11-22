const { getDefaultConfig, mergeConfig } = require("@react-native/metro-config");
const path = require('path');

/**
 * Metro configuration for React Native bundle optimization
 * Improves build performance and reduces bundle size for Islamic content app
 *
 * @type {import('metro-config').MetroConfig}
 */
const defaultConfig = getDefaultConfig(__dirname);

const {
  assetExts,
  sourceExts,
} = defaultConfig.resolver;

const config = {
  transformer: {
    // Enable Hermes for better performance
    hermesParser: true,

    // Optimize JavaScript bundling
    minifierConfig: {
      keep_classnames: false,
      keep_fnames: false,
      mangle: {
        keep_classnames: false,
        keep_fnames: false,
      },
    },

    // Enable babel transformer for better performance
    babelTransformerPath: require.resolve('metro-react-native-babel-transformer'),
  },

  resolver: {
    // Asset extensions including Islamic image formats
    assetExts: assetExts.filter(ext => ext !== 'svg').concat(['svg', 'jpg', 'jpeg', 'png', 'gif', 'webp']),

    // Source extensions for TypeScript support
    sourceExts: [...sourceExts, 'jsx', 'ts', 'tsx', 'js'],

    // Alias for cleaner imports in the Hajj app
    alias: {
      '@components': path.resolve(__dirname, 'src/shared/components'),
      '@hooks': path.resolve(__dirname, 'src/shared/hooks'),
      '@screens': path.resolve(__dirname, 'src/features'),
      '@services': path.resolve(__dirname, 'src/features/content/services'),
      '@types': path.resolve(__dirname, 'src/core/types'),
      '@utils': path.resolve(__dirname, 'src/core/utils'),
      '@assets': path.resolve(__dirname, 'src/assets'),
      '@content': path.resolve(__dirname, 'src/assets/data'),
    },

    // Optimize module resolution
    platforms: ['ios', 'android', 'native'],
  },

  // Server configuration for better development experience
  server: {
    port: process.env.METRO_PORT || 8081,
  },

  // Max workers for parallel processing
  maxWorkers: 2,

  // Configuration for watch mode
  watchFolders: [path.resolve(__dirname, 'src')],

  // Enable source maps for debugging (disabled in production)
  production: process.env.NODE_ENV === 'production' ? {
    sourceMap: false,
    inlineSourceMap: false,
  } : {
    sourceMap: true,
    inlineSourceMap: false,
  },
};

module.exports = mergeConfig(defaultConfig, config);
