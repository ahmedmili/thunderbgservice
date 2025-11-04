import Foundation
import Capacitor

@objc(ThunderBgServicePlugin)
public class ThunderBgServicePlugin: CAPPlugin {
    @objc func start(_ call: CAPPluginCall) { call.resolve(["started": true]) }
    @objc func stop(_ call: CAPPluginCall) { call.resolve(["stopped": true]) }
    @objc func update(_ call: CAPPluginCall) { call.resolve(["updated": true]) }
}


