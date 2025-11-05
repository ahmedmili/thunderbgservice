# Utilisation du plugin depuis le code Java natif

Ce guide explique comment utiliser les classes publiques du plugin `@ahmed-mili/capacitor-thunder-bg-service` directement depuis le code Java natif de votre application Android, sans passer par le bridge Capacitor/TypeScript.

## Classe publique disponible

### `ThunderBgServiceHelper`

Classe utilitaire publique avec des méthodes statiques pour contrôler le service depuis Java natif.

**Package**: `com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper`

## Méthodes disponibles

### 1. Démarrer le service

```java
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;

// Méthode simple
ThunderBgServiceHelper.startService(
    context,
    "Online",                    // Titre
    "Waiting for rides",         // Sous-titre
    true                         // enableLocation
);

// Avec layout personnalisé
ThunderBgServiceHelper.startService(
    context,
    "Driver Online",
    "Waiting",
    true,                        // enableLocation
    "notification_driver",       // customLayout
    "txtTitle",                  // titleViewId
    "txtSubtitle",               // subtitleViewId
    "txtTimer"                   // timerViewId
);
```

### 2. Mettre à jour la notification

```java
// Mettre à jour juste le texte
ThunderBgServiceHelper.updateNotification(
    context,
    "En course",                 // Titre (ou null pour garder l'actuel)
    "En route vers destination"  // Sous-titre
);

// Changer le layout dynamiquement
ThunderBgServiceHelper.updateNotification(
    context,
    "En course",
    "Client XYZ → Destination",
    "notification_riding",       // Nouveau layout
    "txtDriverStatus",           // Nouveaux IDs
    "txtDestination",
    "txtElapsedTime"
);
```

### 3. Arrêter le service

```java
ThunderBgServiceHelper.stopService(context);
```

### 4. Vérifier les ressources

```java
// Vérifier si un layout existe
boolean exists = ThunderBgServiceHelper.layoutExists(context, "notification_driver");

// Vérifier si un ID de vue existe
boolean viewExists = ThunderBgServiceHelper.viewIdExists(context, "txtTitle");
```

## Exemples d'utilisation

### Exemple 1: Depuis une Activity

```java
package com.yourpackage;

import android.app.Activity;
import android.os.Bundle;
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Démarrer le service au démarrage de l'app
        ThunderBgServiceHelper.startService(
            this,
            "Disponible",
            "En attente",
            true
        );
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Arrêter le service quand l'app est fermée (optionnel)
        ThunderBgServiceHelper.stopService(this);
    }
}
```

### Exemple 2: Depuis un Service Android natif

```java
package com.yourpackage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;

public class MyBackgroundService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Démarrer le service de notification
        ThunderBgServiceHelper.startService(
            this,
            "Service actif",
            "En arrière-plan",
            true
        );
    }
    
    @Override
    public void onDestroy() {
        ThunderBgServiceHelper.stopService(this);
        super.onDestroy();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
```

### Exemple 3: Gestionnaire d'état personnalisé

```java
package com.yourpackage;

import android.content.Context;
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;

public class MyNotificationManager {
    private Context context;
    private String currentState = "OFFLINE";
    
    public MyNotificationManager(Context context) {
        this.context = context;
    }
    
    public void goOnline() {
        currentState = "ONLINE";
        ThunderBgServiceHelper.startService(
            context,
            "Disponible",
            "En attente de courses",
            true,
            "notification_online",
            "txtTitle",
            "txtSubtitle",
            "txtTimer"
        );
    }
    
    public void startRide(String clientName, String destination) {
        currentState = "RIDING";
        ThunderBgServiceHelper.updateNotification(
            context,
            "En course",
            clientName + " → " + destination,
            "notification_riding",
            "txtDriverStatus",
            "txtDestination",
            "txtElapsedTime"
        );
    }
    
    public void stop() {
        ThunderBgServiceHelper.stopService(context);
        currentState = "OFFLINE";
    }
}
```

### Exemple 4: Utilisation avec BroadcastReceiver

```java
package com.yourpackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        
        if ("ACTION_START_SERVICE".equals(action)) {
            ThunderBgServiceHelper.startService(
                context,
                "Service démarré",
                "Depuis BroadcastReceiver",
                true
            );
        } else if ("ACTION_UPDATE_STATUS".equals(action)) {
            String status = intent.getStringExtra("status");
            ThunderBgServiceHelper.updateNotification(
                context,
                null,  // Garder le titre actuel
                status // Mettre à jour le sous-titre
            );
        }
    }
}
```

## Avantages de l'utilisation native

1. **Pas de dépendance JS/TS** : Vous pouvez utiliser le service depuis du code Java/Kotlin pur
2. **Performance** : Pas de bridge Capacitor, accès direct aux méthodes natives
3. **Flexibilité** : Intégration facile dans des Services, BroadcastReceivers, etc.
4. **Compatible** : Fonctionne avec le bridge JS/TS en parallèle si besoin

## Notes importantes

1. **Permissions** : Assurez-vous d'avoir les permissions nécessaires (Notifications, Localisation) avant d'appeler `startService()`

2. **Context** : Utilisez toujours `getApplicationContext()` pour éviter les fuites mémoire

3. **Thread safety** : Les méthodes sont thread-safe, vous pouvez les appeler depuis n'importe quel thread

4. **Logs** : Les méthodes loggent automatiquement les actions (filtrez avec `ThunderBgServiceHelper` dans logcat)

5. **Compatibilité** : Vous pouvez utiliser les méthodes natives ET le bridge JS/TS en même temps, ils fonctionnent de manière indépendante

## Fichiers d'exemple

- `EXAMPLE_NativeJavaUsage.java` - Exemples complets d'utilisation
- `EXAMPLE_ActivityUsage.java` - Utilisation dans une Activity
- Ce README - Documentation complète

## Installation

1. Installez le plugin normalement : `npm i @ahmed-mili/capacitor-thunder-bg-service`
2. Faites `npx cap sync`
3. Importez `ThunderBgServiceHelper` dans votre code Java natif
4. Utilisez les méthodes statiques selon vos besoins

Aucune configuration supplémentaire n'est nécessaire !

