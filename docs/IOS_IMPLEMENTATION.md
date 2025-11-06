# 🍎 Guide d'Implémentation iOS

Ce document explique comment utiliser le plugin ThunderBgService sur iOS et les différences avec Android.

## 📋 Fonctionnalités Supportées

### ✅ Supportées

- **Notifications persistantes** : Notifications qui restent visibles
- **Localisation en arrière-plan** : Suivi GPS continu
- **Mise à jour dynamique** : Modification du contenu de la notification
- **Boutons interactifs** : Actions dans les notifications
- **Tâches en arrière-plan** : Tâches périodiques (limitées par iOS)

### ⚠️ Limitations iOS

- **Pas de RemoteViews** : iOS ne supporte pas les layouts XML personnalisés comme Android
- **Tâches limitées** : Les tâches en arrière-plan sont strictement contrôlées par iOS
- **Localisation** : Nécessite une autorisation "Always" explicite de l'utilisateur
- **Pas de code natif** : Sur iOS, on ne peut pas exécuter du code Swift arbitraire en arrière-plan

## 🚀 Installation

### 1. Synchroniser avec Capacitor

```bash
npx cap sync ios
```

### 2. Configuration Info.plist

Voir `ios/INFO_PLIST.md` pour la configuration complète.

### 3. Permissions

Demandez les permissions dans votre code :

```swift
import UserNotifications
import CoreLocation

// Notifications
UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
    // ...
}

// Localisation
let locationManager = CLLocationManager()
locationManager.requestAlwaysAuthorization()
```

## 📱 Utilisation

### API TypeScript (identique à Android)

```typescript
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

// Démarrer le service
await ThunderBgService.start({
  notificationTitle: 'Online',
  notificationSubtitle: 'Service actif',
  enableLocation: true,
  viewData: {
    txtDriverName: 'John Doe',
    txtStatus: 'En ligne',
  },
});

// Mettre à jour
await ThunderBgService.update({
  notificationTitle: 'En cours',
  viewData: {
    txtStatus: 'En cours de mission',
  },
});

// Arrêter
await ThunderBgService.stop();
```

## 🔄 Différences avec Android

### 1. Custom Layouts

**Android** : Supporte des layouts XML personnalisés avec `RemoteViews`
```xml
<!-- notification_online.xml -->
<LinearLayout>
  <TextView android:id="@+id/txtTitle" />
  <ImageView android:id="@+id/imgAvatar" />
</LinearLayout>
```

**iOS** : Les layouts personnalisés sont stockés dans `userInfo` de la notification
```swift
// Les données sont dans notification.userInfo["viewData"]
// Vous devez créer une extension de notification pour les afficher
```

### 2. Tâches en Arrière-plan

**Android** : Exécution illimitée de code Java
```java
public class MyTask implements BackgroundTask {
    public void execute(Context context, String taskId) {
        // Code Java qui s'exécute même si l'app est fermée
    }
}
```

**iOS** : Tâches limitées par BGTaskScheduler
```swift
// Les tâches sont exécutées par le système iOS
// Temps d'exécution limité (quelques minutes)
// Fréquence limitée par le système
```

### 3. Localisation

**Android** : `FOREGROUND_SERVICE_LOCATION` permission
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION"/>
```

**iOS** : `NSLocationAlwaysAndWhenInUseUsageDescription`
```xml
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>Description de l'utilisation</string>
```

### 4. Boutons dans Notifications

**Android** : BroadcastReceiver
```java
public class NotifActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Action reçue
    }
}
```

**iOS** : UNNotificationAction
```swift
let action = UNNotificationAction(
    identifier: "ACTION_ID",
    title: "Button Title",
    options: []
)
```

## 📊 Comparaison Complète

| Fonctionnalité | Android | iOS | Notes |
|----------------|---------|-----|-------|
| **Notifications** | ✅ | ✅ | Identique |
| **Localisation** | ✅ | ✅ | iOS nécessite autorisation Always |
| **Custom Layouts** | ✅ | ⚠️ | iOS via userInfo |
| **Images dynamiques** | ✅ | ✅ | Supportées |
| **Boutons** | ✅ | ✅ | Implémentation différente |
| **Tâches arrière-plan** | ✅ | ⚠️ | iOS limité |
| **State Manager** | ✅ | ⚠️ | À adapter pour iOS |
| **Cache ressources** | ✅ | N/A | Spécifique Android |

## 🛠️ Développement

### Architecture

```
ios/Plugin/
├── ThunderBgServicePlugin.swift    # Plugin principal
├── NotificationHelper.swift         # Gestion notifications
├── LocationHelper.swift            # Gestion localisation
└── BackgroundTaskManager.swift      # Gestion tâches
```

### Helpers

- **NotificationHelper** : Gère les notifications UNUserNotificationCenter
- **LocationHelper** : Gère CLLocationManager pour la localisation
- **BackgroundTaskManager** : Gère BGTaskScheduler pour les tâches

## ⚠️ Limitations iOS

1. **Tâches en arrière-plan** :
   - Exécution limitée à quelques minutes
   - Fréquence contrôlée par iOS
   - Peut être tuée par le système

2. **Localisation** :
   - Nécessite autorisation explicite "Always"
   - L'utilisateur peut révoquer à tout moment
   - Consommation de batterie surveillée par iOS

3. **Custom Layouts** :
   - Pas de RemoteViews comme Android
   - Doit utiliser les extensions de notification
   - Moins de flexibilité

## 🔍 Debugging

### Vérifier les permissions

```swift
// Notifications
UNUserNotificationCenter.current().getNotificationSettings { settings in
    print("Authorization: \(settings.authorizationStatus)")
}

// Localisation
let status = CLLocationManager().authorizationStatus
print("Location status: \(status)")
```

### Logs

Les logs du plugin sont préfixés avec `ThunderBG:` :
```
ThunderBG: Notification shown: Online
ThunderBG: Location tracking started
ThunderBG: Task registered: syncTask
```

## 📚 Ressources

- [Apple Background Tasks](https://developer.apple.com/documentation/backgroundtasks)
- [UserNotifications Framework](https://developer.apple.com/documentation/usernotifications)
- [CoreLocation Framework](https://developer.apple.com/documentation/corelocation)

---

**Note** : Cette implémentation iOS fournit une compatibilité maximale avec Android, mais certaines fonctionnalités sont limitées par les contraintes du système iOS.

