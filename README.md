# @ahmed-mili/capacitor-thunder-bg-service

Plugin Capacitor 7 pour Android qui fournit un service foreground avec notifications, localisation, et gestion de tâches en arrière-plan. Fonctionne même quand l'app est fermée.

## ✨ Fonctionnalités principales

- **UI 100% dynamique depuis l'app** : Injection complète de layouts, textes et boutons depuis votre application
- **Boutons cliquables dans la notification** : Boutons interactifs reliés à vos BroadcastReceiver
- **Persistance d'état** : L'UI et l'état persistent même après fermeture/réouverture de l'app
- **Tâches en arrière-plan** : Exécution de code Java même si l'app est fermée
- **Localisation** : Suivi GPS en arrière-plan
- **Aucune UI/logique par défaut** : Le plugin n'affiche que ce que vous envoyez depuis l'app

## 📋 Table des matières

1. [Installation](#installation)
2. [Configuration](#configuration)
3. [Utilisation de base](#utilisation-de-base)
4. [Notifications personnalisées](#notifications-personnalisées)
5. [UI Dynamique 100% App-Driven](#-ui-dynamique-100-app-driven)
6. [Tâches en arrière-plan](#tâches-en-arrière-plan)
7. [Localisation](#localisation)
8. [Utilisation depuis Java natif](#utilisation-depuis-java-natif)
9. [API complète](#api-complète)
10. [Architecture](#architecture)
11. [Exemples](#exemples)
12. [Dépannage](#dépannage)

---

## 🚀 Installation

### 1. Installer le package

```bash
npm install @ahmed-mili/capacitor-thunder-bg-service
```

### 2. Synchroniser avec Capacitor

```bash
npx cap sync android
```

### 3. Permissions Android

Le plugin nécessite les permissions suivantes (déjà incluses dans le plugin) :
- `FOREGROUND_SERVICE`
- `FOREGROUND_SERVICE_LOCATION`
- `POST_NOTIFICATIONS`
- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `INTERNET`
- `WAKE_LOCK`

### 4. Configuration des ressources Android (Requis)

**Le plugin ne contient aucune UI par défaut.** Vous devez créer vos propres layouts dans votre app.

**Layout de notification** (`android/app/src/main/res/layout/notification_online.xml` - exemple) :
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp">
    <TextView
        android:id="@+id/txtDriverStatus"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="En ligne"
        android:textStyle="bold"
        android:textSize="16sp" />
    <TextView
        android:id="@+id/txtWaiting"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="En attente"
        android:textSize="14sp" />
    <Button
        android:id="@+id/btnAction"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Action" />
</LinearLayout>
```

**Icône de notification** (`android/app/src/main/res/drawable/ic_notification.xml`) :
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FF000000"
        android:pathData="M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM13,17h-2v-6h2v6zM13,9h-2L11,7h2v2z"/>
</vector>
```

**Important** : Créez vos layouts dans `android/app/src/main/res/layout/` de votre app. Le plugin utilisera uniquement les layouts que vous spécifiez via `customLayout`.

### 5. Configuration minimale

Aucune autre configuration n'est requise. Le plugin est prêt à l'emploi.

---

## ⚙️ Configuration

### Permissions Runtime (Android 6+)

Vous devez demander les permissions à l'utilisateur :

```typescript
import { Permissions } from '@capacitor/core';

async function requestPermissions() {
  const permissions = await Permissions.request([
    Permissions.ANDROID.POST_NOTIFICATIONS,
    Permissions.ANDROID.ACCESS_FINE_LOCATION,
    Permissions.ANDROID.ACCESS_COARSE_LOCATION,
  ]);
  return permissions;
}
```

---

## 🎯 Utilisation de base

### 1. Démarrer le service

**Important** : Vous devez toujours fournir `customLayout` car le plugin n'a pas de UI par défaut.

```typescript
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

// Démarrer avec un layout personnalisé
await ThunderBgService.start({
  customLayout: 'notification_online',  // REQUIS : nom de votre layout XML
  titleViewId: 'txtDriverStatus',       // ID du TextView pour le titre
  subtitleViewId: 'txtWaiting',        // ID du TextView pour le sous-titre
  notificationTitle: 'Online',
  notificationSubtitle: 'Service actif',
  enableLocation: true,
  soundsEnabled: false,
});
```

### 2. Mettre à jour la notification

```typescript
// Mettre à jour le contenu de la notification
await ThunderBgService.update({
  notificationTitle: 'En cours',
  notificationSubtitle: 'Traitement des données',
});
```

### 3. Arrêter le service

```typescript
// Arrêter le service et toutes les tâches
await ThunderBgService.stop();
```

### 4. Exemple complet dans un service Angular

```typescript
import { Injectable } from '@angular/core';
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

@Injectable({
  providedIn: 'root'
})
export class BackgroundService {
  
  async startService() {
    try {
      await ThunderBgService.start({
        notificationTitle: 'Online',
        notificationSubtitle: 'Service actif',
        enableLocation: true,
      });
      console.log('Service démarré');
    } catch (error) {
      console.error('Erreur:', error);
    }
  }
  
  async updateStatus(status: string) {
    await ThunderBgService.update({
      notificationSubtitle: status,
    });
  }
  
  async stopService() {
    await ThunderBgService.stop();
  }
}
```

---

## 🎨 Notifications personnalisées

### 1. Layout personnalisé par défaut

Le plugin utilise un layout par défaut (`notification_foreground.xml`) avec :
- Titre (`R.id.title`)
- Sous-titre (`R.id.subtitle`)

### 2. Créer votre propre layout

Créez un fichier XML dans `android/app/src/main/res/layout/` :

```xml
<!-- notification_custom.xml -->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp">

    <TextView
        android:id="@+id/txtTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Title"
        android:textStyle="bold"
        android:textSize="16sp"
        android:textColor="#000000" />

    <TextView
        android:id="@+id/txtSubtitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Subtitle"
        android:textSize="14sp"
        android:textColor="#444444" />

    <TextView
        android:id="@+id/txtTimer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="00:00:00"
        android:textSize="13sp"
        android:textColor="#1B5E20" />
</LinearLayout>
```

### 3. Utiliser le layout personnalisé

```typescript
// Démarrer avec un layout personnalisé
await ThunderBgService.start({
  notificationTitle: 'Online',
  notificationSubtitle: 'En attente',
  enableLocation: true,
  customLayout: 'notification_custom',     // Nom du layout (sans .xml)
  titleViewId: 'txtTitle',                 // ID du TextView pour le titre
  subtitleViewId: 'txtSubtitle',          // ID du TextView pour le sous-titre
  timerViewId: 'txtTimer',                 // ID du TextView pour le timer
});
```

### 4. Changer de layout dynamiquement

```typescript
// Changer de layout sans redémarrer le service
await ThunderBgService.update({
  notificationTitle: 'Nouveau titre',
  notificationSubtitle: 'Nouveau sous-titre',
  customLayout: 'notification_other',      // Nouveau layout
  titleViewId: 'txtOtherTitle',            // Nouveaux IDs
  subtitleViewId: 'txtOtherSubtitle',
  timerViewId: 'txtOtherTimer',
});
```

### 5. Exemple : Layouts multiples selon l'état

```typescript
enum AppState {
  OFFLINE = 'offline',
  ONLINE = 'online',
  ON_RIDE = 'on_ride',
  ARRIVED = 'arrived',
}

class NotificationManager {
  async switchToState(state: AppState) {
    const configs = {
      [AppState.OFFLINE]: {
        title: 'Hors ligne',
        subtitle: 'Service arrêté',
        layout: 'notification_offline',
        titleId: 'txtStatus',
        subtitleId: 'txtMessage',
      },
      [AppState.ONLINE]: {
        title: 'En ligne',
        subtitle: 'En attente de courses',
        layout: 'notification_online',
        titleId: 'txtDriverStatus',
        subtitleId: 'txtWaiting',
      },
      [AppState.ON_RIDE]: {
        title: 'En course',
        subtitle: 'Direction destination',
        layout: 'notification_riding',
        titleId: 'txtRideStatus',
        subtitleId: 'txtDestination',
      },
      [AppState.ARRIVED]: {
        title: 'Arrivé',
        subtitle: 'À destination',
        layout: 'notification_arrived',
        titleId: 'txtArrivalStatus',
        subtitleId: 'txtLocation',
      },
    };
    
    const config = configs[state];
    await ThunderBgService.update({
      notificationTitle: config.title,
      notificationSubtitle: config.subtitle,
      customLayout: config.layout,
      titleViewId: config.titleId,
      subtitleViewId: config.subtitleId,
    });
  }
}
```

---

## 🎨 UI Dynamique 100% App-Driven

Le plugin ne contient **aucune UI ou logique par défaut**. Tout doit être fourni depuis votre application.

### 1. Injection dynamique de textes (viewData)

Utilisez `viewData` pour mettre à jour n'importe quel TextView de votre layout :

```typescript
await ThunderBgService.update({
  customLayout: 'notification_online',
  viewData: {
    txtDriverStatus: 'En ligne',
    txtWaiting: 'En attente de courses',
    txtTimer: '00:05:23',
    // Ajoutez autant de TextViews que vous voulez
  },
});
```

**Important** : Les IDs dans `viewData` doivent correspondre exactement aux IDs de votre XML (sans `@+id/`).

### 2. Boutons cliquables dans la notification (buttons)

Créez des boutons interactifs reliés à vos BroadcastReceiver :

```typescript
await ThunderBgService.update({
  customLayout: 'notification_stepper',
  buttons: [
    {
      viewId: 'btnPrev',  // ID du Button/TextView dans votre XML
      action: 'com.yourapp.ACTION_STEPPER_PREV',  // Action broadcast
    },
    {
      viewId: 'btnNext',
      action: 'com.yourapp.ACTION_STEPPER_NEXT',
    },
    {
      viewId: 'btnDone',
      action: 'com.yourapp.ACTION_ONLINE',
      extras: {  // Optionnel : données supplémentaires
        step: '3',
        status: 'completed',
      },
    },
  ],
});
```

### 3. Configuration du BroadcastReceiver

Dans votre `AndroidManifest.xml` :

```xml
<receiver 
    android:name=".NotifActionReceiver"
    android:exported="true"
    android:enabled="true">
    <intent-filter>
        <action android:name="com.yourapp.ACTION_STEPPER_PREV"/>
        <action android:name="com.yourapp.ACTION_STEPPER_NEXT"/>
        <action android:name="com.yourapp.ACTION_ONLINE"/>
        <!-- Ajoutez toutes vos actions -->
    </intent-filter>
</receiver>
```

Dans votre `NotifActionReceiver.java` :

```java
public class NotifActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("com.yourapp.ACTION_STEPPER_PREV".equals(action)) {
            // Votre logique
        } else if ("com.yourapp.ACTION_STEPPER_NEXT".equals(action)) {
            // Votre logique
        }
    }
}
```

### 4. Exemple complet : Page stepper avec boutons

**Layout XML** (`notification_stepper.xml`) :

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp">
    
    <TextView
        android:id="@+id/txtTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Étapes"
        android:textStyle="bold" />
    
    <TextView
        android:id="@+id/txtCurrentStep"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Étape actuelle: 1/3" />
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        
        <Button
            android:id="@+id/btnPrev"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Précédent" />
        
        <Button
            android:id="@+id/btnNext"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Suivant" />
    </LinearLayout>
</LinearLayout>
```

**TypeScript** :

```typescript
// Démarrer avec page stepper
await ThunderBgService.start({
  customLayout: 'notification_stepper',
  titleViewId: 'txtTitle',
  subtitleViewId: 'txtCurrentStep',
  viewData: {
    txtTitle: 'Étapes',
    txtCurrentStep: 'Étape actuelle: 1/3',
  },
  buttons: [
    { viewId: 'btnPrev', action: 'com.yourapp.ACTION_STEPPER_PREV' },
    { viewId: 'btnNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
  ],
});

// Mettre à jour dynamiquement
await ThunderBgService.update({
  viewData: {
    txtCurrentStep: 'Étape actuelle: 2/3',
  },
});
```

**Java Helper** (dans votre app) :

```java
public class NotificationDynamicHelper {
    private int currentStep = 1;
    
    public void stepNext() {
        currentStep = Math.min(3, currentStep + 1);
        updateStepperUI();
    }
    
    private void updateStepperUI() {
        Intent extras = new Intent();
        extras.putExtra(FgConstants.EXTRA_CUSTOM_LAYOUT, "notification_stepper");
        extras.putExtra(FgConstants.EXTRA_TITLE_VIEW_ID, "txtTitle");
        extras.putExtra(FgConstants.EXTRA_SUBTITLE_VIEW_ID, "txtCurrentStep");
        
        JSONObject viewData = new JSONObject();
        viewData.put("txtTitle", "Étapes");
        viewData.put("txtCurrentStep", "Étape actuelle: " + currentStep + "/3");
        extras.putExtra(FgConstants.EXTRA_VIEW_DATA_JSON, viewData.toString());
        
        JSONArray buttons = new JSONArray();
        buttons.put(new JSONObject().put("viewId", "btnPrev").put("action", "com.yourapp.ACTION_STEPPER_PREV"));
        buttons.put(new JSONObject().put("viewId", "btnNext").put("action", "com.yourapp.ACTION_STEPPER_NEXT"));
        extras.putExtra(FgConstants.EXTRA_BUTTONS_JSON, buttons.toString());
        
        ForegroundTaskService.startAction(context, FgConstants.ACTION_UPDATE, extras);
    }
}
```

### 5. Persistance automatique

Le plugin sauvegarde automatiquement :
- Le layout actuel (`customLayout`)
- Les IDs de vues (`titleViewId`, `subtitleViewId`, `timerViewId`)
- Les données dynamiques (`viewData`)
- Les boutons (`buttons`)

Quand vous fermez et rouvrez l'app, l'UI est automatiquement restaurée à l'état précédent.

### 6. Notes importantes

- **Aucun layout par défaut** : Vous devez toujours fournir `customLayout`
- **IDs exacts** : Les IDs dans `viewData` et `buttons` doivent correspondre exactement à votre XML
- **Boutons cliquables** : Utilisez `Button` ou `TextView` avec `android:clickable="true"`
- **Receiver exporté** : `android:exported="true"` est obligatoire sur Android 12+

---

## 🔄 Tâches en arrière-plan

### 1. Concept

Les tâches en arrière-plan sont des fonctions Java qui s'exécutent périodiquement **même si l'app est fermée**. Elles tournent dans le service foreground.

### 2. Créer une tâche

Créez une classe Java dans votre app qui implémente `BackgroundTask` :

```java
package com.yourpackage;

import android.content.Context;
import android.util.Log;
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.tasks.TaskResultStorage;
import com.webify.thunderbgservice.tasks.TaskEventEmitter;
import org.json.JSONObject;

public class MySyncTask implements BackgroundTask {
    private static final String TAG = "MySyncTask";
    private int syncCount = 0;
    
    @Override
    public void execute(Context context, String taskId) {
        syncCount++;
        Log.i(TAG, "Sync #" + syncCount);
        
        // 1. Stocker des données pour JS
        TaskResultStorage.saveResult(context, taskId, "syncCount", String.valueOf(syncCount));
        TaskResultStorage.saveResult(context, taskId, "lastSync", System.currentTimeMillis() + "");
        
        // 2. Émettre un événement (si l'app est active)
        TaskEventEmitter.emit(context, taskId, "Sync completed: " + syncCount);
        
        // 3. Votre logique métier ici
        performSync(context);
    }
    
    @Override
    public void onRegistered(Context context, String taskId) {
        Log.i(TAG, "Task registered: " + taskId);
        // Initialisation si nécessaire
    }
    
    @Override
    public void onUnregistered(Context context, String taskId) {
        Log.i(TAG, "Task unregistered: " + taskId);
        // Nettoyage si nécessaire
    }
    
    private void performSync(Context context) {
        // Votre code de synchronisation
        // Ex: appeler une API, sauvegarder des données, etc.
    }
}
```

### 3. Enregistrer une tâche depuis TypeScript

```typescript
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

// Démarrer le service d'abord
await ThunderBgService.start({
  notificationTitle: 'Online',
  notificationSubtitle: 'Service actif',
  enableLocation: true,
});

// Enregistrer une tâche
await ThunderBgService.registerTask({
  taskId: 'syncTask',
  taskClass: 'com.yourpackage.MySyncTask',  // Nom complet de la classe
  intervalMs: 10000,  // Toutes les 10 secondes (minimum 1000ms)
});
```

### 4. Écouter les événements de la tâche

```typescript
// Écouter les événements (seulement si l'app est active)
ThunderBgService.addListener('taskEvent', (data) => {
  console.log('Événement de la tâche:', data.taskId);
  console.log('Données:', data.data);
  console.log('Timestamp:', data.timestamp);
  
  // Traiter les données
  if (data.taskId === 'syncTask') {
    handleSyncEvent(data.data);
  }
});
```

### 5. Récupérer les résultats stockés

```typescript
// Récupérer les résultats même si l'app était fermée
async function getTaskResults() {
  const { result } = await ThunderBgService.getTaskResult('syncTask');
  
  if (result) {
    console.log('Sync count:', result.syncCount);
    console.log('Last sync:', result.lastSync);
    console.log('Timestamp:', result.timestamp);
  }
}

// Polling périodique
setInterval(async () => {
  const { result } = await ThunderBgService.getTaskResult('syncTask');
  if (result) {
    updateUI(result);
  }
}, 5000);
```

### 6. Désenregistrer une tâche

```typescript
// Désenregistrer une tâche
await ThunderBgService.unregisterTask('syncTask');
```

### 7. Exemples de tâches

#### Exemple 1: Vérification réseau

```java
public class NetworkCheckTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnected();
        
        TaskResultStorage.saveResult(context, taskId, "connected", String.valueOf(isConnected));
        
        if (!isConnected) {
            TaskEventEmitter.emit(context, taskId, "Network disconnected");
        }
    }
}
```

#### Exemple 2: Mise à jour de localisation

```java
public class LocationUpdateTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // Récupérer la localisation
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if (location != null) {
            JSONObject data = new JSONObject();
            data.put("latitude", location.getLatitude());
            data.put("longitude", location.getLongitude());
            data.put("timestamp", System.currentTimeMillis());
            
            TaskResultStorage.saveResult(context, taskId, data);
            TaskEventEmitter.emit(context, taskId, data);
        }
    }
}
```

#### Exemple 3: Synchronisation de données

```java
public class DataSyncTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        try {
            // Appeler votre API
            String response = callApi(context);
            
            // Sauvegarder la réponse
            TaskResultStorage.saveResult(context, taskId, "lastResponse", response);
            TaskResultStorage.saveResult(context, taskId, "syncStatus", "success");
            
            TaskEventEmitter.emit(context, taskId, "Sync successful");
        } catch (Exception e) {
            TaskResultStorage.saveResult(context, taskId, "syncStatus", "error");
            TaskResultStorage.saveResult(context, taskId, "error", e.getMessage());
        }
    }
    
    private String callApi(Context context) {
        // Votre code HTTP ici
        return "";
    }
}
```

---

## 📍 Localisation

### 1. Activation automatique

La localisation est activée automatiquement si `enableLocation: true` :

```typescript
await ThunderBgService.start({
  notificationTitle: 'Online',
  notificationSubtitle: 'Service actif',
  enableLocation: true,  // Active la localisation
});
```

### 2. Utilisation dans une tâche

Vous pouvez accéder à la localisation depuis vos tâches :

```java
public class LocationTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        FusedLocationProviderClient fused = 
            LocationServices.getFusedLocationProviderClient(context);
        
        fused.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                JSONObject data = new JSONObject();
                data.put("lat", location.getLatitude());
                data.put("lng", location.getLongitude());
                TaskResultStorage.saveResult(context, taskId, data);
            }
        });
    }
}
```

---

## ☕ Utilisation depuis Java natif

### 1. Import

```java
import com.webify.thunderbgservice.core.ThunderBgServiceHelper;
```

### 2. Démarrer le service

```java
// Méthode simple
ThunderBgServiceHelper.startService(
    context,
    "Online",
    "En attente",
    true  // enableLocation
);

// Avec tous les paramètres
ThunderBgServiceHelper.startService(
    context,
    "Online",
    "En attente",
    true,  // enableLocation
    false, // soundsEnabled
    "notification_custom",  // customLayout
    "txtTitle",            // titleViewId
    "txtSubtitle",         // subtitleViewId
    "txtTimer"             // timerViewId
);
```

### 3. Mettre à jour la notification

```java
// Simple
ThunderBgServiceHelper.updateNotification(
    context,
    "Nouveau titre",
    "Nouveau sous-titre"
);

// Avec changement de layout
ThunderBgServiceHelper.updateNotification(
    context,
    "Titre",
    "Sous-titre",
    "notification_other",  // customLayout
    "txtOtherTitle",        // titleViewId
    "txtOtherSubtitle",    // subtitleViewId
    "txtOtherTimer"        // timerViewId
);
```

### 4. Arrêter le service

```java
ThunderBgServiceHelper.stopService(context);
```

### 5. Enregistrer une tâche

```java
// Avec une instance
MyTask task = new MyTask();
ThunderBgServiceHelper.registerTask(context, "myTask", task, 5000);

// Par nom de classe
ThunderBgServiceHelper.registerTask(
    context,
    "myTask",
    "com.yourpackage.MyTask",
    5000
);
```

### 6. Désenregistrer une tâche

```java
ThunderBgServiceHelper.unregisterTask(context, "myTask");
```

### 7. Vérifier si une tâche est enregistrée

```java
boolean isRegistered = ThunderBgServiceHelper.isTaskRegistered("myTask");
```

### 8. Récupérer les résultats d'une tâche

```java
JSONObject result = ThunderBgServiceHelper.getTaskResult(context, "myTask");
if (result != null) {
    String data = result.getString("data");
}
```

### 9. Émettre un événement

```java
ThunderBgServiceHelper.emitTaskEvent(context, "myTask", "Données");
```

### 10. Vérifier l'existence d'un layout

```java
boolean exists = ThunderBgServiceHelper.layoutExists(context, "notification_custom");
```

### 11. Exemple dans une Activity

```java
package com.yourpackage;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import com.webify.thunderbgservice.core.ThunderBgServiceHelper;

public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Button startBtn = findViewById(R.id.btnStart);
        startBtn.setOnClickListener(v -> {
            ThunderBgServiceHelper.startService(
                this,
                "Online",
                "Service démarré",
                true
            );
        });
        
        Button stopBtn = findViewById(R.id.btnStop);
        stopBtn.setOnClickListener(v -> {
            ThunderBgServiceHelper.stopService(this);
        });
    }
}
```

---

## 📚 API complète

### TypeScript/JavaScript

#### `start(options: StartOptions): Promise<{started: boolean}>`

Démarre le service foreground.

**Options:**
- `notificationTitle?: string` - Titre de la notification (optionnel)
- `notificationSubtitle?: string` - Sous-titre (optionnel)
- `enableLocation?: boolean` - Activer la localisation (défaut: true)
- `soundsEnabled?: boolean` - Activer les sons (défaut: false)
- `customLayout?: string` - **Requis** : Nom du layout personnalisé (sans .xml)
- `titleViewId?: string` - ID du TextView pour le titre
- `subtitleViewId?: string` - ID du TextView pour le sous-titre
- `timerViewId?: string` - ID du TextView pour le timer
- `viewData?: { [viewIdName: string]: string }` - **Nouveau** : Objet pour injecter des textes dans n'importe quel TextView
- `buttons?: Array<{ viewId: string; action: string; extras?: object }>` - **Nouveau** : Tableau de boutons cliquables

#### `stop(): Promise<{stopped: boolean}>`

Arrête le service et toutes les tâches.

#### `update(options: Partial<StartOptions>): Promise<{updated: boolean}>`

Met à jour la notification. Tous les paramètres sont optionnels.

#### `registerTask(options: RegisterTaskOptions): Promise<{registered: boolean}>`

Enregistre une tâche en arrière-plan.

**Options:**
- `taskId: string` - ID unique de la tâche
- `taskClass: string` - Nom complet de la classe Java
- `intervalMs: number` - Intervalle en millisecondes (minimum 1000)

#### `unregisterTask(taskId: string): Promise<{unregistered: boolean}>`

Désenregistre une tâche.

#### `getTaskResult(taskId: string): Promise<{result: any | null}>`

Récupère les résultats stockés d'une tâche.

#### `addListener(event: 'taskEvent', listener: Function): Promise<{remove: () => void}>`

Écoute les événements émis par les tâches.

#### `removeAllListeners(): Promise<void>`

Supprime tous les listeners.

### Java natif

Voir la section "Utilisation depuis Java natif" ci-dessus pour la liste complète des méthodes.

---

## 🏗️ Architecture

### Structure du package

```
com.webify.thunderbgservice/
├── core/                    # Fichiers principaux
│   ├── FgConstants.java     # Constantes
│   ├── ForegroundTaskService.java  # Service Android
│   ├── ThunderBgServicePlugin.java # Plugin Capacitor
│   └── ThunderBgServiceHelper.java # Helper publique
├── tasks/                   # Tâches en arrière-plan
│   ├── BackgroundTask.java  # Interface
│   ├── BackgroundTaskManager.java # Gestionnaire
│   ├── TaskEventEmitter.java # Émission d'événements
│   └── TaskResultStorage.java # Stockage de résultats
└── helpers/                 # Utilitaires
    ├── NotificationHelper.java # Gestion notifications
    └── LocationHelper.java     # Gestion localisation
