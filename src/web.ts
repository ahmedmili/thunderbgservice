import { WebPlugin } from '@capacitor/core';
import type { ThunderBgServicePlugin, StartOptions, RegisterTaskOptions, GeofenceOptions, MetricsData, ThemeConfig } from './definitions';

export class ThunderBgServiceWeb extends WebPlugin implements ThunderBgServicePlugin {
  async start(_options: StartOptions): Promise<{ started: boolean }> { return { started: true }; }
  async stop(): Promise<{ stopped: boolean }> { return { stopped: true }; }
  async update(_options: Partial<StartOptions>): Promise<{ updated: boolean }> { return { updated: true }; }
  async registerTask(_options: RegisterTaskOptions): Promise<{ registered: boolean }> { return { registered: true }; }
  async unregisterTask(_taskId: string): Promise<{ unregistered: boolean }> { return { unregistered: true }; }
  async getTaskResult(_taskId: string): Promise<{ result: any | null }> { return { result: null }; }
  async addListener(event: 'taskEvent', listener: (data: { taskId: string; data: any; timestamp: number }) => void): Promise<{ remove: () => Promise<void> }> {
    const handle = await super.addListener(event, listener);
    return { remove: () => handle.remove() };
  }
  async removeAllListeners(): Promise<void> {}
  async addGeofence(_options: GeofenceOptions): Promise<{ added: boolean }> { return { added: true }; }
  async removeGeofence(_geofenceId: string): Promise<{ removed: boolean }> { return { removed: true }; }
  async removeAllGeofences(): Promise<{ removed: boolean }> { return { removed: true }; }
  async getMetrics(): Promise<{ metrics: MetricsData }> { return { metrics: {} }; }
  async resetMetrics(): Promise<{ reset: boolean }> { return { reset: true }; }
  async setTheme(_themeName: string): Promise<{ success: boolean; themeName?: string }> { return { success: true, themeName: _themeName }; }
  async createTheme(_themeName: string, _theme: ThemeConfig): Promise<{ success: boolean }> { return { success: true }; }
  async getCurrentTheme(): Promise<ThemeConfig> { return { name: 'default' }; }
  async removeTheme(_themeName: string): Promise<{ success: boolean }> { return { success: true }; }
}


