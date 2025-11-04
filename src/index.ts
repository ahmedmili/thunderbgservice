import { registerPlugin } from '@capacitor/core';
import type { ThunderBgServicePlugin } from './definitions';
export const ThunderBgService = registerPlugin<ThunderBgServicePlugin>('ThunderBgService', {
  web: () => import('./web.js').then(m => new m.ThunderBgServiceWeb()),
});
export * from './definitions';

