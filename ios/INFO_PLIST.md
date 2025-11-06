# Configuration iOS Requise

Pour que le plugin fonctionne correctement sur iOS, vous devez ajouter les configurations suivantes dans votre `Info.plist` :

## 1. Permissions de Localisation

Ajoutez ces clés dans `Info.plist` :

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>Cette application a besoin de votre localisation pour fournir des services en temps réel.</string>

<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>Cette application a besoin de votre localisation en arrière-plan pour continuer à fonctionner même quand l'app est fermée.</string>
```

## 2. Background Modes

Ajoutez le mode `location` dans les Background Modes :

```xml
<key>UIBackgroundModes</key>
<array>
    <string>location</string>
    <string>fetch</string>
    <string>processing</string>
</array>
```

## 3. Background Task Identifiers

Ajoutez les identifiants de tâches en arrière-plan :

```xml
<key>BGTaskSchedulerPermittedIdentifiers</key>
<array>
    <string>com.ahmedmili.thunderbgservice.task.*</string>
</array>
```

## 4. Configuration dans Xcode

1. Ouvrez votre projet dans Xcode
2. Sélectionnez votre target
3. Allez dans "Signing & Capabilities"
4. Ajoutez :
   - **Background Modes** → Cochez :
     - Location updates
     - Background fetch
     - Background processing

## 5. Code Swift à ajouter dans AppDelegate

Dans votre `AppDelegate.swift`, ajoutez :

```swift
import BackgroundTasks

func application(_ application: UIApplication, 
                 didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
    
    // Enregistrer les handlers de tâches en arrière-plan
    BGTaskScheduler.shared.register(forTaskWithIdentifier: "com.ahmedmili.thunderbgservice.task.*", 
                                    using: nil) { task in
        // Gérer l'exécution de la tâche
        self.handleBackgroundTask(task: task as! BGProcessingTask)
    }
    
    return true
}

private func handleBackgroundTask(task: BGProcessingTask) {
    // Planifier la prochaine exécution
    scheduleBackgroundTask()
    
    // Exécuter la tâche
    task.expirationHandler = {
        task.setTaskCompleted(success: false)
    }
    
    // Votre code de tâche ici
    // ...
    
    task.setTaskCompleted(success: true)
}
```

## Notes Importantes

- Les tâches en arrière-plan iOS sont **très limitées** par le système
- Apple peut tuer les tâches si elles consomment trop de ressources
- Les tâches doivent être complétées rapidement (quelques minutes max)
- Les notifications persistent même si l'app est fermée
- La localisation en arrière-plan nécessite une autorisation "Always"

## Limitations iOS vs Android

| Fonctionnalité | Android | iOS |
|----------------|---------|-----|
| Foreground Service | ✅ Complet | ⚠️ Via notifications |
| Tâches en arrière-plan | ✅ Illimitées | ⚠️ Limitées par le système |
| Localisation arrière-plan | ✅ Facile | ⚠️ Nécessite autorisation Always |
| Custom Layouts | ✅ RemoteViews | ⚠️ Via userInfo dans notification |
| Boutons cliquables | ✅ BroadcastReceiver | ✅ UNNotificationAction |

