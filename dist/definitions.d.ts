export interface StartOptions {
    notificationTitle: string;
    notificationSubtitle?: string;
    enableLocation?: boolean;
    soundsEnabled?: boolean;
    [k: string]: any;
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
}
