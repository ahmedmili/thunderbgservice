# 🏗️ Architecture et organisation

Structure et organisation du code dans `@ahmed-mili/capacitor-thunder-bg-service`.

## 📁 Structure du package

```
thunder-bg-service/
├── android/
│   └── src/main/java/com/ahmedmili/thunderbgservice/
│       ├── core/                           # Fichiers principaux
│       │   ├── FgConstants.java            # Constantes
│       │   ├── ForegroundTaskService.java  # Service Android
│       │   ├── ThunderBgServicePlugin.java # Plugin Capacitor
│       │   └── ThunderBgServiceHelper.java # Helper publique
│       ├── helpers/                        # Classes helper
│       │   └── NotificationHelper.java     # Gestion des notifications
│       └── tasks/                          # Tâches en arrière-plan
│           ├── BackgroundTask.java         # Interface
│           ├── BackgroundTaskManager.java  # Gestionnaire
│           ├── TaskEventEmitter.java      # Émission d'événements
│           └── TaskResultStorage.java      # Stockage des résultats
├── src/
│   └── definitions.ts                     # Types TypeScript
├── examples/                               # Exemples
│   ├── EXAMPLE_usage.ts
│   ├── EXAMPLE_Stepper.ts
│   ├── EXAMPLE_BackgroundTask_usage.ts
│   └── NotificationDynamicHelper.java
├── docs/                                   # Documentation
│   ├── README.md
│   ├── QUICK_START.md
│   ├── API_REFERENCE.md
│   └── USE_CASES.md
└── README.md                               # Documentation principale
```

## 🎯 Packages Java

### Core (`com.ahmedmili.thunderbgservice.core`)

#### `FgConstants`
Constantes utilisées dans tout le plugin.

#### `ForegroundTaskService`
Service Android principal qui gère :
- La notification foreground
- La localisation
- Les tâches en arrière-plan
- La persistance de l'état

#### `ThunderBgServicePlugin`
Plugin Capacitor qui expose les méthodes TypeScript/JavaScript.

#### `ThunderBgServiceHelper`
Classe helper publique pour utilisation depuis le code Java natif de l'app.

### Helpers (`com.ahmedmili.thunderbgservice.helpers`)

#### `NotificationHelper`
Gère la création et la mise à jour des notifications Android :
- Construction des `RemoteViews`
- Application de `viewData` (injection de textes)
- Binding des `buttons` (boutons cliquables)

### Tasks (`com.ahmedmili.thunderbgservice.tasks`)

#### `BackgroundTask`
Interface à implémenter pour créer une tâche en arrière-plan.

#### `BackgroundTaskManager`
Gère l'enregistrement et l'exécution des tâches.

#### `TaskEventEmitter`
Émet des événements vers JavaScript/TypeScript.

#### `TaskResultStorage`
Stocke les résultats des tâches pour récupération ultérieure.

## 📦 Imports requis

### Pour utiliser le plugin depuis TypeScript/JavaScript

```typescript
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';
```

### Pour créer une tâche en arrière-plan (Java)

```java
import com.ahmedmili.thunderbgservice.tasks.BackgroundTask;
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;
```

### Pour utiliser le helper depuis Java natif

```java
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;
```

## 🔄 Flux de données

### Démarrage du service

```
TypeScript/JS
    ↓
ThunderBgServicePlugin.start()
    ↓
ForegroundTaskService.startAction()
    ↓
NotificationHelper.buildNotification()
    ↓
Notification Android
```

### Mise à jour de la notification

```
TypeScript/JS
    ↓
ThunderBgServicePlugin.update()
    ↓
ForegroundTaskService.startAction(ACTION_UPDATE)
    ↓
NotificationHelper.updateNotification()
    ↓
Application de viewData et buttons
    ↓
Notification Android mise à jour
```

### Tâches en arrière-plan

```
TypeScript/JS
    ↓
ThunderBgServicePlugin.registerTask()
    ↓
BackgroundTaskManager.register()
    ↓
ForegroundTaskService exécute périodiquement
    ↓
BackgroundTask.execute()
    ↓
TaskEventEmitter.emit() (si app active)
    ↓
TaskResultStorage.store() (si app inactive)
```

### Boutons cliquables

```
Utilisateur clique sur bouton
    ↓
PendingIntent déclenché
    ↓
BroadcastReceiver de l'app (NotifActionReceiver)
    ↓
Votre logique métier
```

## 🎨 Architecture UI

### UI 100% App-Driven

Le plugin ne contient **aucune UI par défaut**. Toute l'interface est contrôlée par l'application :

1. **Layout XML** : Créé dans `res/layout/` de l'app
2. **viewData** : Injection de textes depuis TypeScript/JS
3. **buttons** : Binding d'actions depuis TypeScript/JS
4. **BroadcastReceiver** : Gestion des clics dans l'app

### Persistance

L'état est automatiquement sauvegardé dans `SharedPreferences` :
- `customLayout`
- `titleViewId`, `subtitleViewId`, `timerViewId`
- `viewDataJson`
- `buttonsJson`
- `enableLocation`, `soundsEnabled`
- `startAtMillis` (pour le timer)

Lors du redémarrage du service, l'état est restauré automatiquement.

## 🔌 Points d'extension

### Pour les développeurs d'applications

1. **Créer des layouts** : Créez vos layouts XML dans `res/layout/`
2. **Créer des tâches** : Implémentez `BackgroundTask` pour vos tâches
3. **Gérer les boutons** : Créez un `BroadcastReceiver` pour gérer les clics
4. **Utiliser le helper** : Utilisez `ThunderBgServiceHelper` depuis Java natif

### Pour les contributeurs

1. **Core** : Logique principale du service
2. **Helpers** : Classes utilitaires pour les notifications
3. **Tasks** : Système de tâches en arrière-plan
4. **Plugin** : Interface Capacitor

## 📚 Avantages de cette organisation

1. **Séparation des responsabilités** : Chaque module a un rôle clair
2. **Extensibilité** : Facile d'ajouter de nouvelles fonctionnalités
3. **Maintenabilité** : Code organisé et documenté
4. **Réutilisabilité** : Helper publique pour utilisation depuis Java natif
5. **Flexibilité** : UI 100% contrôlée par l'app

## 🔗 Voir aussi

- [📘 Guide de démarrage rapide](./QUICK_START.md)
- [📚 Référence API complète](./API_REFERENCE.md)
- [💡 Cas d'usage pratiques](./USE_CASES.md)
- [📖 README principal](../README.md)

