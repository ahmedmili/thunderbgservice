import { WebPlugin } from '@capacitor/core';
import type { ThunderBgServicePlugin, StartOptions } from './definitions';
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
}
