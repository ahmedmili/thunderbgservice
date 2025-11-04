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
}
exports.ThunderBgServiceWeb = ThunderBgServiceWeb;
