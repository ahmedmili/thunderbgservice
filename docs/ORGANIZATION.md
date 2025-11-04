# Organisation du Plugin

Le plugin est organisé en dossiers logiques pour une meilleure maintenabilité.

## Structure des dossiers

### `android/src/main/java/com/webify/thunderbgservice/`

#### **`core/`** - Fichiers principaux du plugin
- `FgConstants.java` - Constantes du service foreground
- `ForegroundTaskService.java` - Service Android principal
- `ThunderBgServicePlugin.java` - Plugin Capacitor principal
- `ThunderBgServiceHelper.java` - Helper publique pour l'app hôte

#### **`tasks/`** - Gestion des tâches en arrière-plan
- `BackgroundTask.java` - Interface pour les tâches personnalisées
- `BackgroundTaskManager.java` - Gestionnaire des tâches
- `TaskEventEmitter.java` - Émission d'événements vers JS
- `TaskResultStorage.java` - Stockage des résultats pour JS

#### **`helpers/`** - Classes utilitaires
- `NotificationHelper.java` - Gestion des notifications
- `LocationHelper.java` - Gestion de la localisation

### `examples/` - Exemples d'utilisation
- Tous les fichiers d'exemple Java, TypeScript, XML et documentation
- Voir `examples/README.md` pour la liste complète

### `docs/` - Documentation
- Documentation supplémentaire et guides

## Imports

### Depuis votre app

**Tâches en arrière-plan:**
```java
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.tasks.BackgroundTaskManager;
import com.webify.thunderbgservice.tasks.TaskResultStorage;
import com.webify.thunderbgservice.tasks.TaskEventEmitter;
```

**Helpers:**
```java
import com.webify.thunderbgservice.helpers.NotificationHelper;
import com.webify.thunderbgservice.helpers.LocationHelper;
```

**Core (service principal):**
```java
import com.webify.thunderbgservice.core.ThunderBgServiceHelper;
import com.webify.thunderbgservice.core.FgConstants;
import com.webify.thunderbgservice.core.ForegroundTaskService;
```

## Avantages de cette organisation

1. **Séparation des responsabilités** - Chaque groupe a sa propre fonction
2. **Maintenabilité** - Plus facile de trouver et modifier le code
3. **Clarté** - Structure claire pour les développeurs
4. **Évolutivité** - Facile d'ajouter de nouvelles fonctionnalités

