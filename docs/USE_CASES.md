# Cas d'usage et exemples pratiques

Collection de cas d'usage réels avec exemples de code complets.

## 📦 1. Application de livraison

### Besoin
- Service qui continue même si l'app est fermée
- Notification avec statut de livraison
- Mise à jour de localisation en temps réel
- Affichage du temps écoulé

### Solution

**TypeScript:**

```typescript
import { ThunderBgService } from '@webify/capacitor-thunder-bg-service';

class DeliveryService {
  async startDelivery(orderId: string, customerAddress: string) {
    // Démarrer avec layout personnalisé
    await ThunderBgService.start({
      notificationTitle: 'Livraison en cours',
      notificationSubtitle: customerAddress,
      enableLocation: true,
      customLayout: 'notification_delivery',
      titleViewId: 'txtDeliveryStatus',
      subtitleViewId: 'txtCustomerAddress',
      timerViewId: 'txtElapsedTime',
    });
    
    // Tâche de mise à jour de localisation
    await ThunderBgService.registerTask({
      taskId: 'locationUpdate',
      taskClass: 'com.yourpackage.DeliveryLocationTask',
      intervalMs: 5000,
    });
  }
  
  async updateStatus(status: 'picking' | 'delivering' | 'arrived') {
    const statuses = {
      picking: 'Récupération de la commande',
      delivering: 'En route vers le client',
      arrived: 'Arrivé à destination',
    };
    
    await ThunderBgService.update({
      notificationTitle: statuses[status],
    });
  }
  
  async completeDelivery() {
    await ThunderBgService.unregisterTask('locationUpdate');
    await ThunderBgService.stop();
  }
}
```

**Java (tâche de localisation):**

```java
package com.yourpackage;

import android.content.Context;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.tasks.TaskResultStorage;
import org.json.JSONObject;

public class DeliveryLocationTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        FusedLocationProviderClient fused = 
            LocationServices.getFusedLocationProviderClient(context);
        
        fused.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                JSONObject data = new JSONObject();
                data.put("latitude", location.getLatitude());
                data.put("longitude", location.getLongitude());
                data.put("timestamp", System.currentTimeMillis());
                
                TaskResultStorage.saveResult(context, taskId, data);
            }
        });
    }
}
```

---

## 🏃 2. Application de fitness

### Besoin
- Tracking GPS pendant l'entraînement
- Notification avec distance et durée
- Continue même si l'écran est éteint

### Solution

```typescript
class WorkoutTracker {
  private workoutType: string = 'running';
  private startTime: number = 0;
  
  async startWorkout(type: 'running' | 'cycling' | 'walking') {
    this.workoutType = type;
    this.startTime = Date.now();
    
    await ThunderBgService.start({
      notificationTitle: `${type.charAt(0).toUpperCase() + type.slice(1)} en cours`,
      notificationSubtitle: 'Distance: 0 km',
      enableLocation: true,
      customLayout: 'notification_workout',
      titleViewId: 'txtWorkoutType',
      subtitleViewId: 'txtDistance',
      timerViewId: 'txtDuration',
    });
    
    // Tâche de tracking
    await ThunderBgService.registerTask({
      taskId: 'workoutTracking',
      taskClass: 'com.yourpackage.WorkoutTrackingTask',
      intervalMs: 2000,
    });
  }
  
  async updateDistance(distance: number) {
    await ThunderBgService.update({
      notificationSubtitle: `Distance: ${distance.toFixed(2)} km`,
    });
  }
  
  async stopWorkout() {
    await ThunderBgService.unregisterTask('workoutTracking');
    await ThunderBgService.stop();
  }
}
```

---

## 🚗 3. Application de transport (Uber-like)

### Besoin
- Changer de layout selon l'état (en ligne, en course, arrivé)
- Mise à jour dynamique de la notification
- Tracking GPS

### Solution

```typescript
enum DriverState {
  OFFLINE = 'offline',
  ONLINE = 'online',
  WAITING = 'waiting',
  ON_RIDE = 'on_ride',
  ARRIVED = 'arrived',
}

class DriverService {
  private currentState: DriverState = DriverState.OFFLINE;
  
  async goOnline() {
    this.currentState = DriverState.ONLINE;
    await this.updateState();
    
    await ThunderBgService.start({
      notificationTitle: 'En ligne',
      notificationSubtitle: 'En attente de courses',
      enableLocation: true,
      customLayout: 'notification_online',
      titleViewId: 'txtDriverStatus',
      subtitleViewId: 'txtWaitingMessage',
    });
  }
  
  async acceptRide(clientName: string, pickupAddress: string) {
    this.currentState = DriverState.WAITING;
    await ThunderBgService.update({
      notificationTitle: 'Course acceptée',
      notificationSubtitle: `${clientName} - ${pickupAddress}`,
      customLayout: 'notification_waiting',
      titleViewId: 'txtRideStatus',
      subtitleViewId: 'txtClientInfo',
    });
  }
  
  async startRide(destination: string) {
    this.currentState = DriverState.ON_RIDE;
    await ThunderBgService.update({
      notificationTitle: 'En course',
      notificationSubtitle: `Destination: ${destination}`,
      customLayout: 'notification_riding',
      titleViewId: 'txtRideStatus',
      subtitleViewId: 'txtDestination',
      timerViewId: 'txtElapsedTime',
    });
    
    // Tracking GPS
    await ThunderBgService.registerTask({
      taskId: 'rideTracking',
      taskClass: 'com.yourpackage.RideTrackingTask',
      intervalMs: 5000,
    });
  }
  
  async arriveAtDestination() {
    this.currentState = DriverState.ARRIVED;
    await ThunderBgService.update({
      notificationTitle: 'Arrivé à destination',
      notificationSubtitle: 'Course terminée',
      customLayout: 'notification_arrived',
      titleViewId: 'txtArrivalStatus',
      subtitleViewId: 'txtCompletionMessage',
    });
  }
  
  async completeRide() {
    await ThunderBgService.unregisterTask('rideTracking');
    this.currentState = DriverState.ONLINE;
    await this.updateState();
  }
  
  private async updateState() {
    // Logique de mise à jour selon l'état
  }
}
```

