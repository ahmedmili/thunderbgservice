import Foundation
import BackgroundTasks

/**
 * Gestionnaire de tâches en arrière-plan pour iOS
 * Utilise BGTaskScheduler pour les tâches périodiques
 */
class BackgroundTaskManager {
    static let shared = BackgroundTaskManager()
    
    private var registeredTasks: [String: BackgroundTaskConfig] = [:]
    private let taskIdentifierPrefix = "com.ahmedmili.thunderbgservice.task."
    
    struct BackgroundTaskConfig {
        let taskId: String
        let interval: TimeInterval
        let handler: () -> Void
    }
    
    private init() {
        registerBackgroundTasks()
    }
    
    /**
     * Enregistre les types de tâches en arrière-plan
     * Note: Les tâches doivent être enregistrées dans AppDelegate
     */
    private func registerBackgroundTasks() {
        // Les tâches sont enregistrées dans AppDelegate via BGTaskScheduler
        // Voir INFO_PLIST.md pour la configuration
        print("ThunderBG: Background tasks manager initialized")
    }
    
    /**
     * Enregistre une tâche périodique
     */
    func registerTask(taskId: String, interval: TimeInterval, handler: @escaping () -> Void) {
        let identifier = taskIdentifierPrefix + taskId
        
        let config = BackgroundTaskConfig(
            taskId: taskId,
            interval: interval,
            handler: handler
        )
        
        registeredTasks[taskId] = config
        
        // Programmer la tâche
        scheduleTask(identifier: identifier, interval: interval)
        
        print("ThunderBG: Task registered: \(taskId) (interval: \(interval)s)")
    }
    
    /**
     * Programme une tâche en arrière-plan
     */
    private func scheduleTask(identifier: String, interval: TimeInterval) {
        // Utiliser BGAppRefreshTaskRequest pour les tâches plus fréquentes
        // ou BGProcessingTaskRequest pour les tâches plus longues
        let request = BGAppRefreshTaskRequest(identifier: identifier)
        request.earliestBeginDate = Date(timeIntervalSinceNow: interval)
        
        do {
            try BGTaskScheduler.shared.submit(request)
            print("ThunderBG: Task scheduled: \(identifier) (interval: \(interval)s)")
        } catch {
            print("ThunderBG: Error scheduling task: \(error.localizedDescription)")
            // Fallback: utiliser BGProcessingTaskRequest
            let processingRequest = BGProcessingTaskRequest(identifier: identifier)
            processingRequest.earliestBeginDate = Date(timeIntervalSinceNow: interval)
            processingRequest.requiresNetworkConnectivity = false
            processingRequest.requiresExternalPower = false
            
            do {
                try BGTaskScheduler.shared.submit(processingRequest)
                print("ThunderBG: Task scheduled with processing request")
            } catch {
                print("ThunderBG: Error scheduling processing task: \(error.localizedDescription)")
            }
        }
    }
    
    /**
     * Désenregistre une tâche
     */
    func unregisterTask(taskId: String) {
        let identifier = taskIdentifierPrefix + taskId
        
        BGTaskScheduler.shared.cancel(taskRequestWithIdentifier: identifier)
        registeredTasks.removeValue(forKey: taskId)
        
        print("ThunderBG: Task unregistered: \(taskId)")
    }
    
    /**
     * Exécute une tâche immédiatement (pour les tests)
     */
    func executeTask(taskId: String) {
        guard let config = registeredTasks[taskId] else {
            print("ThunderBG: Task not found: \(taskId)")
            return
        }
        
        config.handler()
    }
    
    /**
     * Obtient toutes les tâches enregistrées
     */
    func getRegisteredTasks() -> [String] {
        return Array(registeredTasks.keys)
    }
}