```

### Flux de données

```
┌─────────────┐
│   App JS    │
│  (TypeScript)│
└──────┬──────┘
       │
       │ Capacitor Bridge
       │
┌──────▼──────────────────────┐
│  ThunderBgServicePlugin     │
│  (Capacitor Plugin)         │
└──────┬──────────────────────┘
       │
       │ Intents
       │
┌──────▼──────────────────────┐
│  ForegroundTaskService      │
│  (Android Service)           │
└──────┬──────────────────────┘
       │
       ├──► NotificationHelper
       ├──► LocationHelper
       └──► BackgroundTaskManager
                │
                └──► BackgroundTask (votre code)
```

---

## 💡 Exemples

### Exemple 1: Service de livraison

```typescript
class DeliveryService {
  async startDelivery() {
    // Démarrer le service
    await ThunderBgService.start({
      notificationTitle: 'Livraison en cours',
      notificationSubtitle: 'En route vers le client',
      enableLocation: true,
      customLayout: 'notification_delivery',
      titleViewId: 'txtDeliveryStatus',
      subtitleViewId: 'txtClientAddress',
      timerViewId: 'txtElapsedTime',
    });
    
    // Enregistrer une tâche de mise à jour de localisation
    await ThunderBgService.registerTask({
      taskId: 'locationUpdate',
      taskClass: 'com.yourpackage.DeliveryLocationTask',
      intervalMs: 5000,
    });
  }
  
