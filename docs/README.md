# @webify/capacitor-thunder-bg-service

Service Android au premier plan avec notification personnalisée et option localisation. iOS: stub.

## Installation
```bash
npm i @webify/capacitor-thunder-bg-service
npx cap sync
```

## API
```ts
import { ThunderBgService } from '@webify/capacitor-thunder-bg-service';
await ThunderBgService.start({ notificationTitle: 'Online', notificationSubtitle: 'Waiting', enableLocation: true });
await ThunderBgService.update({ notificationSubtitle: 'Working' });
await ThunderBgService.stop();
```

## Android

### Permissions
- INTERNET, WAKE_LOCK, POST_NOTIFICATIONS, ACCESS_FINE/COARSE_LOCATION, FOREGROUND_SERVICE(+LOCATION)

### Personnalisation de la notification (Layout par nom)
Si vous préférez passer juste les noms de layout/IDs:
```ts
await ThunderBgService.start({
  notificationTitle: 'Online',
  notificationSubtitle: 'Waiting',
  enableLocation: true,
  customLayout: 'notification_driver',   // R.layout.notification_driver
  titleViewId: 'txtTitle',               // R.id.txtTitle
  subtitleViewId: 'txtSubtitle',         // R.id.txtSubtitle
  timerViewId: 'txtTimer',                // R.id.txtTimer (optionnel)
});
```

### Utilisation depuis le code Java natif ⭐ NOUVEAU

Vous pouvez utiliser le plugin directement depuis votre code Java natif sans passer par le bridge JS/TS:

```java
import com.webify.thunderbgservice.ThunderBgServiceHelper;

// Démarrer le service
ThunderBgServiceHelper.startService(
    context,
    "Online",
    "Waiting for rides",
    true  // enableLocation
);

// Mettre à jour la notification
ThunderBgServiceHelper.updateNotification(
    context,
    "En course",
    "Client XYZ → Destination",
    "notification_riding",  // Changer le layout
    "txtDriverStatus",
    "txtDestination",
    "txtElapsedTime"
);

// Arrêter le service
ThunderBgServiceHelper.stopService(context);
```

**Voir les exemples complets**:
- `EXAMPLE_NativeJavaUsage.java` - Exemples d'utilisation depuis Java natif
- `EXAMPLE_ActivityUsage.java` - Utilisation dans une Activity
- `EXAMPLE_NativeUsage_README.md` - Documentation complète

### Tâches en arrière-plan ⭐ NOUVEAU

Vous pouvez enregistrer des fonctions Java qui s'exécutent périodiquement **même si l'app est fermée** :

**1. Créez une classe Java qui implémente `BackgroundTask`**:
```java
package com.yourpackage;

import android.content.Context;
import com.webify.thunderbgservice.BackgroundTask;

public class MyTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // Votre code ici - s'exécute même si l'app est fermée !
        Log.i("MyTask", "Tâche exécutée: " + taskId);
    }
}
```

**2. Enregistrez-la depuis TypeScript/JS**:
```ts
await ThunderBgService.registerTask({
  taskId: 'myTask',
  taskClass: 'com.yourpackage.MyTask',  // Nom complet de la classe
  intervalMs: 5000  // Toutes les 5 secondes (minimum 1000ms)
});
```

**3. Ou depuis Java natif**:
```java
ThunderBgServiceHelper.registerTask(
    context,
    "myTask",
    "com.yourpackage.MyTask",
    5000
);
```

**Voir les exemples**:
- `EXAMPLE_BackgroundTask.java` - Exemples de tâches personnalisées
- `EXAMPLE_BackgroundTask_usage.ts` - Utilisation depuis TypeScript

## Test local
```bash
npm pack
# dans l'app hôte
npm i <chemin>/@webify/capacitor-thunder-bg-service-0.1.0.tgz
npx cap sync
```


