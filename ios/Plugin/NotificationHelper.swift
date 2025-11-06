import Foundation
import UserNotifications
import UIKit

/**
 * Helper pour gérer les notifications iOS avec support des custom layouts
 */
class NotificationHelper {
    static let shared = NotificationHelper()
    
    private let notificationCenter = UNUserNotificationCenter.current()
    private var currentNotificationContent: UNMutableNotificationContent?
    private var customLayoutData: [String: Any]?
    
    private init() {
        requestAuthorization()
    }
    
    /**
     * Demande l'autorisation pour les notifications
     */
    func requestAuthorization() {
        notificationCenter.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("ThunderBG: Notification authorization error: \(error)")
            } else {
                print("ThunderBG: Notification authorization granted: \(granted)")
            }
        }
    }
    
    /**
     * Configure et affiche une notification persistante (foreground)
     */
    func showNotification(title: String, subtitle: String?, customLayout: String? = nil, 
                         viewData: [String: String]? = nil, soundsEnabled: Bool = false) {
        
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = subtitle ?? ""
        content.sound = soundsEnabled ? .default : nil
        content.categoryIdentifier = "THUNDER_BG_SERVICE"
        
        // Sauvegarder les données personnalisées
        if let viewData = viewData {
            var userInfo: [String: Any] = ["viewData": viewData]
            if let customLayout = customLayout {
                userInfo["customLayout"] = customLayout
            }
            content.userInfo = userInfo
        }
        
        currentNotificationContent = content
        customLayoutData = viewData
        
        // Créer une requête de notification persistante
        let request = UNNotificationRequest(
            identifier: "THUNDER_BG_FOREGROUND",
            content: content,
            trigger: nil // nil = notification immédiate et persistante
        )
        
        notificationCenter.add(request) { error in
            if let error = error {
                print("ThunderBG: Error showing notification: \(error)")
            } else {
                print("ThunderBG: Notification shown: \(title)")
            }
        }
    }
    
    /**
     * Met à jour la notification existante
     */
    func updateNotification(title: String?, subtitle: String?, viewData: [String: String]? = nil) {
        let content = UNMutableNotificationContent()
        
        if let title = title {
            content.title = title
        } else if let current = currentNotificationContent {
            content.title = current.title
        } else {
            content.title = "Service"
        }
        
        if let subtitle = subtitle {
            content.body = subtitle
        } else if let current = currentNotificationContent {
            content.body = current.body
        }
        
        content.sound = currentNotificationContent?.sound
        content.categoryIdentifier = "THUNDER_BG_SERVICE"
        
        // Mettre à jour les données personnalisées
        if let viewData = viewData {
            var userInfo = currentNotificationContent?.userInfo ?? [:]
            userInfo["viewData"] = viewData
            content.userInfo = userInfo
            customLayoutData = viewData
        } else if let current = currentNotificationContent {
            content.userInfo = current.userInfo
        }
        
        currentNotificationContent = content
        
        let request = UNNotificationRequest(
            identifier: "THUNDER_BG_FOREGROUND",
            content: content,
            trigger: nil
        )
        
        notificationCenter.add(request) { error in
            if let error = error {
                print("ThunderBG: Error updating notification: \(error)")
            }
        }
    }
    
    /**
     * Supprime la notification
     */
    func removeNotification() {
        notificationCenter.removeDeliveredNotifications(withIdentifiers: ["THUNDER_BG_FOREGROUND"])
        notificationCenter.removePendingNotificationRequests(withIdentifiers: ["THUNDER_BG_FOREGROUND"])
        currentNotificationContent = nil
        customLayoutData = nil
        print("ThunderBG: Notification removed")
    }
    
    /**
     * Configure les actions de notification (boutons)
     */
    func configureNotificationActions(buttons: [[String: Any]]) {
        var actions: [UNNotificationAction] = []
        
        for (index, button) in buttons.enumerated() {
            if let actionId = button["action"] as? String,
               let title = button["viewId"] as? String {
                let action = UNNotificationAction(
                    identifier: actionId,
                    title: title,
                    options: []
                )
                actions.append(action)
            }
        }
        
        let category = UNNotificationCategory(
            identifier: "THUNDER_BG_SERVICE",
            actions: actions,
            intentIdentifiers: [],
            options: []
        )
        
        notificationCenter.setNotificationCategories([category])
    }
}

