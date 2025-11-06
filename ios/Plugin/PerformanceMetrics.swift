import Foundation
import UIKit

/**
 * Système de métriques de performance pour iOS
 * Collecte des statistiques sur l'utilisation, la batterie, les tâches, etc.
 */
class PerformanceMetrics {
    static let shared = PerformanceMetrics()
    
    private var taskExecutionCount: [String: Int] = [:]
    private var taskTotalTime: [String: TimeInterval] = [:]
    private var taskLastExecution: [String: Date] = [:]
    
    private var locationUpdateCount: Int = 0
    private var locationTotalDistance: Double = 0.0 // en km
    private var locationFirstUpdate: Date?
    private var locationLastUpdate: Date?
    
    private var notificationUpdateCount: Int = 0
    private var serviceStartTime: Date?
    private var serviceTotalUptime: TimeInterval = 0
    
    private var geofenceTriggerCount: Int = 0
    private var geofenceTriggersByType: [String: Int] = [:]
    
    private init() {
        print("ThunderBG: Performance metrics initialized")
    }
    
    // MARK: - Métriques de Tâches
    
    func recordTaskExecution(taskId: String, executionTime: TimeInterval) {
        taskExecutionCount[taskId, default: 0] += 1
        taskTotalTime[taskId, default: 0] += executionTime
        taskLastExecution[taskId] = Date()
        
        print("ThunderBG: Task executed: \(taskId) (time: \(executionTime)s)")
    }
    
    func getTaskStats(taskId: String) -> [String: Any] {
        let count = taskExecutionCount[taskId] ?? 0
        let totalTime = taskTotalTime[taskId] ?? 0
        let lastExecution = taskLastExecution[taskId]?.timeIntervalSince1970 ?? 0
        let averageTime = count > 0 ? totalTime / Double(count) : 0
        
        return [
            "taskId": taskId,
            "executionCount": count,
            "totalTimeMs": totalTime * 1000,
            "averageTimeMs": averageTime * 1000,
            "lastExecutionTime": lastExecution * 1000
        ]
    }
    
    func getAllTaskStats() -> [String: [String: Any]] {
        var stats: [String: [String: Any]] = [:]
        for taskId in taskExecutionCount.keys {
            stats[taskId] = getTaskStats(taskId: taskId)
        }
        return stats
    }
    
    // MARK: - Métriques de Localisation
    
    func recordLocationUpdate(distanceKm: Double) {
        locationUpdateCount += 1
        locationTotalDistance += distanceKm
        
        let now = Date()
        if locationFirstUpdate == nil {
            locationFirstUpdate = now
        }
        locationLastUpdate = now
        
        print("ThunderBG: Location update recorded (total: \(locationUpdateCount), distance: \(distanceKm)km)")
    }
    
    func getLocationStats() -> [String: Any] {
        return [
            "updateCount": locationUpdateCount,
            "totalDistanceKm": locationTotalDistance,
            "firstUpdateTime": locationFirstUpdate?.timeIntervalSince1970 ?? 0,
            "lastUpdateTime": locationLastUpdate?.timeIntervalSince1970 ?? 0
        ]
    }
    
    // MARK: - Métriques de Service
    
    func recordServiceStart() {
        serviceStartTime = Date()
        print("ThunderBG: Service start recorded")
    }
    
    func recordServiceStop() {
        if let startTime = serviceStartTime {
            let duration = Date().timeIntervalSince(startTime)
            serviceTotalUptime += duration
            serviceStartTime = nil
            print("ThunderBG: Service stop recorded (duration: \(duration)s)")
        }
    }
    
    func recordNotificationUpdate() {
        notificationUpdateCount += 1
    }
    
    func getServiceStats() -> [String: Any] {
        var currentSessionTime: TimeInterval = 0
        if let startTime = serviceStartTime {
            currentSessionTime = Date().timeIntervalSince(startTime)
        }
        
        let totalUptime = serviceTotalUptime + currentSessionTime
        
        return [
            "totalUptimeMs": totalUptime * 1000,
            "totalUptimeHours": totalUptime / 3600,
            "notificationUpdateCount": notificationUpdateCount,
            "isRunning": serviceStartTime != nil
        ]
    }
    
    // MARK: - Métriques de Géofences
    
    func recordGeofenceTrigger(eventType: String) {
        geofenceTriggerCount += 1
        geofenceTriggersByType[eventType, default: 0] += 1
        print("ThunderBG: Geofence trigger recorded: \(eventType)")
    }
    
    func getGeofenceStats() -> [String: Any] {
        return [
            "totalTriggers": geofenceTriggerCount,
            "triggersByType": geofenceTriggersByType
        ]
    }
    
    // MARK: - Métriques Système
    
    func getBatteryStats() -> [String: Any] {
        UIDevice.current.isBatteryMonitoringEnabled = true
        let batteryLevel = Int(UIDevice.current.batteryLevel * 100)
        let isCharging = UIDevice.current.batteryState == .charging
        
        // Estimation basique de la consommation
        let uptime = ProcessInfo.processInfo.systemUptime / 3600 // en heures
        let estimatedConsumption = Double(uptime) * 0.5 + Double(getAllTaskStats().count) * 0.1
        
        return [
            "batteryLevel": batteryLevel,
            "estimatedConsumption": estimatedConsumption,
            "isCharging": isCharging
        ]
    }
    
    // MARK: - Métriques Globales
    
    func getAllMetrics() -> [String: Any] {
        var metrics: [String: Any] = [:]
        
        metrics["tasks"] = getAllTaskStats()
        metrics["location"] = getLocationStats()
        metrics["service"] = getServiceStats()
        metrics["geofences"] = getGeofenceStats()
        metrics["battery"] = getBatteryStats()
        
        return metrics
    }
    
    func reset() {
        taskExecutionCount.removeAll()
        taskTotalTime.removeAll()
        taskLastExecution.removeAll()
        locationUpdateCount = 0
        locationTotalDistance = 0
        locationFirstUpdate = nil
        locationLastUpdate = nil
        notificationUpdateCount = 0
        serviceTotalUptime = 0
        serviceStartTime = nil
        geofenceTriggerCount = 0
        geofenceTriggersByType.removeAll()
        
        print("ThunderBG: All metrics reset")
    }
}