---

## 📊 4. Application de monitoring système

### Besoin
- Vérification périodique du système
- Alerts en cas de problème
- Stockage des résultats pour analyse

### Solution

```typescript
class SystemMonitor {
  async startMonitoring() {
    await ThunderBgService.start({
      notificationTitle: 'Monitoring actif',
      notificationSubtitle: 'Surveillance en cours',
      enableLocation: false,
    });
    
    // Tâche de vérification
    await ThunderBgService.registerTask({
      taskId: 'systemCheck',
      taskClass: 'com.yourpackage.SystemCheckTask',
      intervalMs: 30000, // Toutes les 30 secondes
    });
    
    // Écouter les alertes
    ThunderBgService.addListener('taskEvent', (data) => {
      if (data.taskId === 'systemCheck') {
        this.handleAlert(data.data);
      }
    });
  }
  
  private handleAlert(alert: any) {
    if (alert.type === 'error') {
      // Afficher une notification d'erreur
      this.showErrorNotification(alert.message);
    }
  }
  
  async getSystemStatus() {
    const { result } = await ThunderBgService.getTaskResult('systemCheck');
    return result;
  }
}
```

**Java (tâche de monitoring):**

```java
package com.yourpackage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.tasks.TaskResultStorage;
import com.webify.thunderbgservice.tasks.TaskEventEmitter;
import org.json.JSONObject;

public class SystemCheckTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // Vérifier le réseau
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnected();
        
        // Vérifier la batterie
        // ... votre code de vérification
        
        JSONObject status = new JSONObject();
        status.put("networkConnected", isConnected);
        status.put("timestamp", System.currentTimeMillis());
        
        TaskResultStorage.saveResult(context, taskId, status);
        
        if (!isConnected) {
            JSONObject alert = new JSONObject();
            alert.put("type", "error");
            alert.put("message", "Network disconnected");
            TaskEventEmitter.emit(context, taskId, alert);
        }
    }
}
```

---

## 🔄 5. Synchronisation de données

### Besoin
- Synchronisation périodique avec le serveur
- Mise à jour de la notification avec le statut
- Continue même si l'app est fermée

### Solution

```typescript
class DataSyncService {
  async startSync() {
    await ThunderBgService.start({
      notificationTitle: 'Synchronisation',
      notificationSubtitle: 'En attente...',
      enableLocation: false,
    });
    
    // Tâche de synchronisation
    await ThunderBgService.registerTask({
      taskId: 'dataSync',
      taskClass: 'com.yourpackage.DataSyncTask',
      intervalMs: 60000, // Toutes les minutes
    });
    
    // Écouter les résultats
    ThunderBgService.addListener('taskEvent', (data) => {
      if (data.taskId === 'dataSync') {
        this.updateSyncStatus(data.data);
      }
    });
  }
  
  private async updateSyncStatus(status: any) {
    await ThunderBgService.update({
      notificationSubtitle: `Dernière sync: ${new Date().toLocaleTimeString()}`,
    });
  }
  
  async getSyncResults() {
    const { result } = await ThunderBgService.getTaskResult('dataSync');
    return result;
  }
}
```

**Java (tâche de synchronisation):**

```java
package com.yourpackage;

import android.content.Context;
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.tasks.TaskResultStorage;
import com.webify.thunderbgservice.tasks.TaskEventEmitter;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataSyncTask implements BackgroundTask {
    private OkHttpClient client = new OkHttpClient();
    
    @Override
    public void execute(Context context, String taskId) {
        try {
            // Appeler votre API
            Request request = new Request.Builder()
                .url("https://your-api.com/sync")
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                
                JSONObject result = new JSONObject();
                result.put("status", "success");
                result.put("lastSync", System.currentTimeMillis());
                result.put("data", responseBody);
                
                TaskResultStorage.saveResult(context, taskId, result);
                TaskEventEmitter.emit(context, taskId, "Sync successful");
            }
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", e.getMessage());
            TaskResultStorage.saveResult(context, taskId, error);
            TaskEventEmitter.emit(context, taskId, "Sync failed: " + e.getMessage());
        }
    }
}
```

---

## 💡 Bonnes pratiques

1. **Toujours démarrer le service avant d'enregistrer les tâches**
2. **Utiliser des intervalles raisonnables** (>= 5000ms pour économiser la batterie)
3. **Gérer les erreurs** dans les tâches Java
4. **Stocker les résultats** dans TaskResultStorage pour récupération ultérieure
5. **Émettre des événements** pour communication temps réel (si l'app est active)
6. **Nettoyer les ressources** dans `onUnregistered()`

---

## 🔗 Voir aussi

- [README complet](../README.md)
- [Guide de démarrage rapide](./QUICK_START.md)
- [Exemples de code](../examples/)

