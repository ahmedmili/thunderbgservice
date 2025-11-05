/**
 * Tests d'intégration pour le plugin principal
 */
import { ThunderBgService } from '../src/index';
import type { ThemeConfig } from '../src/definitions';

// Mock du plugin Capacitor
const mockPlugin = {
  start: jest.fn(),
  stop: jest.fn(),
  update: jest.fn(),
  registerTask: jest.fn(),
  unregisterTask: jest.fn(),
  getTaskResult: jest.fn(),
  addGeofence: jest.fn(),
  removeGeofence: jest.fn(),
  removeAllGeofences: jest.fn(),
  getMetrics: jest.fn(),
  resetMetrics: jest.fn(),
  setTheme: jest.fn(),
  createTheme: jest.fn(),
  getCurrentTheme: jest.fn(),
  removeTheme: jest.fn(),
  addListener: jest.fn(),
  removeAllListeners: jest.fn(),
};

jest.mock('@capacitor/core', () => ({
  registerPlugin: jest.fn(() => mockPlugin),
}));

describe('ThunderBgService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('start', () => {
    it('should call plugin start with options', async () => {
      mockPlugin.start.mockResolvedValue({ started: true });
      
      const result = await ThunderBgService.start({
        notificationTitle: 'Test Service',
        notificationSubtitle: 'Testing',
        enableLocation: true,
      });

      expect(mockPlugin.start).toHaveBeenCalledWith({
        notificationTitle: 'Test Service',
        notificationSubtitle: 'Testing',
        enableLocation: true,
      });
      expect(result.started).toBe(true);
    });

    it('should handle minimal options', async () => {
      mockPlugin.start.mockResolvedValue({ started: true });
      
      await ThunderBgService.start({
        notificationTitle: 'Minimal',
      });

      expect(mockPlugin.start).toHaveBeenCalledWith({
        notificationTitle: 'Minimal',
      });
    });
  });

  describe('stop', () => {
    it('should call plugin stop', async () => {
      mockPlugin.stop.mockResolvedValue({ stopped: true });
      
      const result = await ThunderBgService.stop();

      expect(mockPlugin.stop).toHaveBeenCalled();
      expect(result.stopped).toBe(true);
    });
  });

  describe('update', () => {
    it('should call plugin update with partial options', async () => {
      mockPlugin.update.mockResolvedValue({ updated: true });
      
      const result = await ThunderBgService.update({
        notificationTitle: 'Updated Title',
      });

      expect(mockPlugin.update).toHaveBeenCalledWith({
        notificationTitle: 'Updated Title',
      });
      expect(result.updated).toBe(true);
    });
  });

  describe('geofencing', () => {
    it('should add geofence', async () => {
      mockPlugin.addGeofence.mockResolvedValue({ added: true });
      
      const result = await ThunderBgService.addGeofence({
        id: 'test-geofence',
        latitude: 48.8566,
        longitude: 2.3522,
        radius: 100,
      });

      expect(mockPlugin.addGeofence).toHaveBeenCalledWith({
        id: 'test-geofence',
        latitude: 48.8566,
        longitude: 2.3522,
        radius: 100,
      });
      expect(result.added).toBe(true);
    });

    it('should remove geofence', async () => {
      mockPlugin.removeGeofence.mockResolvedValue({ removed: true });
      
      const result = await ThunderBgService.removeGeofence('test-geofence');

      expect(mockPlugin.removeGeofence).toHaveBeenCalledWith('test-geofence');
      expect(result.removed).toBe(true);
    });

    it('should remove all geofences', async () => {
      mockPlugin.removeAllGeofences.mockResolvedValue({ removed: true });
      
      const result = await ThunderBgService.removeAllGeofences();

      expect(mockPlugin.removeAllGeofences).toHaveBeenCalled();
      expect(result.removed).toBe(true);
    });
  });

  describe('metrics', () => {
    it('should get metrics', async () => {
      const mockMetrics = {
        taskExecutionCount: 100,
        avgTaskExecutionTime: 50.5,
      };
      mockPlugin.getMetrics.mockResolvedValue({ metrics: mockMetrics });
      
      const result = await ThunderBgService.getMetrics();

      expect(mockPlugin.getMetrics).toHaveBeenCalled();
      expect(result.metrics).toEqual(mockMetrics);
    });

    it('should reset metrics', async () => {
      mockPlugin.resetMetrics.mockResolvedValue({ reset: true });
      
      const result = await ThunderBgService.resetMetrics();

      expect(mockPlugin.resetMetrics).toHaveBeenCalled();
      expect(result.reset).toBe(true);
    });
  });

  describe('themes', () => {
    it('should set theme', async () => {
      mockPlugin.setTheme.mockResolvedValue({ success: true, themeName: 'dark' });
      
      const result = await ThunderBgService.setTheme('dark');

      expect(mockPlugin.setTheme).toHaveBeenCalledWith('dark');
      expect(result.success).toBe(true);
      expect(result.themeName).toBe('dark');
    });

    it('should create theme', async () => {
      mockPlugin.createTheme.mockResolvedValue({ success: true });
      
      const theme: ThemeConfig = {
        name: 'custom',
        backgroundColor: '#FFFFFF',
        titleColor: '#000000',
      };
      
      const result = await ThunderBgService.createTheme('custom', theme);

      expect(mockPlugin.createTheme).toHaveBeenCalledWith('custom', theme);
      expect(result.success).toBe(true);
    });

    it('should get current theme', async () => {
      const mockTheme = {
        name: 'dark',
        backgroundColor: '#212121',
        titleColor: '#FFFFFF',
      };
      mockPlugin.getCurrentTheme.mockResolvedValue(mockTheme);
      
      const result = await ThunderBgService.getCurrentTheme();

      expect(mockPlugin.getCurrentTheme).toHaveBeenCalled();
      expect(result).toEqual(mockTheme);
    });

    it('should remove theme', async () => {
      mockPlugin.removeTheme.mockResolvedValue({ success: true });
      
      const result = await ThunderBgService.removeTheme('custom');

      expect(mockPlugin.removeTheme).toHaveBeenCalledWith('custom');
      expect(result.success).toBe(true);
    });
  });

  describe('tasks', () => {
    it('should register task', async () => {
      mockPlugin.registerTask.mockResolvedValue({ registered: true });
      
      const result = await ThunderBgService.registerTask({
        taskId: 'test-task',
        taskClass: 'com.example.TestTask',
        intervalMs: 5000,
      });

      expect(mockPlugin.registerTask).toHaveBeenCalledWith({
        taskId: 'test-task',
        taskClass: 'com.example.TestTask',
        intervalMs: 5000,
      });
      expect(result.registered).toBe(true);
    });

    it('should unregister task', async () => {
      mockPlugin.unregisterTask.mockResolvedValue({ unregistered: true });
      
      const result = await ThunderBgService.unregisterTask('test-task');

      expect(mockPlugin.unregisterTask).toHaveBeenCalledWith('test-task');
      expect(result.unregistered).toBe(true);
    });

    it('should get task result', async () => {
      const mockResult = { data: 'test', timestamp: 1234567890 };
      mockPlugin.getTaskResult.mockResolvedValue({ result: mockResult });
      
      const result = await ThunderBgService.getTaskResult('test-task');

      expect(mockPlugin.getTaskResult).toHaveBeenCalledWith('test-task');
      expect(result.result).toEqual(mockResult);
    });
  });

  describe('listeners', () => {
    it('should add listener', async () => {
      const listener = jest.fn();
      mockPlugin.addListener.mockResolvedValue({ remove: jest.fn() });
      
      const result = await ThunderBgService.addListener('taskEvent', listener);

      expect(mockPlugin.addListener).toHaveBeenCalledWith('taskEvent', listener);
      expect(result.remove).toBeDefined();
    });

    it('should remove all listeners', async () => {
      mockPlugin.removeAllListeners.mockResolvedValue(undefined);
      
      await ThunderBgService.removeAllListeners();

      expect(mockPlugin.removeAllListeners).toHaveBeenCalled();
    });
  });
});

