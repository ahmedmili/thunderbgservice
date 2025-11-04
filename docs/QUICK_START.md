# Guide de démarrage rapide

Guide rapide pour commencer à utiliser le plugin en 5 minutes.

## 1. Installation (2 min)

```bash
npm install @webify/capacitor-thunder-bg-service
npx cap sync android
```

## 2. Code minimal (3 min)

### TypeScript/JavaScript

```typescript
import { ThunderBgService } from '@webify/capacitor-thunder-bg-service';

// Démarrer
await ThunderBgService.start({
  notificationTitle: 'Online',
  notificationSubtitle: 'Service actif',
  enableLocation: true,
});

// Mettre à jour
await ThunderBgService.update({
  notificationSubtitle: 'Nouveau statut',
});

// Arrêter
await ThunderBgService.stop();
```

### Java natif

```java
import com.webify.thunderbgservice.core.ThunderBgServiceHelper;

// Démarrer
ThunderBgServiceHelper.startService(
    context,
    "Online",
    "Service actif",
    true
);

// Mettre à jour
ThunderBgServiceHelper.updateNotification(
    context,
    "Online",
    "Nouveau statut"
);

// Arrêter
ThunderBgServiceHelper.stopService(context);
```

## 3. Cas d'usage courants

### Service simple avec notification

```typescript
// Démarrer
await ThunderBgService.start({
  notificationTitle: 'Mon service',
  notificationSubtitle: 'En cours d\'exécution',
});

// Le service continue même si l'app est fermée
```

### Service avec localisation

```typescript
await ThunderBgService.start({
  notificationTitle: 'Tracking GPS',
  notificationSubtitle: 'Localisation active',
  enableLocation: true,  // Active la localisation
});
```

### Notification personnalisée

1. Créez `android/app/src/main/res/layout/notification_custom.xml`
2. Utilisez-le :

```typescript
await ThunderBgService.start({
  notificationTitle: 'Titre',
  notificationSubtitle: 'Sous-titre',
  customLayout: 'notification_custom',
  titleViewId: 'txtTitle',
  subtitleViewId: 'txtSubtitle',
});
```

### Tâche en arrière-plan

1. Créez une classe Java :

```java
package com.yourpackage;

import android.content.Context;
import com.webify.thunderbgservice.tasks.BackgroundTask;

public class MyTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // Votre code ici
    }
}
```

2. Enregistrez-la :

```typescript
await ThunderBgService.registerTask({
  taskId: 'myTask',
  taskClass: 'com.yourpackage.MyTask',
  intervalMs: 5000,
});
```

## 4. Checklist de configuration

- [ ] Package installé
- [ ] `npx cap sync android` exécuté
- [ ] Permissions demandées à l'utilisateur (Android 13+)
- [ ] Service démarré avec `start()`
- [ ] Notification visible dans la barre d'état

## 5. Prochaines étapes

- [Lire le README complet](../README.md)
- [Voir les exemples](../examples/)
- [Consulter l'API complète](../README.md#api-complète)

---

**C'est tout !** Vous êtes prêt à utiliser le plugin. 🚀

