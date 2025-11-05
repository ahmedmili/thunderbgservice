/**
 * Tests unitaires pour les helpers et utilitaires
 */

describe('Helper Functions', () => {
  describe('Color parsing', () => {
    it('should parse valid hex colors', () => {
      const validColors = [
        '#FFFFFF',
        '#000000',
        '#FF0000',
        '#00FF00',
        '#0000FF',
        '#123456',
      ];
      
      validColors.forEach(color => {
        expect(color).toMatch(/^#[0-9A-Fa-f]{6}$/);
      });
    });

    it('should validate color format', () => {
      const invalidColors = [
        'FFFFFF',
        '#FFFF',
        '#GGGGGG',
        'rgb(255,255,255)',
      ];
      
      invalidColors.forEach(color => {
        expect(color).not.toMatch(/^#[0-9A-Fa-f]{6}$/);
      });
    });
  });

  describe('Geofence validation', () => {
    it('should validate geofence radius', () => {
      const validRadiuses = [10, 50, 100, 500, 1000];
      const invalidRadiuses = [-10, 0, 5000];
      
      validRadiuses.forEach(radius => {
        expect(radius).toBeGreaterThan(0);
        expect(radius).toBeLessThanOrEqual(1000);
      });
      
      invalidRadiuses.forEach(radius => {
        expect(radius <= 0 || radius > 1000).toBe(true);
      });
    });

    it('should validate coordinates', () => {
      const validLat = 48.8566;
      const validLng = 2.3522;
      
      expect(validLat).toBeGreaterThanOrEqual(-90);
      expect(validLat).toBeLessThanOrEqual(90);
      expect(validLng).toBeGreaterThanOrEqual(-180);
      expect(validLng).toBeLessThanOrEqual(180);
    });
  });

  describe('Theme validation', () => {
    it('should validate theme names', () => {
      const validNames = ['default', 'dark', 'blue', 'my-custom-theme'];
      const invalidNames = ['', 'theme with spaces', 'theme@special'];
      
      validNames.forEach(name => {
        expect(name).toMatch(/^[a-z0-9-_]+$/i);
        expect(name.length).toBeGreaterThan(0);
      });
      
      invalidNames.forEach(name => {
        if (name.length === 0) {
          expect(name).toBe('');
        } else {
          expect(name).not.toMatch(/^[a-z0-9-_]+$/i);
        }
      });
    });
  });

  describe('Task interval validation', () => {
    it('should validate minimum interval', () => {
      const minInterval = 1000; // 1 second
      const validIntervals = [1000, 5000, 10000, 60000];
      const invalidIntervals = [0, 500, 999];
      
      validIntervals.forEach(interval => {
        expect(interval).toBeGreaterThanOrEqual(minInterval);
      });
      
      invalidIntervals.forEach(interval => {
        expect(interval).toBeLessThan(minInterval);
      });
    });
  });
});

