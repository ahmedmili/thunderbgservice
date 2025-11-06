"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ThunderBgServiceWeb = void 0;
const core_1 = require("@capacitor/core");
class ThunderBgServiceWeb extends core_1.WebPlugin {
    async start(_options) { return { started: true }; }
    async stop() { return { stopped: true }; }
    async update(_options) { return { updated: true }; }
    async registerTask(_options) { return { registered: true }; }
    async unregisterTask(_taskId) { return { unregistered: true }; }
    async getTaskResult(_taskId) { return { result: null }; }
    async addListener(event, listener) {
        const handle = await super.addListener(event, listener);
        return { remove: () => handle.remove() };
    }
    async removeAllListeners() { }
    async addGeofence(_options) { return { added: true }; }
    async removeGeofence(_geofenceId) { return { removed: true }; }
    async removeAllGeofences() { return { removed: true }; }
    async getMetrics() { return { metrics: {} }; }
    async resetMetrics() { return { reset: true }; }
    async setTheme(_themeName) { return { success: true, themeName: _themeName }; }
    async createTheme(_themeName, _theme) { return { success: true }; }
    async getCurrentTheme() { return { name: 'default' }; }
    async removeTheme(_themeName) { return { success: true }; }
}
exports.ThunderBgServiceWeb = ThunderBgServiceWeb;
