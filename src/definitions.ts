export interface StartOptions { notificationTitle: string; notificationSubtitle?: string; enableLocation?: boolean; soundsEnabled?: boolean; [k: string]: any; }
export interface RegisterTaskOptions {
  taskId: string;
  taskClass: string;  // Nom complet de la classe Java (ex: "com.yourpackage.MyTask")
  intervalMs: number; // Intervalle en millisecondes (minimum 1000)
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
}

