# Guide d'utilisation - Dynamisation de la notification

Ce guide explique comment utiliser les templates fournis pour dynamiser la notification selon l'état de votre application.

## Fichiers fournis

1. **EXAMPLE_NotificationDynamicHelper.java** - Classe Java helper avec méthodes statiques
2. **EXAMPLE_usage.ts** - Exemples d'utilisation TypeScript/Angular
3. **EXAMPLE_notification_online.xml** - Exemple de layout XML personnalisé

## Étapes d'installation

### 1. Copier la classe Java helper

Copiez `EXAMPLE_NotificationDynamicHelper.java` dans votre projet Android :
```
android/app/src/main/java/com/votrepackage/NotificationDynamicHelper.java
```

**Modifiez** :
- Le package : `package com.votrepackage;` (remplacez par votre package)
- Ajustez les méthodes selon vos besoins (états, layouts, IDs)

### 2. Créer les layouts XML

Créez les layouts dans `android/app/src/main/res/layout/` :

- `notification_online.xml` (pour l'état ONLINE)
- `notification_riding.xml` (pour l'état ON_RIDE/DRIVING)
- `notification_waiting.xml` (pour l'état WAITING_PICKUP)
- `notification_arrived.xml` (pour l'état ARRIVED)
- `notification_default.xml` (layout par défaut)

**Important** : Assurez-vous que les IDs dans vos layouts correspondent à ceux passés au plugin :
- `txtTitle` (ou celui que vous passez dans `titleViewId`)
- `txtSubtitle` (ou celui que vous passez dans `subtitleViewId`)
- `txtTimer` (ou celui que vous passez dans `timerViewId`)

### 3. Utiliser dans votre code TypeScript/Angular

Copiez les helpers TypeScript de `EXAMPLE_usage.ts` dans votre service Angular :

```typescript
import { ThunderBgService } from '@webify/capacitor-thunder-bg-service';
import { NotificationDynamicHelper, AppState, NotificationStateManager } from './notification-helpers';

// Dans votre service
class MyService {
  private stateManager = new NotificationStateManager();

  async goOnline() {
    await this.stateManager.goOnline();
  }

  onRideAssigned() {
    this.stateManager.startRide();
  }
}
```

## Exemples d'utilisation

### Exemple 1: Démarrer avec état ONLINE

```typescript
const options = NotificationDynamicHelper.buildOptionsForState(AppState.ONLINE, true);
await ThunderBgService.start(options);
```

### Exemple 2: Changer d'état dynamiquement (avec changement de layout!)

```typescript
// Changer le titre/sous-titre ET le layout sans redémarrer le service
const options = NotificationDynamicHelper.buildOptionsForState(AppState.ON_RIDE);
await ThunderBgService.update({
  notificationTitle: options.notificationTitle,
  notificationSubtitle: options.notificationSubtitle,
  customLayout: options.customLayout,        // NOUVEAU: changer le layout
  titleViewId: options.titleViewId,          // Nouveaux IDs pour le nouveau layout
  subtitleViewId: options.subtitleViewId,
  timerViewId: options.timerViewId,
});
```

### Exemple 3: Changer de layout/écran dynamiquement ⭐ NOUVEAU

```typescript
// Changer de layout à tout moment sans redémarrer le service
await ThunderBgService.update({
  notificationTitle: 'Nouveau titre',
  notificationSubtitle: 'Nouveau sous-titre',
  customLayout: 'notification_riding',      // Changer vers un autre layout
  titleViewId: 'txtDriverStatus',            // IDs du nouveau layout
  subtitleViewId: 'txtDestination',
  timerViewId: 'txtElapsedTime',
});
```

### Exemple 4: Utiliser un State Manager (avec changement de layout automatique)

```typescript
const stateManager = new NotificationStateManager();
await stateManager.goOnline();              // Layout: notification_online
await stateManager.startRide();             // Layout change vers: notification_riding
await stateManager.waitingForPickup();      // Layout change vers: notification_waiting
await stateManager.arrived();               // Layout change vers: notification_arrived
await stateManager.completeRide();
```

### Exemple 5: Gestionnaire de layout dynamique

```typescript
const layoutManager = new DynamicLayoutManager();

// Change automatiquement de layout selon l'état
await layoutManager.switchToState(AppState.ONLINE);    // Layout: notification_online
await layoutManager.switchToState(AppState.ON_RIDE);   // Layout: notification_riding
await layoutManager.switchToState(AppState.ARRIVED);   // Layout: notification_arrived
```

## Personnalisation

### Ajouter de nouveaux états

1. Dans `NotificationDynamicHelper.java`, ajoutez dans l'enum `AppState` :
```java
NEW_STATE,  // Votre nouvel état
```

2. Ajoutez les méthodes correspondantes :
```java
public static String getTitleForState(AppState state) {
    switch (state) {
        case NEW_STATE:
            return "Mon nouvel état";
        // ...
    }
}
```

3. Créez le layout correspondant : `notification_new_state.xml`

### Changer les IDs de vues

Si vos layouts utilisent des IDs différents, modifiez `getViewIdsForLayout()` :

```java
public static NotificationViewIds getViewIdsForLayout(String layoutName) {
    NotificationViewIds ids = new NotificationViewIds();
    if (layoutName.contains("my_custom_layout")) {
        ids.titleId = "myCustomTitleId";
        ids.subtitleId = "myCustomSubtitleId";
        ids.timerId = "myCustomTimerId";
    }
    return ids;
}
```

## Structure des layouts

Chaque layout XML doit contenir au minimum :
- Un `TextView` pour le titre (ID correspondant à `titleViewId`)
- Un `TextView` pour le sous-titre (ID correspondant à `subtitleViewId`)
- Un `TextView` pour le timer (ID correspondant à `timerViewId`)

Le plugin mettra à jour automatiquement ces vues toutes les secondes avec le timer.

## Logs de debug

Le plugin génère des logs dans logcat :
- `ThunderBG: Service created`
- `ThunderBG: Service started in foreground`
- `ThunderBG: Alive #N • HH:MM:SS` (toutes les secondes)

Filtrez avec `ThunderBG` pour voir l'activité du service.

## Notes importantes

1. **⭐ Le layout peut être changé dynamiquement !** : Vous pouvez maintenant changer de layout à tout moment en appelant `update()` avec les nouveaux paramètres `customLayout`, `titleViewId`, `subtitleViewId`, `timerViewId`. Pas besoin de redémarrer le service.

2. **Les IDs doivent correspondre** : Les IDs passés dans `titleViewId`, `subtitleViewId`, `timerViewId` doivent exister dans votre layout XML. Chaque layout peut avoir des IDs différents.

3. **Le timer est automatique** : Le plugin met à jour automatiquement le timer toutes les secondes. Vous n'avez pas besoin de le gérer manuellement.

4. **Changement de layout** : Quand vous changez de layout, assurez-vous de passer les bons IDs correspondant au nouveau layout. Le plugin reconstruira la notification avec le nouveau layout.

5. **Permissions** : Assurez-vous d'avoir les permissions nécessaires (Notifications, Localisation) avant d'appeler `start()`.

6. **Logs** : Le plugin log `"Layout changed to: <nom_layout>"` dans logcat quand un changement de layout est détecté.

