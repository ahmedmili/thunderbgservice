import Foundation
import Capacitor
import UserNotifications

/**
 * Plugin principal pour ThunderBgService sur iOS
 * Implémente les mêmes fonctionnalités que la version Android
 */
@objc(ThunderBgServicePlugin)
public class ThunderBgServicePlugin: CAPPlugin {
    
    private let notificationHelper = NotificationHelper.shared
    private let locationHelper = LocationHelper.shared
    private let taskManager = BackgroundTaskManager.shared
    private let geofenceHelper = GeofenceHelper.shared
    
    // Stockage des résultats de tâches
    private var taskResults: [String: Any] = [:]
    private var taskEventListeners: [String: (Any) -> Void] = [:]
    
    /**
     * Démarre le service foreground avec notification
     */
    @objc func start(_ call: CAPPluginCall) {
        guard let title = call.getString("notificationTitle") else {
            call.reject("notificationTitle is required")
            return
        }
        
        let subtitle = call.getString("notificationSubtitle")
        let enableLocation = call.getBool("enableLocation", false)
        let soundsEnabled = call.getBool("soundsEnabled", false)
        let customLayout = call.getString("customLayout")
        
        // Extraire viewData et buttons depuis les options
        var viewData: [String: String]?
        if let viewDataObj = call.getObject("viewData") {
            viewData = viewDataObj.compactMapValues { $0 as? String }
        }
        
        var buttons: [[String: Any]]?
        if let buttonsArray = call.getArray("buttons") {
            buttons = buttonsArray as? [[String: Any]]
        }
        
        // Démarrer la notification
        notificationHelper.showNotification(
            title: title,
            subtitle: subtitle,
            customLayout: customLayout,
            viewData: viewData,
            soundsEnabled: soundsEnabled
        )
        
        // Configurer les actions de notification (boutons)
        if let buttons = buttons {
            notificationHelper.configureNotificationActions(buttons: buttons)
        }
        
        // Démarrer la localisation si demandée
        if enableLocation {
            locationHelper.startLocationUpdates { [weak self] location in
                // Émettre un événement de localisation
                self?.notifyListeners("locationUpdate", data: [
                    "latitude": location.coordinate.latitude,
                    "longitude": location.coordinate.longitude,
                    "timestamp": Int64(location.timestamp.timeIntervalSince1970 * 1000)
                ])
            }
        }
        
        call.resolve(["started": true])
    }
    
    /**
     * Met à jour la notification
     */
    @objc func update(_ call: CAPPluginCall) {
        let title = call.getString("notificationTitle")
        let subtitle = call.getString("notificationSubtitle")
        
        // Extraire viewData
        var viewData: [String: String]?
        if let viewDataObj = call.getObject("viewData") {
            viewData = viewDataObj.compactMapValues { $0 as? String }
        }
        
        // Extraire buttons
        var buttons: [[String: Any]]?
        if let buttonsArray = call.getArray("buttons") {
            buttons = buttonsArray as? [[String: Any]]
        }
        
        notificationHelper.updateNotification(
            title: title,
            subtitle: subtitle,
            viewData: viewData
        )
        
        if let buttons = buttons {
            notificationHelper.configureNotificationActions(buttons: buttons)
        }
        
        call.resolve(["updated": true])
    }
    
    /**
     * Arrête le service
     */
    @objc func stop(_ call: CAPPluginCall) {
        notificationHelper.removeNotification()
        locationHelper.stopLocationUpdates()
        
        // Arrêter toutes les tâches
        let taskIds = taskManager.getRegisteredTasks()
        for taskId in taskIds {
            taskManager.unregisterTask(taskId: taskId)
        }
        
        // Supprimer toutes les géofences
        geofenceHelper.removeAllGeofences()
        
        call.resolve(["stopped": true])
    }
    
