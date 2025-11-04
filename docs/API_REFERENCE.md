# Référence API complète

Documentation complète de toutes les méthodes et classes disponibles.

## 📚 Table des matières

1. [API TypeScript/JavaScript](#api-typescriptjavascript)
2. [API Java - ThunderBgServiceHelper](#api-java---thunderbgservicehelper)
3. [API Java - BackgroundTask](#api-java---backgroundtask)
4. [API Java - Helpers](#api-java---helpers)
5. [Types et interfaces](#types-et-interfaces)

---

## API TypeScript/JavaScript

### `ThunderBgService.start(options: StartOptions)`

Démarre le service foreground avec notification.

**Paramètres:**
```typescript
interface StartOptions {
  notificationTitle: string;        // Titre de la notification (requis)
  notificationSubtitle?: string;    // Sous-titre (optionnel, défaut: "Running")
  enableLocation?: boolean;         // Activer la localisation (défaut: true)
  soundsEnabled?: boolean;          // Activer les sons (défaut: false)
  customLayout?: string;            // Nom du layout personnalisé (sans .xml)
  titleViewId?: string;             // ID du TextView pour le titre
  subtitleViewId?: string;          // ID du TextView pour le sous-titre
  timerViewId?: string;             // ID du TextView pour le timer
}
```

**Retour:** `Promise<{started: boolean}>`

**Exemple:**
```typescript
await ThunderBgService.start({
  notificationTitle: 'Online',
  notificationSubtitle: 'Service actif',
  enableLocation: true,
  customLayout: 'notification_custom',
  titleViewId: 'txtTitle',
  subtitleViewId: 'txtSubtitle',
});
```

---

### `ThunderBgService.stop()`

Arrête le service foreground et toutes les tâches enregistrées.

**Retour:** `Promise<{stopped: boolean}>`

**Exemple:**
```typescript
await ThunderBgService.stop();
```

---

### `ThunderBgService.update(options: Partial<StartOptions>)`

Met à jour la notification. Tous les paramètres sont optionnels.

**Paramètres:** Même interface que `StartOptions`, tous optionnels

**Retour:** `Promise<{updated: boolean}>`

**Exemple:**
```typescript
await ThunderBgService.update({
  notificationTitle: 'Nouveau titre',
  notificationSubtitle: 'Nouveau sous-titre',
  customLayout: 'notification_other',  // Change le layout
  titleViewId: 'txtOtherTitle',
});
```

---

### `ThunderBgService.registerTask(options: RegisterTaskOptions)`

Enregistre une tâche en arrière-plan qui s'exécute périodiquement.

**Paramètres:**
```typescript
interface RegisterTaskOptions {
  taskId: string;        // ID unique de la tâche (requis)
  taskClass: string;     // Nom complet de la classe Java (requis)
  intervalMs: number;     // Intervalle en millisecondes (requis, minimum 1000)
}
```

**Retour:** `Promise<{registered: boolean}>`

**Exemple:**
```typescript
await ThunderBgService.registerTask({
  taskId: 'myTask',
  taskClass: 'com.yourpackage.MyTask',
  intervalMs: 5000,
});
```

**Erreurs:**
- `Invalid parameters: taskId, taskClass required, intervalMs >= 1000`

---

### `ThunderBgService.unregisterTask(taskId: string)`

Désenregistre une tâche.

**Paramètres:**
- `taskId: string` - ID de la tâche à désenregistrer

**Retour:** `Promise<{unregistered: boolean}>`

**Exemple:**
```typescript
await ThunderBgService.unregisterTask('myTask');
```

**Erreurs:**
- `taskId is required`

---

### `ThunderBgService.getTaskResult(taskId: string)`

Récupère les résultats stockés d'une tâche.

**Paramètres:**
- `taskId: string` - ID de la tâche

**Retour:** `Promise<{result: any | null}>`

**Exemple:**
```typescript
const { result } = await ThunderBgService.getTaskResult('myTask');
if (result) {
  console.log('Données:', result.data);
  console.log('Timestamp:', result.timestamp);
}
```

**Erreurs:**
- `taskId is required`
- `Error getting task result: <message>`

---

### `ThunderBgService.addListener(event: 'taskEvent', listener: Function)`

Écoute les événements émis par les tâches.

**Paramètres:**
- `event: 'taskEvent'` - Type d'événement
- `listener: (data: TaskEventData) => void` - Fonction de callback

**Retour:** `Promise<{remove: () => void}>`

**Interface:**
```typescript
interface TaskEventData {
  taskId: string;      // ID de la tâche qui a émis l'événement
  data: any;           // Données envoyées par la tâche
  timestamp: number;   // Timestamp de l'événement
}
```

**Exemple:**
```typescript
const listener = await ThunderBgService.addListener('taskEvent', (data) => {
  console.log('Événement:', data.taskId, data.data);
});

// Supprimer le listener plus tard
listener.remove();
```

---

### `ThunderBgService.removeAllListeners()`

Supprime tous les listeners enregistrés.

**Retour:** `Promise<void>`

**Exemple:**
```typescript
await ThunderBgService.removeAllListeners();
```

---

## API Java - ThunderBgServiceHelper

Classe publique pour utiliser le plugin depuis le code Java natif.

### Imports requis

```java
import com.webify.thunderbgservice.core.ThunderBgServiceHelper;
```

---

### `startService(Context, String, String, boolean)`

Démarre le service avec les paramètres de base.

**Signature:**
```java
public static void startService(
    Context context,
    String title,
    String subtitle,
    boolean enableLocation
)
```

**Exemple:**
```java
ThunderBgServiceHelper.startService(
    context,
    "Online",
    "Service actif",
    true
);
```

---

### `startService(Context, String, String, boolean, String, String, String, String)`

Démarre le service avec un layout personnalisé.

**Signature:**
```java
public static void startService(
    Context context,
    String title,
    String subtitle,
    boolean enableLocation,
    String customLayout,
    String titleViewId,
    String subtitleViewId,
    String timerViewId
)
```

**Exemple:**
```java
ThunderBgServiceHelper.startService(
    context,
    "Online",
    "Service actif",
    true,
    "notification_custom",
    "txtTitle",
    "txtSubtitle",
    "txtTimer"
);
```

---

### `startService(Context, String, String, boolean, boolean, String, String, String, String)`

Démarre le service avec tous les paramètres.

**Signature:**
```java
public static void startService(
    Context context,
    String title,
    String subtitle,
    boolean enableLocation,
    boolean soundsEnabled,
    String customLayout,
    String titleViewId,
    String subtitleViewId,
    String timerViewId
)
```

---

### `updateNotification(Context, String, String)`

Met à jour la notification (simple).

**Signature:**
```java
public static void updateNotification(
    Context context,
    String title,
    String subtitle
)
```

---

### `updateNotification(Context, String, String, String, String, String, String)`

Met à jour la notification avec changement de layout.

**Signature:**
```java
public static void updateNotification(
    Context context,
    String title,
    String subtitle,
    String customLayout,
    String titleViewId,
    String subtitleViewId,
    String timerViewId
)
```

---

### `stopService(Context)`

Arrête le service.

**Signature:**
```java
public static void stopService(Context context)
```

---

### `registerTask(Context, String, BackgroundTask, long)`

Enregistre une tâche avec une instance.

**Signature:**
```java
public static boolean registerTask(
    Context context,
    String taskId,
    BackgroundTask task,
    long intervalMs
)
```

**Retour:** `true` si enregistré avec succès

---

### `registerTask(Context, String, String, long)`

Enregistre une tâche par nom de classe.

**Signature:**
```java
public static boolean registerTask(
    Context context,
    String taskId,
    String taskClassName,
    long intervalMs
)
```

**Retour:** `true` si enregistré avec succès

---

### `unregisterTask(Context, String)`

Désenregistre une tâche.

**Signature:**
```java
public static boolean unregisterTask(Context context, String taskId)
```

---

### `isTaskRegistered(String)`

Vérifie si une tâche est enregistrée.

**Signature:**
```java
public static boolean isTaskRegistered(String taskId)
```

---

### `getTaskResult(Context, String)`

Récupère les résultats d'une tâche.

**Signature:**
```java
public static JSONObject getTaskResult(Context context, String taskId)
```

**Retour:** `JSONObject` ou `null`

---

### `emitTaskEvent(Context, String, Object)`

Émet un événement vers JS.

**Signature:**
```java
public static void emitTaskEvent(Context context, String taskId, Object data)
```

---

### `layoutExists(Context, String)`

Vérifie si un layout existe.

**Signature:**
```java
public static boolean layoutExists(Context context, String layoutName)
```

---

### `viewIdExists(Context, String)`

Vérifie si un ID de vue existe.

**Signature:**
```java
public static boolean viewIdExists(Context context, String viewIdName)
```

---

## API Java - BackgroundTask

Interface pour créer des tâches en arrière-plan.

### Imports requis

```java
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.tasks.TaskResultStorage;
import com.webify.thunderbgservice.tasks.TaskEventEmitter;
```

---

### `execute(Context, String)`

Méthode principale appelée périodiquement.

**Signature:**
```java
void execute(Context context, String taskId)
```

**Exemple:**
```java
@Override
public void execute(Context context, String taskId) {
    // Votre code ici
}
```

---

### `onRegistered(Context, String)`

Appelée quand la tâche est enregistrée (optionnel).

**Signature:**
```java
default void onRegistered(Context context, String taskId)
```

**Exemple:**
```java
@Override
public void onRegistered(Context context, String taskId) {
    // Initialisation
}
```

---

### `onUnregistered(Context, String)`

Appelée quand la tâche est désenregistrée (optionnel).

**Signature:**
```java
default void onUnregistered(Context context, String taskId)
```

**Exemple:**
```java
@Override
public void onUnregistered(Context context, String taskId) {
    // Nettoyage
}
```

---

## API Java - Helpers

### TaskResultStorage

Stockage de résultats pour récupération par JS.

#### `saveResult(Context, String, String, String)`

Sauvegarde un résultat simple.

```java
TaskResultStorage.saveResult(context, taskId, "key", "value");
```

#### `saveResult(Context, String, JSONObject)`

Sauvegarde un objet JSON complet.

```java
JSONObject data = new JSONObject();
data.put("key", "value");
TaskResultStorage.saveResult(context, taskId, data);
```

#### `getResult(Context, String)`

Récupère un résultat.

```java
JSONObject result = TaskResultStorage.getResult(context, taskId);
```

#### `clearResult(Context, String)`

Supprime un résultat.

```java
TaskResultStorage.clearResult(context, taskId);
```

#### `clearAll(Context)`

Supprime tous les résultats.

```java
TaskResultStorage.clearAll(context);
```

---

### TaskEventEmitter

Émission d'événements vers JS.

#### `emit(Context, String, Object)`

Émet un événement avec un objet.

```java
TaskEventEmitter.emit(context, taskId, "Données");
```

#### `emit(Context, String, JSONObject)`

Émet un événement avec un JSONObject.

```java
JSONObject data = new JSONObject();
data.put("key", "value");
TaskEventEmitter.emit(context, taskId, data);
```

---

## Types et interfaces

### TypeScript

```typescript
interface StartOptions {
  notificationTitle: string;
  notificationSubtitle?: string;
  enableLocation?: boolean;
  soundsEnabled?: boolean;
  customLayout?: string;
  titleViewId?: string;
  subtitleViewId?: string;
  timerViewId?: string;
}

interface RegisterTaskOptions {
  taskId: string;
  taskClass: string;
  intervalMs: number;
}

interface TaskEventData {
  taskId: string;
  data: any;
  timestamp: number;
}
```

### Java - Constantes

```java
// FgConstants
CHANNEL_ID_FOREGROUND
NOTIFICATION_ID_FOREGROUND
ACTION_START
ACTION_STOP
ACTION_UPDATE
ACTION_REGISTER_TASK
ACTION_UNREGISTER_TASK
EXTRA_TITLE
EXTRA_SUBTITLE
EXTRA_ENABLE_LOCATION
EXTRA_SOUNDS
EXTRA_CUSTOM_LAYOUT
EXTRA_TITLE_VIEW_ID
EXTRA_SUBTITLE_VIEW_ID
EXTRA_TIMER_VIEW_ID
EXTRA_TASK_ID
EXTRA_TASK_CLASS
EXTRA_TASK_INTERVAL
```

---

## 🔗 Voir aussi

- [README principal](../README.md)
- [Guide de démarrage rapide](./QUICK_START.md)
- [Cas d'usage](./USE_CASES.md)
- [Exemples](../examples/)