  async updateDeliveryStatus(status: string, address: string) {
    await ThunderBgService.update({
      notificationTitle: status,
      notificationSubtitle: address,
    });
  }
  
  async stopDelivery() {
    await ThunderBgService.unregisterTask('locationUpdate');
    await ThunderBgService.stop();
  }
}
```

### Exemple 2: Application de fitness

```typescript
class FitnessTracker {
  async startWorkout() {
    await ThunderBgService.start({
      notificationTitle: 'Entraînement en cours',
      notificationSubtitle: 'Course',
      enableLocation: true,
      customLayout: 'notification_workout',
      titleViewId: 'txtWorkoutType',
      subtitleViewId: 'txtDistance',
      timerViewId: 'txtDuration',
    });
    
    // Enregistrer une tâche de tracking
    await ThunderBgService.registerTask({
      taskId: 'fitnessTracking',
      taskClass: 'com.yourpackage.FitnessTrackingTask',
      intervalMs: 2000,
    });
  }
  
  async updateWorkoutData(distance: string) {
    await ThunderBgService.update({
      notificationSubtitle: `Distance: ${distance} km`,
    });
  }
}
```

### Exemple 3: Application de monitoring

```typescript
class MonitoringService {
  async startMonitoring() {
    await ThunderBgService.start({
      notificationTitle: 'Monitoring actif',
      notificationSubtitle: 'Surveillance en cours',
      enableLocation: false,
    });
    
    // Tâche de vérification système
    await ThunderBgService.registerTask({
      taskId: 'systemCheck',
      taskClass: 'com.yourpackage.SystemCheckTask',
      intervalMs: 30000,
    });
    
    // Écouter les événements
    ThunderBgService.addListener('taskEvent', (data) => {
      if (data.taskId === 'systemCheck') {
        this.handleSystemAlert(data.data);
      }
    });
  }
  
