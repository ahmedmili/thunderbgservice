import Foundation
import CoreLocation

/**
 * Helper pour gérer la localisation en arrière-plan sur iOS
 */
class LocationHelper: NSObject, CLLocationManagerDelegate {
    static let shared = LocationHelper()
    
    private let locationManager = CLLocationManager()
    private var isTracking = false
    private var locationUpdateHandler: ((CLLocation) -> Void)?
    
    private override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.pausesLocationUpdatesAutomatically = false
    }
    
    /**
     * Démarre le suivi de localisation
     */
    func startLocationUpdates(handler: ((CLLocation) -> Void)? = nil) {
        guard !isTracking else {
            print("ThunderBG: Location tracking already started")
            return
        }
        
        locationUpdateHandler = handler
        
        // Demander les permissions
        let status = locationManager.authorizationStatus
        
        switch status {
        case .notDetermined:
            locationManager.requestAlwaysAuthorization()
        case .authorizedWhenInUse:
            // Essayer de demander l'autorisation toujours
            locationManager.requestAlwaysAuthorization()
        case .authorizedAlways:
            // Déjà autorisé, démarrer
            startTracking()
        case .denied, .restricted:
            print("ThunderBG: Location permission denied")
        @unknown default:
            break
        }
    }
    
    /**
     * Arrête le suivi de localisation
     */
    func stopLocationUpdates() {
        guard isTracking else { return }
        
        locationManager.stopUpdatingLocation()
        isTracking = false
        locationUpdateHandler = nil
        print("ThunderBG: Location tracking stopped")
    }
    
    /**
     * Démarre réellement le tracking
     */
    private func startTracking() {
        locationManager.startUpdatingLocation()
        locationManager.startMonitoringSignificantLocationChanges()
        isTracking = true
        print("ThunderBG: Location tracking started")
    }
    
    /**
     * Obtient la dernière localisation connue
     */
    func getLastKnownLocation() -> CLLocation? {
        return locationManager.location
    }
    
    // MARK: - CLLocationManagerDelegate
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        
        print("ThunderBG: Location updated: \(location.coordinate.latitude), \(location.coordinate.longitude)")
        locationUpdateHandler?(location)
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("ThunderBG: Location error: \(error.localizedDescription)")
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        let status = manager.authorizationStatus
        
        switch status {
        case .authorizedAlways:
            if !isTracking {
                startTracking()
            }
        case .authorizedWhenInUse:
            print("ThunderBG: Location authorized when in use only")
        case .denied, .restricted:
            print("ThunderBG: Location authorization denied")
            stopLocationUpdates()
        default:
            break
        }
    }
}

