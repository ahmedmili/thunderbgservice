import { WebPlugin } from '@capacitor/core';
import type { ThunderBgServicePlugin, StartOptions, RegisterTaskOptions, GeofenceOptions, MetricsData, ThemeConfig } from './definitions';
export declare class ThunderBgServiceWeb extends WebPlugin implements ThunderBgServicePlugin {
    start(_options: StartOptions): Promise<{
        started: boolean;
    }>;
    stop(): Promise<{
        stopped: boolean;
    }>;
    update(_options: Partial<StartOptions>): Promise<{
        updated: boolean;
    }>;
    registerTask(_options: RegisterTaskOptions): Promise<{
        registered: boolean;
    }>;
    unregisterTask(_taskId: string): Promise<{
        unregistered: boolean;
    }>;
    getTaskResult(_taskId: string): Promise<{
        result: any | null;
    }>;
    addListener(event: 'taskEvent', listener: (data: {
        taskId: string;
        data: any;
        timestamp: number;
    }) => void): Promise<{
        remove: () => Promise<void>;
    }>;
    removeAllListeners(): Promise<void>;
    addGeofence(_options: GeofenceOptions): Promise<{
        added: boolean;
    }>;
    removeGeofence(_geofenceId: string): Promise<{
        removed: boolean;
    }>;
    removeAllGeofences(): Promise<{
        removed: boolean;
    }>;
    getMetrics(): Promise<{
        metrics: MetricsData;
    }>;
    resetMetrics(): Promise<{
        reset: boolean;
    }>;
    setTheme(_themeName: string): Promise<{
        success: boolean;
        themeName?: string;
    }>;
    createTheme(_themeName: string, _theme: ThemeConfig): Promise<{
        success: boolean;
    }>;
    getCurrentTheme(): Promise<ThemeConfig>;
    removeTheme(_themeName: string): Promise<{
        success: boolean;
    }>;
}
