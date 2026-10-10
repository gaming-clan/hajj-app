module.exports = {
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaFeatures: { jsx: true },
    ecmaVersion: 2021,
    sourceType: 'module',
  },
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true,
    jest: true,
    'react-native/react-native': true,
  },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:react/recommended',
    'plugin:react-native/all',
    'plugin:prettier/recommended',
  ],
  parserOptions: {
    ecmaFeatures: {
      jsx: true,
    },
    ecmaVersion: 12,
    sourceType: 'module',
  },
  plugins: ['react', 'react-native', 'prettier'],
  rules: {
    'prettier/prettier': 'error',
    'no-undef': 'off',
    'no-unused-vars': 'off',
    'react/prop-types': 'off',
    'react/no-unescaped-entities': 'off',
    'react-native/sort-styles': 'off',
    'react/jsx-uses-react': 'off',
    'react/react-in-jsx-scope': 'off',
    'react-native/no-unused-styles': 2,
    'react-native/split-platform-components': 2,
    'react-native/no-inline-styles': 0,
    'react-native/no-color-literals': 0,
    'react-native/no-raw-text': 2,
    'react-native/no-single-element-style-arrays': 2,
  },
  settings: {
    react: {
      version: 'detect',
    },
  },
};
