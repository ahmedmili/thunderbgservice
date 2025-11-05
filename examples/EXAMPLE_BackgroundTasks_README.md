# Guide: Tâches en arrière-plan

Ce guide explique comment créer et enregistrer des tâches qui s'exécutent périodiquement **même si l'app est fermée**.

## Principe

Les tâches sont exécutées dans le service foreground Android, ce qui signifie qu'elles continuent de s'exécuter même si :
- L'app est en arrière-plan
- L'app est fermée (swipe kill)
- L'écran est éteint
- Le téléphone est verrouillé

## Création d'une tâche

### Étape 1: Créer la classe Java

Créez une classe dans votre app qui implémente `BackgroundTask` :

```java
package com.yourpackage;

import android.content.Context;
import android.util.Log;
import com.ahmedmili.thunderbgservice.tasks.BackgroundTask;

public class MyCustomTask implements BackgroundTask {
    private static final String TAG = "MyCustomTask";
    
    @Override
    public void execute(Context context, String taskId) {
        // Votre code ici - s'exécute même si l'app est fermée !
        Log.i(TAG, "Tâche exécutée: " + taskId);
        
        // Exemples d'actions possibles:
        // - Vérifier l'état du réseau
        // - Envoyer des requêtes HTTP
        // - Mettre à jour la notification
        // - Vérifier la localisation
        // - Synchroniser des données
        // - etc.
    }
}
```

### Étape 2: Enregistrer la tâche

**Depuis TypeScript/JS**:
```typescript
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

await ThunderBgService.registerTask({
  taskId: 'myTask',
  taskClass: 'com.yourpackage.MyCustomTask',  // Nom complet de la classe
  intervalMs: 5000  // Toutes les 5 secondes (minimum 1000ms)
});
```

**Depuis Java natif**:
```java
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;

// Méthode 1: Enregistrer directement une instance
MyCustomTask task = new MyCustomTask();
ThunderBgServiceHelper.registerTask(context, "myTask", task, 5000);

// Méthode 2: Enregistrer par nom de classe
ThunderBgServiceHelper.registerTask(
    context,
    "myTask",
    "com.yourpackage.MyCustomTask",
    5000
);
```

### Étape 3: Démarrer le service

Les tâches ne s'exécutent que si le service foreground est actif :

```typescript
// D'abord démarrer le service
await ThunderBgService.start({
  notificationTitle: 'Online',
  notificationSubtitle: 'Service actif',
  enableLocation: true,
});

// Puis enregistrer les tâches
await ThunderBgService.registerTask({
  taskId: 'myTask',
  taskClass: 'com.yourpackage.MyCustomTask',
  intervalMs: 5000,
});
```

## Exemples de tâches

### Exemple 1: Tâche de synchronisation

```java
public class SyncTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // Synchroniser les données avec le serveur
        // Même si l'app est fermée !
        syncDataToServer(context);
    }
    
    private void syncDataToServer(Context context) {
        // Votre code de synchronisation
    }
}
```

### Exemple 2: Tâche de vérification réseau

```java
public class NetworkCheckTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnected();
        
        // Mettre à jour la notification si le réseau change
        if (!isConnected) {
            ThunderBgServiceHelper.updateNotification(
                context,
                "Hors ligne",
                "Pas de connexion réseau"
            );
        }
    }
}
```

### Exemple 3: Tâche de mise à jour de localisation

```java
public class LocationUpdateTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // Vérifier la localisation et mettre à jour
        // Même si l'app est fermée !
        updateLocation(context);
    }
    
    private void updateLocation(Context context) {
        // Votre code de localisation
    }
}
```

### Exemple 4: Tâche qui met à jour la notification

```java
public class NotificationUpdateTask implements BackgroundTask {
    private int counter = 0;
    
    @Override
    public void execute(Context context, String taskId) {
        counter++;
        ThunderBgServiceHelper.updateNotification(
            context,
            null, // Garder le titre actuel
            "Updates: " + counter + " (background)"
        );
    }
}
```

## Gestion des tâches

### Désenregistrer une tâche

```typescript
await ThunderBgService.unregisterTask('myTask');
```

```java
ThunderBgServiceHelper.unregisterTask(context, "myTask");
```

### Arrêter toutes les tâches

Quand vous appelez `stop()`, toutes les tâches sont automatiquement arrêtées :

```typescript
await ThunderBgService.stop(); // Arrête le service ET toutes les tâches
```

## Limitations et bonnes pratiques

### Limitations

1. **Intervalle minimum** : 1000ms (1 seconde)
2. **Pas de code JavaScript** : Les tâches doivent être en Java natif
3. **Pas de callbacks JS** : Les tâches s'exécutent en natif, pas de retour vers JS quand l'app est fermée
4. **Batterie** : Des intervalles trop courts peuvent drainer la batterie

### Bonnes pratiques

1. **Intervalles raisonnables** : Utilisez des intervalles >= 5000ms (5 secondes) pour économiser la batterie
2. **Gestion d'erreurs** : Toujours gérer les exceptions dans `execute()`
3. **Logs** : Utilisez `Log` pour déboguer (visible dans logcat même si l'app est fermée)
4. **Arrêt propre** : Désenregistrez les tâches quand elles ne sont plus nécessaires

## Débogage

### Voir les logs

Filtrez logcat avec :
- `ThunderBG` - Logs du service
- `BackgroundTaskManager` - Logs du gestionnaire de tâches
- `MyCustomTask` - Logs de votre tâche (remplacez par votre nom de classe)

### Vérifier qu'une tâche est enregistrée

```java
boolean isRegistered = ThunderBgServiceHelper.isTaskRegistered("myTask");
Log.i("Debug", "Task registered: " + isRegistered);
```

## Notes importantes

1. **Les tâches s'exécutent dans le service** : Elles partagent le même thread pool que le service foreground
2. **Pas de blocage** : Ne bloquez pas la méthode `execute()` trop longtemps
3. **Context** : Utilisez `context.getApplicationContext()` si vous devez stocker le context
4. **Persistence** : Les tâches sont sauvegardées et peuvent être restaurées après redémarrage du service (si nécessaire)

## Fichiers d'exemple

- `EXAMPLE_BackgroundTask.java` - Exemples complets de tâches personnalisées
- `EXAMPLE_BackgroundTask_usage.ts` - Utilisation depuis TypeScript
- Ce README - Documentation complète

