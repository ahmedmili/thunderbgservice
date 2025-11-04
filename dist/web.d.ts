import { WebPlugin } from '@capacitor/core';
import type { ThunderBgServicePlugin, StartOptions, RegisterTaskOptions } from './definitions';
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
}
