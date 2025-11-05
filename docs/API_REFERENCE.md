# 📚 Référence API complète

Référence complète de toutes les méthodes et interfaces de `@ahmed-mili/capacitor-thunder-bg-service`.

## 📦 TypeScript/JavaScript API

### `start(options: StartOptions): Promise<{started: boolean}>`

Démarre le service foreground avec notification personnalisée.

**Paramètres:**

```typescript
interface StartOptions {
  customLayout: string;              // REQUIS: Nom du layout (sans .xml)
  titleViewId?: string;               // ID du TextView pour le titre
  subtitleViewId?: string;            // ID du TextView pour le sous-titre
  timerViewId?: string;               // ID du TextView pour le timer
  enableLocation?: boolean;             // Activer la localisation (défaut: true)
  soundsEnabled?: boolean;             // Activer les sons (défaut: false)
  viewData?: {                         // Injection dynamique de textes
    [viewIdName: string]: string;
  };
  buttons?: Array<{                   // Boutons cliquables
    viewId: string;                    // ID du bouton dans le XML
    action: string;                     // Action pour le BroadcastReceiver
    extras?: object;                    // Extras optionnels
  }>;
}
```

**Exemple:**

```typescript
await ThunderBgService.start({
  customLayout: 'notification_online',
  titleViewId: 'txtTitle',
  subtitleViewId: 'txtSubtitle',
  timerViewId: 'txtTimer',
  enableLocation: true,
  viewData: {
    txtTitle: 'Online',
    txtSubtitle: 'En attente',
    txtTimer: '00:00:00',
  },
  buttons: [
    { viewId: 'btnAction', action: 'com.yourapp.ACTION_CLICK' },
  ],
});
```

**Retourne:** `Promise<{started: boolean}>`

---

### `stop(): Promise<{stopped: boolean}>`

Arrête le service foreground et toutes les tâches enregistrées.

**Exemple:**

```typescript
await ThunderBgService.stop();
```

**Retourne:** `Promise<{stopped: boolean}>`

---

### `update(options: Partial<StartOptions>): Promise<{updated: boolean}>`

Met à jour la notification. Tous les paramètres sont optionnels.

**Exemple:**

```typescript
// Mettre à jour uniquement les textes
await ThunderBgService.update({
  viewData: {
    txtTitle: 'Nouveau titre',
    txtSubtitle: 'Nouveau sous-titre',
  },
});

// Changer de layout
await ThunderBgService.update({
  customLayout: 'notification_riding',
  titleViewId: 'txtDriverStatus',
  subtitleViewId: 'txtDestination',
  viewData: {
    txtDriverStatus: 'En cours',
    txtDestination: 'Destination',
  },
});

// Mettre à jour les boutons
await ThunderBgService.update({
  buttons: [
    { viewId: 'btnNext', action: 'com.yourapp.ACTION_NEXT' },
  ],
});
```

**Retourne:** `Promise<{updated: boolean}>`

---

### `registerTask(options: RegisterTaskOptions): Promise<{registered: boolean}>`

Enregistre une tâche en arrière-plan qui s'exécutera périodiquement.

**Paramètres:**

```typescript
interface RegisterTaskOptions {
  taskId: string;                      // ID unique de la tâche
  taskClass: string;                    // Nom complet de la classe Java
  intervalMs: number;                   // Intervalle en ms (minimum 1000)
}
```

**Exemple:**

```typescript
await ThunderBgService.registerTask({
  taskId: 'myTask',
  taskClass: 'com.yourapp.MyBackgroundTask',
  intervalMs: 5000, // Toutes les 5 secondes
});
```

**Retourne:** `Promise<{registered: boolean}>`

**Note:** La classe Java doit implémenter `com.ahmedmili.thunderbgservice.tasks.BackgroundTask`.

---

### `unregisterTask(taskId: string): Promise<{unregistered: boolean}>`

Désenregistre une tâche en arrière-plan.

**Exemple:**

```typescript
await ThunderBgService.unregisterTask('myTask');
```

**Retourne:** `Promise<{unregistered: boolean}>`

---

### `getTaskResult(taskId: string): Promise<{result: any | null}>`

Récupère les résultats stockés d'une tâche.

**Exemple:**

```typescript
const result = await ThunderBgService.getTaskResult('myTask');
if (result.result) {
  console.log('Données:', result.result);
}
```

**Retourne:** `Promise<{result: any | null}>`

