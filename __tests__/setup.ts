/**
 * Configuration globale pour les tests Jest
 */

// Mock Capacitor Core
jest.mock('@capacitor/core', () => ({
  Capacitor: {
    getPlatform: jest.fn(() => 'android'),
    isNativePlatform: jest.fn(() => true),
    isPluginAvailable: jest.fn(() => true),
  },
  registerPlugin: jest.fn(),
}));

// Mock console pour éviter le bruit dans les tests
global.console = {
  ...console,
  log: jest.fn(),
  debug: jest.fn(),
  info: jest.fn(),
  warn: jest.fn(),
  error: jest.fn(),
};

// Timeout global pour les tests
jest.setTimeout(10000);

