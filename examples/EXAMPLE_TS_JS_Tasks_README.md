# Communication entre tâches Java et JS/TS

## ⚠️ Limitations importantes

**Vous ne pouvez PAS envoyer directement une fonction TypeScript/JavaScript qui s'exécute en arrière-plan quand l'app est fermée.**

### Pourquoi ?

1. **Android ne peut pas exécuter du JavaScript sans le runtime JS actif**
2. **Le bridge Capacitor ne fonctionne pas quand l'app est fermée**
3. **Les fonctions JS/TS nécessitent le process de l'app, qui peut être tué**

## ✅ Solution hybride

Le plugin offre un système hybride qui permet la communication entre tâches Java et JS/TS :

### 1. Tâches Java (s'exécutent même si l'app est fermée)

Les tâches doivent être en **Java natif** et implémenter `BackgroundTask` :

```java
public class MyTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // S'exécute même si l'app est fermée !
    }
}
```

### 2. Communication avec JS/TS

Deux méthodes de communication :

#### A. Événements en temps réel (si l'app est active)

Si l'app est **active**, les tâches Java peuvent émettre des événements vers JS :

```java
// Dans votre tâche Java
TaskEventEmitter.emit(context, taskId, "Données");
```

```typescript
// Dans votre code JS/TS
ThunderBgService.addListener('taskEvent', (data) => {
  console.log('Événement reçu:', data);
});
```

#### B. Stockage de résultats (même si l'app est fermée)

Si l'app est **fermée**, les données sont stockées et peuvent être récupérées plus tard :

```java
// Dans votre tâche Java (même si l'app est fermée)
TaskResultStorage.saveResult(context, taskId, "key", "value");
```

```typescript
// Dans votre code JS/TS (quand l'app revient)
const { result } = await ThunderBgService.getTaskResult('myTask');
console.log('Résultats:', result);
```

## Utilisation complète

### Étape 1: Créer la tâche Java

```java
package com.yourpackage;

import android.content.Context;
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.tasks.TaskResultStorage;
import com.webify.thunderbgservice.tasks.TaskEventEmitter;

public class MyTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // 1. Stocker des données (fonctionne même si l'app est fermée)
        TaskResultStorage.saveResult(context, taskId, "counter", "123");
        
        // 2. Émettre un événement (seulement si l'app est active)
        TaskEventEmitter.emit(context, taskId, "Données en temps réel");
    }
}
```

### Étape 2: Enregistrer la tâche

```typescript
// Démarrer le service
await ThunderBgService.start({
  notificationTitle: 'Online',
  notificationSubtitle: 'Service actif',
  enableLocation: true,
});

// Enregistrer la tâche
await ThunderBgService.registerTask({
  taskId: 'myTask',
  taskClass: 'com.yourpackage.MyTask',
  intervalMs: 5000,
});
```

### Étape 3: Écouter les événements (si l'app est active)

```typescript
ThunderBgService.addListener('taskEvent', (data) => {
  console.log('Événement reçu:', data.taskId, data.data);
});
```

### Étape 4: Récupérer les résultats (même si l'app était fermée)

```typescript
// Polling périodique
setInterval(async () => {
  const { result } = await ThunderBgService.getTaskResult('myTask');
  if (result) {
    console.log('Résultats:', result);
  }
}, 10000);
```

## Comparaison

| Méthode | App active | App fermée | Usage |
|---------|-----------|-----------|-------|
| **Événements** (`TaskEventEmitter`) | ✅ Oui | ❌ Non | Temps réel quand l'app est active |
| **Stockage** (`TaskResultStorage`) | ✅ Oui | ✅ Oui | Récupération différée |

## Exemples

- `EXAMPLE_TaskWithJSCommunication.java` - Tâche Java avec communication
- `EXAMPLE_TaskJSCommunication.ts` - Code JS/TS pour écouter et récupérer

## Workflow recommandé

1. **Tâche Java** : Exécute votre logique en arrière-plan
2. **Stockage** : Stocke toujours les résultats dans `TaskResultStorage`
3. **Événements** : Émet des événements si l'app est active (bonus)
4. **JS/TS** : Écoute les événements OU récupère les résultats stockés

## Résumé

- ❌ **Pas de fonctions TS/JS directes** en arrière-plan
- ✅ **Tâches Java** qui s'exécutent même si l'app est fermée
- ✅ **Communication** via événements (app active) + stockage (app fermée)
- ✅ **Récupération** des résultats stockés quand l'app revient