---

### `addListener(event: 'taskEvent', listener: Function): Promise<{remove: () => void}>`

Écoute les événements émis par les tâches Java.

**Exemple:**

```typescript
const listener = await ThunderBgService.addListener('taskEvent', (data) => {
  console.log('Événement:', data.taskId);
  console.log('Données:', data.data);
  console.log('Timestamp:', data.timestamp);
});

// Supprimer le listener
listener.remove();
```

**Retourne:** `Promise<{remove: () => void}>`

**Note:** Les événements ne sont émis que si l'app est active. Sinon, les données sont stockées et peuvent être récupérées avec `getTaskResult()`.

---

### `removeAllListeners(): Promise<void>`

Supprime tous les listeners d'événements.

**Exemple:**

```typescript
await ThunderBgService.removeAllListeners();
```

**Retourne:** `Promise<void>`

---

## ☕ Java Native API

### `ThunderBgServiceHelper`

Classe helper publique pour utiliser le plugin depuis le code Java natif.

**Package:** `com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper`

#### Méthodes statiques

##### `startService(Context, String, String, boolean)`

```java
ThunderBgServiceHelper.startService(
    context,
    "Online",           // Titre
    "En attente",       // Sous-titre
    true                // enableLocation
);
```

##### `startService(Context, String, String, boolean, String, String, String, String)`

```java
ThunderBgServiceHelper.startService(
    context,
    "Online",
    "En attente",
    true,
    "notification_online",  // customLayout
    "txtTitle",             // titleViewId
    "txtSubtitle",          // subtitleViewId
    "txtTimer"              // timerViewId
);
```

##### `stopService(Context)`

```java
ThunderBgServiceHelper.stopService(context);
```

##### `updateNotification(Context, String, String)`

```java
ThunderBgServiceHelper.updateNotification(
    context,
    "Nouveau titre",
    "Nouveau sous-titre"
);
```

##### `registerTask(Context, String, String, long)`

```java
ThunderBgServiceHelper.registerTask(
    context,
    "myTask",                               // taskId
    "com.yourapp.MyBackgroundTask",         // taskClass
    5000                                    // intervalMs
);
```

##### `unregisterTask(Context, String)`

```java
ThunderBgServiceHelper.unregisterTask(context, "myTask");
```

##### `getTaskResult(Context, String)`

```java
JSONObject result = ThunderBgServiceHelper.getTaskResult(context, "myTask");
if (result != null) {
    String data = result.optString("data");
}
```

##### `emitTaskEvent(Context, String, Object)`

```java
ThunderBgServiceHelper.emitTaskEvent(context, "myTask", "Données");
```

##### `layoutExists(Context, String)`

```java
boolean exists = ThunderBgServiceHelper.layoutExists(context, "notification_custom");
```

---

## 🎯 Interface BackgroundTask

Pour créer une tâche en arrière-plan, implémentez cette interface :

**Package:** `com.ahmedmili.thunderbgservice.tasks.BackgroundTask`

```java
public interface BackgroundTask {
    void execute(Context context, String taskId);
}
```

**Exemple:**

```java
package com.yourapp;

import android.content.Context;
import com.ahmedmili.thunderbgservice.tasks.BackgroundTask;
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;

public class MyBackgroundTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // Votre logique ici
        ThunderBgServiceHelper.emitTaskEvent(context, taskId, "Données");
    }
}
```

---

## 📝 Types et Interfaces

### `StartOptions`

```typescript
interface StartOptions {
  customLayout: string;              // REQUIS
  titleViewId?: string;
  subtitleViewId?: string;
  timerViewId?: string;
  enableLocation?: boolean;
  soundsEnabled?: boolean;
  viewData?: { [viewIdName: string]: string };
  buttons?: Array<{
    viewId: string;
    action: string;
    extras?: object;
  }>;
}
```

### `RegisterTaskOptions`

```typescript
interface RegisterTaskOptions {
  taskId: string;
  taskClass: string;
  intervalMs: number; // minimum 1000
}
```

### `TaskEvent`

```typescript
interface TaskEvent {
  taskId: string;
  data: any;
  timestamp: number;
}
```

---

## 🔗 Voir aussi

- [📘 Guide de démarrage rapide](./QUICK_START.md)
- [💡 Cas d'usage pratiques](./USE_CASES.md)
- [📝 Exemples complets](../examples/)
- [📖 README principal](../README.md)