    /**
     * Enregistre une tâche en arrière-plan
     * Note: Sur iOS, les tâches sont limitées par le système
     */
    @objc func registerTask(_ call: CAPPluginCall) {
        guard let taskId = call.getString("taskId") else {
            call.reject("taskId is required")
            return
        }
        
        guard let intervalMs = call.getInt("intervalMs"), intervalMs >= 1000 else {
            call.reject("intervalMs must be >= 1000")
            return
        }
        
        // Sur iOS, on ne peut pas exécuter du code Swift arbitraire en arrière-plan
        // On simule avec un handler qui sera appelé par BGTaskScheduler
        let interval = TimeInterval(intervalMs) / 1000.0
        
        taskManager.registerTask(taskId: taskId, interval: interval) { [weak self] in
            // Exécuter la tâche
            self?.executeTask(taskId: taskId)
        }
        
        call.resolve(["registered": true])
    }
    
    /**
     * Désenregistre une tâche
     */
    @objc func unregisterTask(_ call: CAPPluginCall) {
        guard let taskId = call.getString("taskId") else {
            call.reject("taskId is required")
            return
        }
        
        taskManager.unregisterTask(taskId: taskId)
        taskResults.removeValue(forKey: taskId)
        
        call.resolve(["unregistered": true])
    }
    
    /**
     * Récupère le résultat d'une tâche
     */
    @objc func getTaskResult(_ call: CAPPluginCall) {
        guard let taskId = call.getString("taskId") else {
            call.reject("taskId is required")
            return
        }
        
        let result = taskResults[taskId]
        call.resolve(["result": result ?? NSNull()])
    }
    
    /**
     * Ajoute un listener pour les événements de tâches
     */
    @objc func addListener(_ call: CAPPluginCall) {
        guard let eventName = call.getString("eventName"),
              eventName == "taskEvent" else {
            call.reject("Invalid event name")
            return
        }
        
        // Stocker le listener (sera appelé via notifyListeners)
        call.resolve(["remove": true])
    }
    
    /**
     * Supprime tous les listeners
     */
    @objc func removeAllListeners(_ call: CAPPluginCall) {
        taskEventListeners.removeAll()
        call.resolve()
    }
    
    /**
     * Exécute une tâche (méthode interne)
     */
    private func executeTask(taskId: String) {
        // Créer un résultat de tâche
        let result: [String: Any] = [
            "taskId": taskId,
            "timestamp": Int64(Date().timeIntervalSince1970 * 1000),
            "data": "Task executed"
        ]
        
        taskResults[taskId] = result
        
        // Émettre un événement si des listeners sont actifs
        notifyListeners("taskEvent", data: [
            "taskId": taskId,
            "data": result["data"] ?? "",
            "timestamp": result["timestamp"] ?? 0
        ])
        
        print("ThunderBG: Task executed: \(taskId)")
    }
    
    /**
     * Ajoute une géofence
     */
    @objc func addGeofence(_ call: CAPPluginCall) {
        guard let id = call.getString("id"),
              let latitude = call.getDouble("latitude"),
              let longitude = call.getDouble("longitude") else {
            call.reject("id, latitude, and longitude are required")
            return
        }
        
        let radius = call.getDouble("radius") ?? 100.0
        let onEnter = call.getString("onEnter")
        let onExit = call.getString("onExit")
        
        // Extraire les extras
        var extras: [String: String]?
        if let extrasObj = call.getObject("extras") {
            extras = extrasObj.compactMapValues { $0 as? String }
        }
        
        let config = GeofenceHelper.GeofenceConfig(
            id: id,
            latitude: latitude,
            longitude: longitude,
            radius: radius,
            onEnterAction: onEnter,
            onExitAction: onExit,
            extras: extras
        )
        
        geofenceHelper.addGeofence(config: config)
        
        call.resolve(["added": true])
    }
    
    /**
     * Supprime une géofence
     */
    @objc func removeGeofence(_ call: CAPPluginCall) {
        guard let geofenceId = call.getString("geofenceId") else {
            call.reject("geofenceId is required")
            return
        }
        
        geofenceHelper.removeGeofence(id: geofenceId)
        
        call.resolve(["removed": true])
    }
    
    /**
     * Supprime toutes les géofences
     */
    @objc func removeAllGeofences(_ call: CAPPluginCall) {
        geofenceHelper.removeAllGeofences()
        
        call.resolve(["removed": true])
    }
}
