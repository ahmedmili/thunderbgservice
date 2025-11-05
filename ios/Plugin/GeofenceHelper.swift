import Foundation
import CoreLocation

/**
 * Helper pour gérer le géofencing sur iOS
 */
class GeofenceHelper: NSObject, CLLocationManagerDelegate {
    static let shared = GeofenceHelper()
    
    private let locationManager = CLLocationManager()
    private var activeRegions: [String: CLRegion] = [:]
    private var geofenceConfigs: [String: GeofenceConfig] = [:]
    
    /**
     * Configuration d'une géofence
     */
    struct GeofenceConfig {
        let id: String
        let latitude: Double
        let longitude: Double
        let radius: Double // en mètres
        let onEnterAction: String?
        let onExitAction: String?
        let extras: [String: String]?
    }
    
    private override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
    }
    
    /**
     * Ajoute une géofence
     */
    func addGeofence(config: GeofenceConfig) {
        // Vérifier les permissions
        let status = locationManager.authorizationStatus
        guard status == .authorizedAlways else {
            print("ThunderBG: Location permission not granted for geofencing")
            locationManager.requestAlwaysAuthorization()
            return
        }
        
        // Créer la région circulaire
        let center = CLLocationCoordinate2D(latitude: config.latitude, longitude: config.longitude)
        let region = CLCircularRegion(center: center, radius: config.radius, identifier: config.id)
        region.notifyOnEntry = config.onEnterAction != nil
        region.notifyOnExit = config.onExitAction != nil
        
        activeRegions[config.id] = region
        geofenceConfigs[config.id] = config
        
        // Démarrer le monitoring
        locationManager.startMonitoring(for: region)
        print("ThunderBG: Geofence added: \(config.id)")
    }
    
    /**
     * Supprime une géofence
     */
    func removeGeofence(id: String) {
        guard let region = activeRegions[id] else {
            print("ThunderBG: Geofence not found: \(id)")
            return
        }
        
        locationManager.stopMonitoring(for: region)
        activeRegions.removeValue(forKey: id)
        geofenceConfigs.removeValue(forKey: id)
        print("ThunderBG: Geofence removed: \(id)")
    }
    
    /**
     * Supprime toutes les géofences
     */
    func removeAllGeofences() {
        for (_, region) in activeRegions {
            locationManager.stopMonitoring(for: region)
        }
        activeRegions.removeAll()
        geofenceConfigs.removeAll()
        print("ThunderBG: All geofences removed")
    }
    
    /**
     * Obtient la configuration d'une géofence
     */
    func getGeofenceConfig(id: String) -> GeofenceConfig? {
        return geofenceConfigs[id]
    }
    
    // MARK: - CLLocationManagerDelegate
    
    func locationManager(_ manager: CLLocationManager, didEnterRegion region: CLRegion) {
        guard let config = geofenceConfigs[region.identifier],
              let action = config.onEnterAction else {
            return
        }
        
        // Émettre une notification pour l'événement
        NotificationCenter.default.post(
            name: NSNotification.Name("ThunderBGGeofenceEvent"),
            object: nil,
            userInfo: [
                "geofenceId": config.id,
                "eventType": "ENTER",
                "action": action,
                "latitude": config.latitude,
                "longitude": config.longitude,
                "extras": config.extras ?? [:]
            ]
        )
        
        print("ThunderBG: Geofence ENTER: \(config.id) -> \(action)")
    }
    
    func locationManager(_ manager: CLLocationManager, didExitRegion region: CLRegion) {
        guard let config = geofenceConfigs[region.identifier],
              let action = config.onExitAction else {
            return
        }
        
        // Émettre une notification pour l'événement
        NotificationCenter.default.post(
            name: NSNotification.Name("ThunderBGGeofenceEvent"),
            object: nil,
            userInfo: [
                "geofenceId": config.id,
                "eventType": "EXIT",
                "action": action,
                "latitude": config.latitude,
                "longitude": config.longitude,
                "extras": config.extras ?? [:]
            ]
        )
        
        print("ThunderBG: Geofence EXIT: \(config.id) -> \(action)")
    }
    
    func locationManager(_ manager: CLLocationManager, monitoringDidFailFor region: CLRegion?, withError error: Error) {
        print("ThunderBG: Geofence monitoring error: \(error.localizedDescription)")
    }
}

