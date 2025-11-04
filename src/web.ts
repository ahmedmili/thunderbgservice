import { WebPlugin } from '@capacitor/core';
import type { ThunderBgServicePlugin, StartOptions, RegisterTaskOptions } from './definitions';

export class ThunderBgServiceWeb extends WebPlugin implements ThunderBgServicePlugin {
  async start(_options: StartOptions): Promise<{ started: boolean }> { return { started: true }; }
  async stop(): Promise<{ stopped: boolean }> { return { stopped: true }; }
  async update(_options: Partial<StartOptions>): Promise<{ updated: boolean }> { return { updated: true }; }
  async registerTask(_options: RegisterTaskOptions): Promise<{ registered: boolean }> { return { registered: true }; }
  async unregisterTask(_taskId: string): Promise<{ unregistered: boolean }> { return { unregistered: true }; }
  async getTaskResult(_taskId: string): Promise<{ result: any | null }> { return { result: null }; }
}