  private handleSystemAlert(data: any) {
    // Traiter les alertes système
    console.log('Alert:', data);
  }
  
  async getSystemStatus() {
    const { result } = await ThunderBgService.getTaskResult('systemCheck');
    return result;
  }
}
```

---

## 🔧 Dépannage

### Le service s'arrête quand l'app est fermée

✅ **Solution**: Le service est conçu pour persister. Vérifiez :
- Les permissions sont accordées
- Le service est bien démarré avec `start()`
- Aucune restriction de batterie n'est active

### La notification ne s'affiche pas

✅ **Solution**:
1. Vérifiez que la permission `POST_NOTIFICATIONS` est accordée (Android 13+)
2. Vérifiez que le canal de notification existe
3. Vérifiez que le service est bien démarré

### Les tâches ne s'exécutent pas

✅ **Solution**:
1. Vérifiez que le service est démarré avant d'enregistrer les tâches
2. Vérifiez que la classe Java existe et implémente `BackgroundTask`
3. Vérifiez les logs Android (logcat) pour les erreurs

### Les événements ne sont pas reçus

✅ **Solution**:
- Les événements ne sont émis que si l'app est active
- Utilisez `getTaskResult()` pour récupérer les données si l'app était fermée
- Vérifiez que le listener est bien enregistré

### Le layout personnalisé ne s'affiche pas

✅ **Solution**:
1. Vérifiez que le fichier XML existe dans `res/layout/`
2. Vérifiez que les IDs des TextViews sont corrects
3. Vérifiez les logs pour voir les IDs résolus
4. **Important** : Le plugin ne contient pas de layout par défaut. Vous devez toujours fournir `customLayout`

### Les boutons dans la notification ne fonctionnent pas

✅ **Solution**:
1. Vérifiez que le `BroadcastReceiver` est déclaré dans `AndroidManifest.xml` avec `android:exported="true"`
2. Vérifiez que les actions dans `buttons` correspondent exactement aux actions déclarées dans le Receiver
3. Vérifiez que les IDs de boutons dans `buttons` correspondent exactement aux IDs dans votre XML (sans `@+id/`)
4. Utilisez `Button` ou `TextView` avec `android:clickable="true"` dans votre layout
5. Vérifiez les logs Logcat (filtre `ThunderBG`) pour voir si les boutons sont bindés : cherchez `"Button bound: viewId=..."` ou `"⚠️ NO RECEIVER FOUND"`
6. Vérifiez les logs de votre Receiver pour voir si les intents sont reçus : ajoutez `Log.d("Receiver", "Received: " + intent.getAction())`

### Erreur de compilation Java

✅ **Solution**:
- Vérifiez que tous les imports sont corrects
- Utilisez `com.webify.thunderbgservice.core.*` pour les classes core
- Utilisez `com.webify.thunderbgservice.tasks.*` pour les tâches

---

## 📖 Ressources supplémentaires

### Documentation
- [📘 Guide de démarrage rapide](./docs/QUICK_START.md) - Commencez en 5 minutes
- [📚 Référence API complète](./docs/API_REFERENCE.md) - Toutes les méthodes détaillées
- [💡 Cas d'usage pratiques](./docs/USE_CASES.md) - Exemples réels d'utilisation
- [🏗️ Architecture et organisation](./docs/ORGANIZATION.md) - Structure du package

### Exemples
- [📁 Index des exemples](./examples/INDEX.md) - Guide de navigation
- [📝 Exemples TypeScript/JavaScript](./examples/EXAMPLE_usage.ts)
- [☕ Exemples Java natif](./examples/EXAMPLE_NativeJavaUsage.java)
- [🔄 Tâches en arrière-plan](./examples/EXAMPLE_BackgroundTask_usage.ts)
- [📡 Communication JS/Java](./examples/EXAMPLE_TaskJSCommunication.ts)

### Guides
- [🎯 Guide des tâches](./examples/EXAMPLE_BackgroundTasks_README.md)
- [🔌 Guide d'utilisation native](./examples/EXAMPLE_NativeUsage_README.md)
- [💬 Guide de communication JS/TS](./examples/EXAMPLE_TS_JS_Tasks_README.md)

---

## 📝 Notes importantes

1. **UI 100% app-driven**: Le plugin ne contient aucune UI/logique par défaut. Vous devez fournir `customLayout`, `viewData` et `buttons` depuis votre app.
2. **Layout requis**: Vous devez toujours fournir `customLayout` lors du démarrage. Créez vos layouts dans `res/layout/` de votre app.
3. **Persistance automatique**: L'UI et l'état (layout, viewData, buttons) sont automatiquement sauvegardés et restaurés après fermeture/réouverture de l'app.
4. **Batterie**: Les intervalles courts peuvent drainer la batterie. Utilisez des intervalles >= 5000ms pour les tâches.
5. **Permissions**: Toujours demander les permissions runtime avant d'utiliser le service.
6. **Tâches**: Les tâches doivent être en Java natif, pas en JavaScript.
7. **Boutons cliquables**: Utilisez `Button` ou `TextView` avec `android:clickable="true"` dans vos layouts. Déclarez le `BroadcastReceiver` avec `android:exported="true"` dans le Manifest.

---

## 🆘 Support

Pour toute question ou problème, consultez les exemples dans le dossier `examples/` ou ouvrez une issue sur le repository.

---

**Version**: 0.1.0  
**Capacitor**: ^7.0.0  
**Android**: API 21+

