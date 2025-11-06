/**
 * Tests unitaires pour les définitions TypeScript
 */
import { StartOptions, GeofenceOptions, ThemeConfig, MetricsData } from '../src/definitions';

describe('Type Definitions', () => {
  describe('StartOptions', () => {
    it('should have required notificationTitle', () => {
      const valid: StartOptions = {
        notificationTitle: 'Test Service',
      };
      expect(valid.notificationTitle).toBe('Test Service');
    });

    it('should accept optional properties', () => {
      const withOptions: StartOptions = {
        notificationTitle: 'Test',
        notificationSubtitle: 'Subtitle',
        enableLocation: true,
        soundsEnabled: false,
      };
      expect(withOptions.notificationSubtitle).toBe('Subtitle');
      expect(withOptions.enableLocation).toBe(true);
      expect(withOptions.soundsEnabled).toBe(false);
    });
  });

  describe('GeofenceOptions', () => {
    it('should have required properties', () => {
      const geofence: GeofenceOptions = {
        id: 'test-geofence',
        latitude: 48.8566,
        longitude: 2.3522,
        radius: 100,
      };
      expect(geofence.id).toBe('test-geofence');
      expect(geofence.latitude).toBe(48.8566);
      expect(geofence.longitude).toBe(2.3522);
      expect(geofence.radius).toBe(100);
    });

    it('should accept optional onEnter and onExit', () => {
      const geofence: GeofenceOptions = {
        id: 'test',
        latitude: 0,
        longitude: 0,
        radius: 50,
        onEnter: 'ACTION_ENTER',
        onExit: 'ACTION_EXIT',
      };
      expect(geofence.onEnter).toBe('ACTION_ENTER');
      expect(geofence.onExit).toBe('ACTION_EXIT');
    });

    it('should accept extras', () => {
      const geofence: GeofenceOptions = {
        id: 'test',
        latitude: 0,
        longitude: 0,
        radius: 50,
        extras: {
          clientId: '123',
          name: 'Test Location',
        },
      };
      expect(geofence.extras?.clientId).toBe('123');
      expect(geofence.extras?.name).toBe('Test Location');
    });
  });

  describe('ThemeConfig', () => {
    it('should have required name property', () => {
      const theme: ThemeConfig = {
        name: 'my-theme',
      };
      expect(theme.name).toBe('my-theme');
    });

    it('should accept color properties', () => {
      const theme: ThemeConfig = {
        name: 'test',
        backgroundColor: '#FFFFFF',
        titleColor: '#000000',
        subtitleColor: '#666666',
        accentColor: '#2196F3',
      };
      expect(theme.backgroundColor).toBe('#FFFFFF');
      expect(theme.titleColor).toBe('#000000');
      expect(theme.accentColor).toBe('#2196F3');
    });

    it('should accept font properties', () => {
      const theme: ThemeConfig = {
        name: 'test',
        fontSize: 16,
        fontFamily: 'Roboto',
      };
      expect(theme.fontSize).toBe(16);
      expect(theme.fontFamily).toBe('Roboto');
    });
  });

  describe('MetricsData', () => {
    it('should have optional metrics properties', () => {
      const metrics: MetricsData = {
        taskExecutionCount: 100,
        avgTaskExecutionTime: 50.5,
        notificationUpdateCount: 25,
        locationUpdateCount: 150,
        geofenceTriggerCount: 10,
        serviceUptime: 3600000,
        serviceUptimeHours: 1.0,
        currentBatteryLevel: 80,
        batteryDrain: 5,
      };
      expect(metrics.taskExecutionCount).toBe(100);
      expect(metrics.avgTaskExecutionTime).toBe(50.5);
      expect(metrics.currentBatteryLevel).toBe(80);
    });

    it('should have optional resourceCache', () => {
      const metrics: MetricsData = {
        resourceCache: {
          hits: 100,
          misses: 10,
          size: 50,
          hitRate: 90.9,
        },
      };
      expect(metrics.resourceCache?.hits).toBe(100);
      expect(metrics.resourceCache?.hitRate).toBe(90.9);
    });
  });
});

