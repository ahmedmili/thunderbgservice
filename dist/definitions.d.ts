export interface StartOptions {
    notificationTitle: string;
    notificationSubtitle?: string;
    enableLocation?: boolean;
    soundsEnabled?: boolean;
    customLayout?: string;
    titleViewId?: string;
    subtitleViewId?: string;
    timerViewId?: string;
    viewData?: {
        [viewIdName: string]: string;
    };
    buttons?: Array<{
        viewId: string;
        action: string;
        extras?: {
            [key: string]: string;
        };
    }>;
}
export interface RegisterTaskOptions {
    taskId: string;
    taskClass: string;
    intervalMs: number;
}
export interface GeofenceOptions {
    id: string;
    latitude: number;
    longitude: number;
    radius: number;
    onEnter?: string;
    onExit?: string;
    extras?: {
        [key: string]: string;
    };
}
export interface MetricsData {
    taskExecutionCount?: number;
    totalTaskExecutionTime?: number;
    avgTaskExecutionTime?: number;
    tasks?: {
        [taskId: string]: any;
    };
    notificationUpdateCount?: number;
    locationUpdateCount?: number;
    location?: any;
    geofenceTriggerCount?: number;
    geofences?: any;
    serviceUptime?: number;
    serviceUptimeHours?: number;
    service?: any;
    currentBatteryLevel?: number;
    batteryDrain?: number;
    battery?: any;
    resourceCache?: {
        hits: number;
        misses: number;
        size: number;
        hitRate: number;
    };
    cache?: string;
}
export interface ThemeConfig {
    name: string;
    backgroundColor?: string;
    titleColor?: string;
    subtitleColor?: string;
    accentColor?: string;
    iconTintColor?: string;
    timerColor?: string;
    buttonBackgroundColor?: string;
    buttonTextColor?: string;
    fontSize?: number;
    fontFamily?: string;
}
export interface ThunderBgServicePlugin {
    start(options: StartOptions): Promise<{
        started: boolean;
    }>;
    stop(): Promise<{
        stopped: boolean;
    }>;
    update(options: Partial<StartOptions>): Promise<{
        updated: boolean;
    }>;
    registerTask(options: RegisterTaskOptions): Promise<{
        registered: boolean;
    }>;
    unregisterTask(taskId: string): Promise<{
        unregistered: boolean;
    }>;
    getTaskResult(taskId: string): Promise<{
        result: any | null;
    }>;
    addListener(event: 'taskEvent', listener: (data: {
        taskId: string;
        data: any;
        timestamp: number;
    }) => void): Promise<{
        remove: () => void;
    }>;
    removeAllListeners(): Promise<void>;
    addGeofence(options: GeofenceOptions): Promise<{
        added: boolean;
    }>;
    removeGeofence(geofenceId: string): Promise<{
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
    setTheme(themeName: string): Promise<{
        success: boolean;
        themeName?: string;
    }>;
    createTheme(themeName: string, theme: ThemeConfig): Promise<{
        success: boolean;
    }>;
    getCurrentTheme(): Promise<ThemeConfig>;
    removeTheme(themeName: string): Promise<{
        success: boolean;
    }>;
}
