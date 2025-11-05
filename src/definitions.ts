export interface StartOptions { notificationTitle: string; notificationSubtitle?: string; enableLocation?: boolean; soundsEnabled?: boolean; [k: string]: any; }
export interface RegisterTaskOptions {
  taskId: string;
  taskClass: string;  // Nom complet de la classe Java (ex: "com.yourpackage.MyTask")
  intervalMs: number; // Intervalle en millisecondes (minimum 1000)
}
export interface GeofenceOptions {
  id: string;
  latitude: number;
  longitude: number;
  radius: number; // en mètres
  onEnter?: string; // Action broadcast/notification lors de l'entrée
  onExit?: string; // Action broadcast/notification lors de la sortie
  extras?: { [key: string]: string }; // Données supplémentaires
}

export interface MetricsData {
  // Métriques de tâches
  taskExecutionCount?: number;
  totalTaskExecutionTime?: number;
  avgTaskExecutionTime?: number;
  tasks?: { [taskId: string]: any };
  
  // Métriques de notifications
  notificationUpdateCount?: number;
  
  // Métriques de localisation
  locationUpdateCount?: number;
  location?: any;
  
  // Métriques de géofences
  geofenceTriggerCount?: number;
  geofences?: any;
  
  // Métriques de service
  serviceUptime?: number;
  serviceUptimeHours?: number;
  service?: any;
  
  // Métriques de batterie
  currentBatteryLevel?: number;
  batteryDrain?: number;
  battery?: any;
  
  // Cache de ressources
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
  backgroundColor?: string; // Couleur hexadécimale (ex: "#FFFFFF")
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
  start(options: StartOptions): Promise<{ started: boolean }>;
  stop(): Promise<{ stopped: boolean }>;
  update(options: Partial<StartOptions>): Promise<{ updated: boolean }>;
  registerTask(options: RegisterTaskOptions): Promise<{ registered: boolean }>;
  unregisterTask(taskId: string): Promise<{ unregistered: boolean }>;
  getTaskResult(taskId: string): Promise<{ result: any | null }>;
  addListener(event: 'taskEvent', listener: (data: { taskId: string; data: any; timestamp: number }) => void): Promise<{ remove: () => void }>;
  removeAllListeners(): Promise<void>;
  addGeofence(options: GeofenceOptions): Promise<{ added: boolean }>;
  removeGeofence(geofenceId: string): Promise<{ removed: boolean }>;
  removeAllGeofences(): Promise<{ removed: boolean }>;
  getMetrics(): Promise<{ metrics: MetricsData }>;
  resetMetrics(): Promise<{ reset: boolean }>;
  setTheme(themeName: string): Promise<{ success: boolean; themeName?: string }>;
  createTheme(themeName: string, theme: ThemeConfig): Promise<{ success: boolean }>;
  getCurrentTheme(): Promise<ThemeConfig>;
  removeTheme(themeName: string): Promise<{ success: boolean }>;
}

